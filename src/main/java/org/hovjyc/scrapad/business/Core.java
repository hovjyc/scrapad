package org.hovjyc.scrapad.business;

import org.apache.log4j.Logger;

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
            Scraper lWannonce = new Scraper();
            lWannonce.scrap();
            ResourcesManager.getInstance().save();
        } catch (InterruptedException e) {
            LOG.fatal("Scraping interrompu: " + e.getMessage());
        }
    }
}
