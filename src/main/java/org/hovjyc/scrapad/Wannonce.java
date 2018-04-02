package org.hovjyc.scrapad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    /** URL corresponding à la recherche de femmes. */
    private String url_woman = "http://www.wannonce.com/rencontres-adultes-85/?typefilt=loc&pa=fr&localisation=&georayon=50&z3=3&z5=1&fraich=1&zok=1&z2=6935%2C_dynform&zbtn=1&z1=0&num1=2";

    /** URL correspondant à la recherche d'hommes. */
    private String url_man = "http://www.wannonce.com/rencontres-adultes-85/p1.htm?num1=1&text1_1=&num2_1=&num3_1=&num4=&text2=&text1_2=&num2_2=&num3_2=&num5=&text4=";

    /** URL correspondant à la recherche de couples. */
    private String url_couple = "http://www.wannonce.com/rencontres-adultes-85/p1.htm?num1=3&text1_1=&num2_1=&num3_1=&num4=&text2=&text1_2=&num2_2=&num3_2=&num5=&text4=";

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
     */
    public void scrap() {
        for (Gender lGender : ResourcesManager.getInstance().getGenders()) {
            switch (lGender) {
            case COUPLE:
                LOG.info("Recherche de couples");
                scrap(url_couple);
                break;
            case FEMME:
                LOG.info("Recherche de femmes");
                scrap(url_woman);
                break;
            case HOMME:
                LOG.info("Recherche d'hommes");
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
     */
    private void scrap(String pURL) {
        LOG.info("Formulaire: recherche en île-de-france. Type de petites annonces : Recherches");
        try {
            Response lResponse = Jsoup.connect(pURL).ignoreContentType(true).userAgent(IScrapadConstants.USER_AGENT)
                    .referrer(IScrapadConstants.REFERER).timeout(12000).followRedirects(true).execute();
            Document lPage = lResponse.parse();
            Elements lAds = lPage.getElementsByClass("bloc_liste");
            for (Element lAdElt : lAds) {
                Elements lDateElt = lAdElt.getElementsByClass("liste8");
                // Si date < date minimale: END
                if (lDateElt != null) {
                    Date lDate = Util.dateFromString(lDateElt.text());
                    if (lDate.compareTo(ResourcesManager.getInstance().getDate()) >= 0) {
                        // La date est ok.
                        // On récupère le lieu
                        Elements lCityElt = lAdElt.getElementsByClass("d_city");
                        if (lCityElt != null) {
                            String lCity = lCityElt.text();
                            // Lieu OK. on récupère l'URL de l'annonce et son titre.
                            Elements lTitleURLElt = lAdElt.getElementsByClass("lien_fiche");
                            if (lTitleURLElt != null) {
                                Elements lTitleElt = lTitleURLElt.get(0).getElementsByTag("b");
                                String lTitle = lTitleElt.text();
                                String lURL = lTitleURLElt.attr("abs:href");
                                // URL et titre récupéré. On récupère la description.
                                Elements lDescriptionPreviewElt = lAdElt.getElementsByClass("liste9");
                                if (lDescriptionPreviewElt != null) {
                                    // La description contient également le genre et le titre de l'annonce, idéal pour vérifier les mots.
                                    String lDescriptionPreview = lDescriptionPreviewElt.text();
                                    if (Util.containsKeyWord(lDescriptionPreview, ResourcesManager.getInstance().getGoodKeywords())) {
                                        // Si un bon mot clé est présent, alors ouvrir l'annonce n'est pas nécessaire : elle est bonne.
                                        Ad lAd = new Ad(lTitle, lDescriptionPreview, lCity, lDate, lURL);
                                        adsToContact.add(lAd);
                                    } else if (!Util.containsKeyWord(lDescriptionPreview, ResourcesManager.getInstance().getBadKeywords())) {
                                        // TODO Si l'annonce ne contient pas de bons mots, ni de mauvais, alors on l'ouvre pour l'analyser de plus près.
                                    }
                                }
                            }
                        }
                    }
                }
            }
            LOG.info("FINISH");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
