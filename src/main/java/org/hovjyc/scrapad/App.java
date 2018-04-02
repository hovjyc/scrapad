package org.hovjyc.scrapad;

import org.apache.log4j.Logger;

/**
 * Main class.
 */
public final class App {
    
    /** Logger. */
    private static final Logger LOG = Logger.getLogger(App.class);
    
    /**
     * Private constructor.
     */
    private App() {

    }

    /**
     * The main method.
     * @param pArgs
     *            The arguments
     */
    public static void main(final String[] pArgs) {
        LOG.info("Chargement des fichiers de préférences.");
        ResourcesManager.getInstance().load();
        LOG.info("Scraping du site wannonce.");
        Wannonce lWannonce = new Wannonce();
        lWannonce.scrap();
    }
}
