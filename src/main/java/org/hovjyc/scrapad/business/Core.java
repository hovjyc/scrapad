package org.hovjyc.scrapad.business;

import org.apache.log4j.Logger;
import org.hovjyc.scrapad.business.scrapers.GtrouveScraper;
import org.hovjyc.scrapad.common.ScrapadException;

/**
 * Main class.
 */
public final class Core {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(Core.class);

    /**
     * Private constructor.
     */
    private Core() {

    }

    /**
     * The main method.
     * @param pArgs
     *            The arguments
     */
    public static void main(final String[] pArgs) {
    	try {
    		LOG.info("Chargement des fichiers de préférences.");
    		ResourcesManager.getInstance().load();
    		LOG.info("Scraping du site wannonce.");
    		//  WannonceScraper lWannonce = new WannonceScraper();
    		//lWannonce.scrap();
    		GtrouveScraper lGtrouve = new GtrouveScraper();
    		lGtrouve.scrap();
    		ResourcesManager.getInstance().save();
    	} catch (ScrapadException e) {
    		LOG.fatal("Scraping interrompu: " + e.getMessage());
    	}
    }
}
