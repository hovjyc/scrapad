package org.hovjyc.scrapad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * The wannonce web site.
 */
public class Wannonce {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(Wannonce.class);

    /** Le timeout. */
    private static final int TIMEOUT = 12000;

    /** Le temps maximal à attendre avant d'ouvrir un lien. */
    private static final int WAIT_TIME = 3000;

    /** URL corresponding à la recherche de femmes. */
    private String url_woman = "http://www.wannonce.com/rencontres-adultes-85/?typefilt=loc&pa=fr&localisation=&georayon=50&z3=3&z5=1&fraich=1&zok=1&z2=6935%2C_dynform&zbtn=1&z1=0&num1=2";

    /** URL correspondant à la recherche d'hommes. */
    private String url_man = "http://www.wannonce.com/rencontres-adultes-85/?typefilt=loc&pa=fr&localisation=&georayon=50&z3=3&z5=1&fraich=1&zok=1&z2=6935%2C_dynform&zbtn=1&z1=0&num1=1";

    /** URL correspondant à la recherche de couples. */
    private String url_couple = "http://www.wannonce.com/rencontres-adultes-85/?typefilt=loc&pa=fr&localisation=&georayon=50&z3=3&z5=1&fraich=1&zok=1&z2=6935%2C_dynform&zbtn=1&z1=0&num1=3";

    /**
     * Liste d'annonces correspondant aux critères et pouvant donc être contactées.
     */
    private List<Ad> adsToContact = new ArrayList<Ad>();

    /**
     * Constructor.
     */
    public Wannonce() {
    }

    /**
     * Extrait les annonces du site en fonction des préférences
     * 
     * @throws InterruptedException
     *             Scraping interrompu
     */
    public void scrap() throws InterruptedException {
        for (Gender lGender : ResourcesManager.getInstance().getGenders()) {
            switch (lGender) {
            case COUPLE:
                LOG.info("====================");
                LOG.info("Recherche de couples");
                LOG.info("====================");
                scrap(url_couple);
                break;
            case FEMME:
                LOG.info("====================");
                LOG.info("Recherche de femmes");
                LOG.info("====================");
                scrap(url_woman);
                break;
            case HOMME:
                LOG.info("====================");
                LOG.info("Recherche d'hommes");
                LOG.info("====================");
                scrap(url_man);
                break;
            default:
                LOG.error("Genre inconnu: " + lGender);
                break;
            }
        }
    }

    /**
     * Parcours les petites annonces apparaissant à l'URL donnée.
     * 
     * @param pURL
     *            L'URL de la page contenant les annonces voulues.
     * 
     * @throws InterruptedException
     *             Scraping interrompu
     */
    private void scrap(String pURL) throws InterruptedException {
        LOG.info("Formulaire: recherche en île-de-france. Type de petites annonces : Recherches");
        try {
            Response lResponse = Jsoup.connect(pURL).ignoreContentType(true).userAgent(IScrapadConstants.USER_AGENT)
                    .referrer(IScrapadConstants.REFERER).timeout(TIMEOUT).followRedirects(true).execute();
            Document lPage = lResponse.parse();
            // Le premier élément n'est pas un bloc_list comme les autres...
            Elements lFirstAd = lPage.getElementsByClass("bloc_liste2");
            Elements lAds = lPage.getElementsByClass("bloc_liste");
            lAds.addAll(lFirstAd);
            for (Element lAdElt : lAds) {
                LOG.info("-------------------------------");
                LOG.info("Analyse d'une nouvelle annonce.");
                Elements lDateElt = lAdElt.getElementsByClass("liste8");
                // Si date < date minimale: END
                if (lDateElt.isEmpty()) {
                    LOG.error("Erreur: Impossible de récupérer la date.");
                } else {
                    Date lDate = Util.dateFromString(lDateElt.text());
                    if (lDate.compareTo(ResourcesManager.getInstance().getDate()) < 0) {
                        // Le parcours s'arrête quand l'on commence à parser des annonces trop anciennes
                        LOG.info("fin du parcours");
                        return;
                    }
                    // La date est ok.
                    // On récupère le lieu
                    Elements lCityElt = lAdElt.getElementsByClass("liste10");
                    if (lCityElt.isEmpty()) {
                        LOG.error("Erreur: Impossible de récupérer la ville.");
                    } else {
                        String lCity = lCityElt.text();
                        // Lieu OK. on récupère l'URL de l'annonce et son titre.
                        Elements lTitleURLElt = lAdElt.getElementsByClass("lien_fiche");
                        if (lTitleURLElt.isEmpty()) {
                            LOG.error("Erreur: Impossible de récupérer le titre et l'URL.");
                        } else {
                            Elements lTitleElt = lTitleURLElt.get(0).getElementsByTag("b");
                            String lTitle = lTitleElt.text();
                            String lURL = lTitleURLElt.attr("abs:href");
                            // URL et titre récupérés.
                            Elements lTxtAdElt = lAdElt.getElementsByClass("liste9");
                            String lTxtAd = lTxtAdElt.text();
                            String lBadKeyWord = Util.containsKeyWord(lTxtAd,
                                    ResourcesManager.getInstance().getBadKeywords());
                            if (lBadKeyWord != null) {
                                LOG.info(
                                        "Annonce '" + lTitle + "' rejetée car contenant le mot: '" + lBadKeyWord + "'");
                            } else {
                                int lPause = new Random().nextInt(WAIT_TIME);
                                LOG.info("Annonce '" + lTitle + "': date, titre, descriptions, ville et URL OK.");
                                LOG.info("date: " + lDate.toString());
                                LOG.info("ville: " + lCity);
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
                                    // On continue que si le pseudo de l'annonceur ne fait pas parti des annonceurs
                                    // proscris.
                                    if (ResourcesManager.getInstance().getPseudos().contains(lPseudo)) {
                                        LOG.info("'" + lPseudo + "' est banni, annonce ignorée.");
                                    } else {
                                        Elements lDescriptionElt = lAdPage.getElementsByClass("txtannonce");
                                        if (lDescriptionElt.isEmpty()) {
                                            LOG.error("Erreur: Impossible de récupérer la description");
                                        } else {
                                            String lDescription = lDescriptionElt.text();
                                            String lGoodWord = Util.containsKeyWord(lDescription,
                                                    ResourcesManager.getInstance().getGoodKeywords());
                                            if (lGoodWord != null) {
                                                Ad lAd = new Ad(lTitle, lPseudo, lDescription, lCity, lDate, lURL);
                                                LOG.info("Mot qualifiant trouvé : '" + lGoodWord
                                                        + "'. Ajout de l'annonce dans la liste.");
                                                adsToContact.add(lAd);
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
                                                    // Si le numéro n'est pas communiqué et que l'annonce ne contient
                                                    // pas de mauvais mot, alors elle est potentiellement valide, on
                                                    // l'ajoute.
                                                    Ad lAd = new Ad(lTitle, lPseudo, lDescription, lCity, lDate, lURL);
                                                    LOG.info("Ajout de l'annonce dans la liste.");
                                                    adsToContact.add(lAd);
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
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
