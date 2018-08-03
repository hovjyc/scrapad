package org.hovjyc.scrapad.business.scrapers;

import java.util.ArrayList;
import java.util.List;

import org.hovjyc.scrapad.business.IAdScrapListener;
import org.hovjyc.scrapad.common.ScrapadException;
import org.hovjyc.scrapad.model.Ad;

/**
 * The abstract class for scrapers
 */
public abstract class AbstractScraper {
	/** The timeout. */
	protected static final int TIMEOUT = 12000;

	/** The maximal time to wait before opening a link. */
	protected static final int WAIT_TIME = 1000;

	/** List of ad scrap listeners. */
	private List<IAdScrapListener> adScrapListeners;

	/**
	 * Constructor.
	 */
	public AbstractScraper() {
		adScrapListeners = new ArrayList<IAdScrapListener>();
	}

	/**
	 * Add an ad scrap listener
	 * 
	 * @param pAdScrapListener
	 *            The listener
	 */
	public void addAdScrapListener(IAdScrapListener pAdScrapListener) {
		adScrapListeners.add(pAdScrapListener);
	}

	/**
	 * Notify listeners that an ad was scraped.
	 * 
	 * @param pAd
	 *            The ad scraped
	 */
	protected void fireAdScraped(Ad pAd) {
		for (IAdScrapListener lAdScrapListener : adScrapListeners) {
			lAdScrapListener.handleAdScraped(pAd);
		}
	}

	/**
	 * Extracts ads from the site according to the preferences
	 * 
	 * @throws ScrapadException
	 *             Scraping interrupted
	 */
	public abstract void scrap() throws ScrapadException;
}
