package org.hovjyc.scrapad.business;

import org.hovjyc.scrapad.model.Ad;

/**
 * Interface of the scraped ad listener
 */
public interface IAdScrapListener {

	/**
	 * Handle the actions when an ad is scraped.
	 * 
	 * @param pAd
	 *            The ad scraped
	 */
	public void handleAdScraped(Ad pAd);
}
