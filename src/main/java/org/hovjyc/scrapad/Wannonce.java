package org.hovjyc.scrapad;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * The wannonce web site.
 */
public class Wannonce extends AbstractWebSite {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(Wannonce.class);

    /**
     * Constructor.
     */
    public Wannonce() {
        super("http://www.wannonce.com"
                + "/r/fr/ile-de-france-13/rencontres-adultes-85/");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reduceSearchScope(final HtmlPage pPage) {
        LOG.info("Formulaire: recherche en île-de-france");
        LOG.error(pPage.getForms());
      /*  final HtmlForm lForm = (HtmlForm) pPage
                .getByXPath("//*[@id=\"f_filtrer\"]").get(0);
        final HtmlSubmitInput lButton = (HtmlSubmitInput) pPage
                .getByXPath("//*[@id=\"token-input-localisation-input\"]")
                .get(0);
        final HtmlTextInput lTextField = (HtmlTextInput) pPage
                .getByXPath("//*[@id=\"localisation-submit\"]").get(0);
        lTextField.setValueAttribute("île-de-france");
        try {
            final HtmlPage lPage2 = lButton.click();
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }*/
    }

}
