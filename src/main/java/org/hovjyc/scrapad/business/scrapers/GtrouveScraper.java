package org.hovjyc.scrapad.business.scrapers;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Random;

import org.apache.log4j.Logger;
import org.hovjyc.scrapad.business.ResourcesManager;
import org.hovjyc.scrapad.business.Util;
import org.hovjyc.scrapad.common.IScrapadConstants;
import org.hovjyc.scrapad.common.ScrapadException;
import org.hovjyc.scrapad.model.Ad;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 * Scraper for gtrouve site
 */
public class GtrouveScraper extends AbstractScraper {

	/** Logger. */
	private static final Logger LOG = Logger.getLogger(GtrouveScraper.class);

	/** URL corresponding to the sans-lendemain search. */
	private String url_sans_lendemain = "https://www.g-trouve.com/annonces/rencontres/ile-de-france/sans-lendemain/";

	/** URL corresponding to the echangiste search. */
	private String url_echangiste = "https://www.g-trouve.com/annonces/rencontres/ile-de-france/echangiste/";

	/**
	 * Constructor.
	 */
	public GtrouveScraper() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int scrap() throws ScrapadException {
		LOG.info("====================");
		LOG.info("Recherche rubrique échangiste.");
		LOG.info("====================");
		int lNbAds = scrap(url_echangiste, 1, 0);
		LOG.info("====================");
		LOG.info("Recherche rubrique sans lendemain.");
		LOG.info("====================");
		lNbAds += scrap(url_sans_lendemain, 1, lNbAds);
		return lNbAds;
	}

	/**
	 * Browses Ads from the URL
	 * 
	 * @param pURL
	 *            The URL from which we get the ads
	 * @param pPage
     *            The page number
     * @param pNbAds
     *            The number of ads scraped before this scrap
	 * @return The number of ads scraped.
	 * 
	 * @throws ScrapadException
	 *             Scraping error
	 */
	private int scrap(String pURL, int pPage, int pNbAds) throws ScrapadException {
		LOG.info("Formulaire: recherche en île-de-france. Type de petites annonces : Recherches");
		Response lResponse;
		try {
			String lURL = pURL + "p-" + pPage + "/";
			lResponse = Jsoup.connect(lURL).ignoreContentType(true).userAgent(IScrapadConstants.USER_AGENT)
					.referrer(IScrapadConstants.REFERER).timeout(TIMEOUT).followRedirects(true).execute();
			int lNbAds = pNbAds;
			Document lPage = lResponse.parse();
			Elements lAds = lPage.getElementsByClass("td_titel");
			for (Element lAdElt : lAds) {
				LOG.info("-------------------------------");
				LOG.info("Analyse d'une nouvelle annonce.");
				Elements lDateLocationElt = lAdElt.getElementsByClass("description");
				if (!lDateLocationElt.isEmpty()) {
					// Current td-title correspond to a real ad and not to a sponsorized link.
					String[] lDateLocation = lDateLocationElt.text().split("  |  \n");
					Date lDate = Util.dateFromString(lDateLocation[0]);
					String lLocation = lDateLocation[2];
					if (lDate.compareTo(ResourcesManager.getInstance().getDate()) < 0) {
						// Ad too old : end of the scrap.
						LOG.info("Date de la prochaine annonce: " + lDate + ". Fin du parcours");
						return lNbAds;
					}
					if (lLocation == null || lLocation.isEmpty()) {
						LOG.error("Erreur: Impossible de récupérer la ville.");
					} else {
						Elements lTitleElt = lAdElt.getElementsByTag("a");
						String lTitle = lTitleElt.text();
						String lAdURL = lTitleElt.attr("abs:href");
						// URL and title scraped.
						String lTxtAd = lAdElt.text();
						String lBadKeyWord = Util.containsKeyWord(lTxtAd,
								ResourcesManager.getInstance().getBadKeywords());
						String lGoodKeyWord = Util.containsKeyWord(lTxtAd,
                                ResourcesManager.getInstance().getGoodKeywords());
                        // The good keyword has the priority on the bad keyword.
						if (lBadKeyWord != null && lGoodKeyWord == null) {
							LOG.info("Annonce '" + lTitle + "' rejetée car contenant le mot: '" + lBadKeyWord + "'");
						} else {
							int lPause = new Random().nextInt(WAIT_TIME);
							LOG.info("Annonce '" + lTitle + "': date, titre, descriptions, ville et URL OK.");
							LOG.info("date: " + lDate.toString());
							LOG.info("ville: " + lLocation);
							LOG.info("URL: " + lAdURL);
							LOG.info("Ouverture de l'annonce dans: " + lPause + " ms.");
							Thread.sleep(lPause);
							Response lAdResponse = Jsoup.connect(lAdURL).ignoreContentType(true)
									.userAgent(IScrapadConstants.USER_AGENT).referrer(IScrapadConstants.REFERER)
									.timeout(TIMEOUT).followRedirects(true).execute();
							Document lAdPage = lAdResponse.parse();
							// The ad ID is seen as a pseudo.
							Elements lPseudoElt = lAdPage.getElementsContainingOwnText("N°");
							String lPseudo = lPseudoElt.text();
							// We continue only if user name is not in the ignored users list.
							if (ResourcesManager.getInstance().getPseudos().contains(lPseudo)) {
								LOG.info("'" + lPseudo + "' est banni, annonce ignorée.");
							} else {
								Elements lForbidElt = lAdPage.getElementsMatchingText("Modes de paiement acceptés");
								if (!lForbidElt.isEmpty()) {
									LOG.info(
											"Annonce rejetée car contenant un champ 'Modes de paiement acceptés'. Utilisateur '"
													+ lPseudo + "' banni.");
									ResourcesManager.getInstance().getPseudos().add(lPseudo);
								} else {

									Elements lPanelBodyElt = lAdPage.getElementsByClass("panel-body");
									if (lPanelBodyElt.isEmpty()) {
										LOG.error("Erreur: Impossible de récupérer la description");
									} else {
										boolean lAdHasImg = lPanelBodyElt.get(1).getElementsByTag("img").isEmpty();
										Element lDescriptionElt;
										if (lAdHasImg) {
											lDescriptionElt = lPanelBodyElt.get(1);
										} else {
											lDescriptionElt = lPanelBodyElt.get(2);
										}
										StringBuilder lDescriptionBuilder = new StringBuilder();
										for (Node lChild : lDescriptionElt.childNodes()) {
											if (lChild instanceof TextNode) {
												lDescriptionBuilder.append(((TextNode) lChild).text());
											}
										}
										String lDescription = lDescriptionBuilder.toString();
										String lGoodWord = Util.containsKeyWord(lDescription,
												ResourcesManager.getInstance().getGoodKeywords());
										if (lGoodWord != null) {
											Ad lAd = new Ad(lTitle, lPseudo, lDescription, lLocation, lDate, lAdURL);
											LOG.info("Mot qualifiant trouvé : '" + lGoodWord
													+ "'. Ajout de l'annonce dans la liste.");
											fireAdScraped(lAd);
											lNbAds++;
											// if pNbads = 0, no ads were scraped before this function call.
											// / 2 is used because two category of two differents URL
											// are searched in the same time.
											// It permits to have the two genders ads.
											if ((pNbAds == 0
													&& lNbAds >= ResourcesManager.getInstance().getMaxNbAds() / 2)
													|| pNbAds > 0
															&& lNbAds >= ResourcesManager.getInstance().getMaxNbAds()) {
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
											} else {
												Ad lAd = new Ad(lTitle, lPseudo, lDescription, lLocation, lDate,
														lAdURL);
												LOG.info("Ajout de l'annonce dans la liste.");
												fireAdScraped(lAd);
												lNbAds++;
												if (lNbAds >= ResourcesManager.getInstance().getMaxNbAds() / 2) {
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
			int lNewPage = pPage + 1;
			LOG.info("Ouverture de la page: " + lNewPage);
			return scrap(pURL, lNewPage, lNbAds);
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
