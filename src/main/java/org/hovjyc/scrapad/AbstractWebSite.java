package org.hovjyc.scrapad;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * A website.
 */
public abstract class AbstractWebSite {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(AbstractWebSite.class);

    /** The potential time to wait before javascript loading. */
    private static final int JAVASCRIPT_LOAD_TIME = 5000;

    /** The site url. */
    private String url;

    /**
     * Constructor.
     * @param pUrl
     *            The site url
     */
    public AbstractWebSite(final String pUrl) {
        url = pUrl;
    }

    /**
     * Connect.
     */
    public void scrap() {

        WebClient lWebClient = App.getWebClient();
        try {

            final HtmlPage lPage = lWebClient.getPage(url);
            lWebClient.waitForBackgroundJavaScriptStartingBefore(
                    JAVASCRIPT_LOAD_TIME);
            LOG.info("Connecté à la page: " + lPage.getUrl());

            String lFileName = "html/file.html";
            File lFile = new File(lFileName);
            if (lFile.exists()) {
                LOG.info("Ecrasement du fichier existant");
                lFile.delete();
            }
            LOG.info("Sauvegarde du fichier html");
            lPage.save(lFile);

            reduceSearchScope(lPage);
            lWebClient.close();
        } catch (IOException e) {
            LOG.warn("Erreur: " + e.getMessage());
            scrap();
        }
    }

    /**
     * Reduce the scope for a more targeted search.
     * @param pPage
     *            The page
     */
    protected abstract void reduceSearchScope(HtmlPage pPage);
}
