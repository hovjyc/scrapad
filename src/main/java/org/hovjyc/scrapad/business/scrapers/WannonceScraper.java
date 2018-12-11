package org.hovjyc.scrapad.business.scrapers;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;
import org.hovjyc.scrapad.business.ResourcesManager;
import org.hovjyc.scrapad.business.Util;
import org.hovjyc.scrapad.business.enums.GenderEnum;
import org.hovjyc.scrapad.common.IScrapadConstants;
import org.hovjyc.scrapad.common.ScrapadException;
import org.hovjyc.scrapad.model.Ad;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 * The wannonce scraper.
 */
public class WannonceScraper extends AbstractScraper {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(WannonceScraper.class);

    /** URL corresponding to the woman search. */
    private String url_woman = "http://www.wannonce.com/rencontres-adultes-85/?typefilt=loc&pa=fr&localisation=&georayon=50&z3=3&z5=1&fraich=1&zok=1&z2=6935%2C_dynform&zbtn=1&z1=0&num1=2";

    /** URL corresponding to the man search. */
    private String url_man = "http://www.wannonce.com/rencontres-adultes-85/p1.htm?num1=1&text1_1=&num2_1=&num3_1=&num4=&text2=&text1_2=&num2_2=&num3_2=&num5=&text4=";

    /** URL corresponding to the couple search. */
    private String url_couple = "http://www.wannonce.com/rencontres-adultes-85/p1.htm?num1=3&text1_1=&num2_1=&num3_1=&num4=&text2=&text1_2=&num2_2=&num3_2=&num5=&text4=";

    /**
     * Constructor.
     */
    public WannonceScraper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int scrap() throws ScrapadException {
        for (GenderEnum lGender : ResourcesManager.getInstance().getGenders()) {
            switch (lGender) {
            case COUPLE:
                LOG.info("====================");
                LOG.info("Recherche de couples");
                LOG.info("====================");
                return scrap(url_couple, 1, 0);    
            case FEMME:
                LOG.info("====================");
                LOG.info("Recherche de femmes");
                LOG.info("====================");
                return scrap(url_woman, 1, 0);
            case HOMME:
                LOG.info("====================");
                LOG.info("Recherche d'hommes");
                LOG.info("====================");
                return scrap(url_man, 1, 0);
            default:
                LOG.error("Genre inconnu: " + lGender);
                break;
            }
        }
        return 0;
    }

    /**
     * Browses Ads from the URL
     * 
     * @param pURL
     *            The URL from which we get the ads
     * @return The number of ads scraped.
     * 
     * @throws ScrapadException,
     *             IOException Scraping interrompu
     */
    private int scrap(String pURL, int pPage, int pNbAds) throws ScrapadException {
        LOG.info("Formulaire: recherche en île-de-france. Type de petites annonces : Recherches");
        Response lResponse;
        int lNbAds = pNbAds;
        try {
            //TODO
            //String lPaginationURL = pURL.replaceAll("p[0-9]+", "p" + pPage);
            lResponse = Jsoup.connect(pURL).ignoreContentType(true).userAgent(IScrapadConstants.USER_AGENT)
                    .referrer(IScrapadConstants.REFERER).timeout(TIMEOUT).followRedirects(true).execute();
            lResponse = Jsoup.connect("http://www.wannonce.com/rencontres-adultes-85/p2.htm?num1=2").ignoreContentType(true).userAgent(IScrapadConstants.USER_AGENT)
                    .referrer(IScrapadConstants.REFERER).timeout(TIMEOUT).followRedirects(true).execute();
            Document lPage = lResponse.parse();
            // The first ad is not of the same class as the others...
            Elements lFirstAd = lPage.getElementsByClass("bloc_liste2");
            Elements lAds = lPage.getElementsByClass("bloc_liste");
            lAds.addAll(lFirstAd);
            for (Element lAdElt : lAds) {
                LOG.info("-------------------------------");
                LOG.info("Analyse d'une nouvelle annonce.");
                Elements lDateElt = lAdElt.getElementsByClass("liste8");
                // If date < minimal date: END
                if (lDateElt.isEmpty()) {
                    LOG.error("Erreur: Impossible de récupérer la date.");
                } else {
                    Date lDate = Util.dateFromString(lDateElt.text());
                    if (lDate.compareTo(ResourcesManager.getInstance().getDate()) < 0) {
                        // The scraping stops when the parsed ads are too old.
                        LOG.info("fin du parcours");
                        return lNbAds;
                    }
                    // The date is ok.
                    // We get the location.
                    Elements lLocationElt = lAdElt.getElementsByClass("liste10");
                    if (lLocationElt.isEmpty()) {
                        LOG.error("Erreur: Impossible de récupérer la ville.");
                    } else {
                        String lLocation = lLocationElt.text();
                        // Location OK. We get the ad URL and its title.
                        Elements lTitleURLElt = lAdElt.getElementsByClass("lien_fiche");
                        if (lTitleURLElt.isEmpty()) {
                            LOG.error("Erreur: Impossible de récupérer le titre et l'URL.");
                        } else {
                            Elements lTitleElt = lTitleURLElt.get(0).getElementsByTag("b");
                            String lTitle = lTitleElt.text();
                            String lURL = lTitleURLElt.attr("abs:href");
                            // URL and title scraped.
                            Elements lTxtAdElt = lAdElt.getElementsByClass("liste9");
                            String lTxtAd = lTxtAdElt.text();
                            String lBadKeyWord = Util.containsKeyWord(lTxtAd,
                                    ResourcesManager.getInstance().getBadKeywords());
                            String lGoodKeyWord = Util.containsKeyWord(lTxtAd,
                                    ResourcesManager.getInstance().getGoodKeywords());
                            // The good keyword has the priority on the bad keyword.
                            if (lBadKeyWord != null && lGoodKeyWord == null) {
                                LOG.info(
                                        "Annonce '" + lTitle + "' rejetée car contenant le mot: '" + lBadKeyWord + "'");
                            } else {
                                int lPause = new Random().nextInt(WAIT_TIME);
                                LOG.info("Annonce '" + lTitle + "': date, titre, descriptions, ville et URL OK.");
                                LOG.info("date: " + lDate.toString());
                                LOG.info("ville: " + lLocation);
                                LOG.info("URL: " + lURL);
                                LOG.info("Ouverture de l'annonce dans: " + lPause + " ms.");
                                Thread.sleep(lPause);
                                Response lAdResponse = Jsoup.connect(lURL).ignoreContentType(true)
                                        .userAgent(IScrapadConstants.USER_AGENT).referrer(IScrapadConstants.REFERER)
                                        .timeout(TIMEOUT).followRedirects(true).execute();
                                Document lAdPage = lAdResponse.parse();
                                Elements lPseudoElt = lAdPage.getElementsByClass("a_pseudo");
                                if (lPseudoElt.isEmpty()) {
                                    LOG.error("Erreur: Impossible de récupérer le pseudo");
                                } else {
                                    String lPseudo = lPseudoElt.text();
                                    // We continue only if user name is not in the ignored users list.
                                    if (ResourcesManager.getInstance().getPseudos().contains(lPseudo)) {
                                        LOG.info("'" + lPseudo + "' est banni, annonce ignorée.");
                                    } else {
                                        Elements lDescriptionElt = lAdPage.getElementsByClass("txtannonce");
                                        if (lDescriptionElt.isEmpty()) {
                                            LOG.error("Erreur: Impossible de récupérer la description");
                                        } else {
                                            StringBuilder lDescriptionBuilder = new StringBuilder();
                                            // Get only the text node elements to exclude the wannonce comments
                                            for (Element lElement : lDescriptionElt) {
                                                for (Node lNode : lElement.childNodes()) {
                                                    if (lNode instanceof TextNode) {
                                                        lDescriptionBuilder.append(((TextNode) lNode).text());
                                                    }
                                                }
                                            }
                                            String lDescription = lDescriptionBuilder.toString();
                                            String lGoodWord = Util.containsKeyWord(lDescription,
                                                    ResourcesManager.getInstance().getGoodKeywords());
                                            if (lGoodWord != null) {
                                                Ad lAd = new Ad(lTitle, lPseudo, lDescription, lLocation, lDate, lURL);
                                                LOG.info("Mot qualifiant trouvé : '" + lGoodWord
                                                        + "'. Ajout de l'annonce dans la liste.");
                                                fireAdScraped(lAd);
                                                lNbAds++;
                                                if (lNbAds >= ResourcesManager.getInstance().getMaxNbAds() / 2) {
                                                    // Ad too old : end of the scrap.
                                                    LOG.info("Nombre d'annonces affichées : " + lNbAds
                                                            + ". Fin du parcours");
                                                    return lNbAds;
                                                }
                                            } else {
                                                String lBadWord = Util.containsKeyWord(lDescription,
                                                        ResourcesManager.getInstance().getBadKeywords());
                                                if (lBadWord != null) {
                                                    LOG.info("Annonce rejetée car contenant le mot: '" + lBadWord
                                                            + "'. Utilisateur '" + lPseudo + "' banni.");
                                                    ResourcesManager.getInstance().getPseudos().add(lPseudo);
                                                } else if (lAdPage.getElementsByClass("noncom").isEmpty()) {
                                                    LOG.info("Annonce rejetée car numéro présent. Utilisateur '"
                                                            + lPseudo + "' banni.");
                                                    ResourcesManager.getInstance().getPseudos().add(lPseudo);
                                                } else {
                                                    // If the ad does not contains any phone number
                                                    // and does not contains any bad words, we add it.
                                                    Ad lAd = new Ad(lTitle, lPseudo, lDescription, lLocation, lDate,
                                                            lURL);
                                                    LOG.info("Ajout de l'annonce dans la liste.");
                                                    fireAdScraped(lAd);
                                                    lNbAds++;
                                                    if (lNbAds >= ResourcesManager.getInstance().getMaxNbAds()) {
                                                        // Ad too old : end of the scrap.
                                                        LOG.info("Nombre d'annonces affichées : " + lNbAds
                                                                + ". Fin du parcours");
                                                        return lNbAds;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            int lNewPage = pPage + 1;
            LOG.info("Ouverture de la page: " + lNewPage);
            //TODO
            //return scrap(pURL, lNewPage, lNbAds);
            return 1;
        } catch (InterruptedException e) {
            String lMessage = "Thread de pause interrompu lors du parcours des exceptions: " + pURL + ".";
            LOG.error(lMessage + e.getMessage());
            throw new ScrapadException(lMessage);
        } catch (SocketTimeoutException e) {
            String lMessage = "Erreur de type timeout lors du chargement d'une annonce.";
            LOG.error(lMessage + e.getMessage());
            throw new ScrapadException(lMessage);
        } catch (IOException e) {
            String lMessage = "Impossible de se connecter à l'URL: " + pURL;
            LOG.error("Impossible de se connecter à l'URL: " + pURL + e.getMessage());
            throw new ScrapadException(lMessage);
        }
    }
}
