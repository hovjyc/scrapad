package org.hovjyc.scrapad;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Main class.
 */
public final class App {

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
        Wannonce lWannonce = new Wannonce();
        lWannonce.scrap();
    }

    /**
     * Returns a web client.
     * @return a web client.
     */
    public static WebClient getWebClient() {
        // Create the web client.
        WebClient lWebClient = new WebClient(BrowserVersion.EDGE);
        lWebClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        lWebClient.getOptions().setThrowExceptionOnScriptError(false);
        return lWebClient;
    }
}
