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
	public void scrap() throws ScrapadException {
		LOG.info("====================");
		LOG.info("Recherche rubrique échangiste.");
		LOG.info("====================");
		scrap(url_echangiste);
		LOG.info("====================");
		LOG.info("Recherche rubrique sans lendemain.");
		LOG.info("====================");
		scrap(url_sans_lendemain);
	}

	/**
	 * Browses Ads from the URL
	 * 
	 * @param pURL
	 *            The URL from which we get the ads
	 * @return The list of ads to contact.
	 * 
	 * @throws ScrapadException
	 *             Scraping interrompu
	 */
	private void scrap(String pURL) throws ScrapadException {
		LOG.info("Formulaire: recherche en île-de-france. Type de petites annonces : Recherches");
		Response lResponse;
		try {
			lResponse = Jsoup.connect(pURL).ignoreContentType(true).userAgent(IScrapadConstants.USER_AGENT)
					.referrer(IScrapadConstants.REFERER).timeout(TIMEOUT).followRedirects(true).execute();
			int lNbAds = 0;
			Document lPage = lResponse.parse();
			Elements lAds = lPage.getElementsByClass("td_titel");
			for(Element lAdElt : lAds) {
				LOG.info("-------------------------------");
				LOG.info("Analyse d'une nouvelle annonce.");
				Elements lDateLocationElt = lAdElt.getElementsByClass("description");
				if (!lDateLocationElt.isEmpty()) {
					// Current td-title correspond to a real ad and not to a sponsorized link.
					String[] lDateLocation = lDateLocationElt.text().split("  |  \n");
					Date lDate = Util.dateFromString(lDateLocation[0]);
					String lLocation = lDateLocation[2];
					// The browse stops if the date is too old or if the max number of ads is reached.
					// There are two links to scrap, so we take half ads of the max for each.
					if (lDate.compareTo(ResourcesManager.getInstance().getDate()) < 0
							|| lNbAds >= ResourcesManager.getInstance().getMaxNbAds() / 2) {
						// Ad too old : end of the scrap.
						LOG.info("fin du parcours");
						return;
					}
					if (lLocation == null || lLocation.isEmpty()) {
						LOG.error("Erreur: Impossible de récupérer la ville.");
					} else {
						Elements lTitleElt = lAdElt.getElementsByTag("a");
						String lTitle = lTitleElt.text();
						String lURL = lTitleElt.attr("abs:href");
						// URL and title scraped.
						String lTxtAd = lAdElt.text();
						String lBadKeyWord = Util.containsKeyWord(lTxtAd,
								ResourcesManager.getInstance().getBadKeywords());
						if (lBadKeyWord != null) {
							LOG.info("Annonce '" + lTitle + "' rejetée car contenant le mot: '" + lBadKeyWord + "'");
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
											Ad lAd = new Ad(lTitle, lPseudo, lDescription, lLocation, lDate, lURL);
											LOG.info("Mot qualifiant trouvé : '" + lGoodWord
													+ "'. Ajout de l'annonce dans la liste.");
											fireAdScraped(lAd);
										} else {
											String lBadWord = Util.containsKeyWord(lDescription,
													ResourcesManager.getInstance().getBadKeywords());
											if (lBadWord != null) {
												LOG.info("Annonce rejetée car contenant le mot: '" + lBadWord
														+ "'. Utilisateur '" + lPseudo + "' banni.");
												ResourcesManager.getInstance().getPseudos().add(lPseudo);
											} else {
												Ad lAd = new Ad(lTitle, lPseudo, lDescription, lLocation, lDate, lURL);
												LOG.info("Ajout de l'annonce dans la liste.");
												fireAdScraped(lAd);
											}
										}
									}
								}
							}

						}
					}
				}
				lNbAds++;
			}
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
