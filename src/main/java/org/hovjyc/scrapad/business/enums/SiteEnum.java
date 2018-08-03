package org.hovjyc.scrapad.business.enums;

/**
 * Site
 */
public enum SiteEnum {
	/** The available sites. */
    WANNONCE("wannonce"), GTROUVE("gtrouve");
	
	/** The string site. */
    private String siteStr = "";

    /**
     * Constructor.
     * @param pSiteStr
     *            The site
     */
    SiteEnum(final String pSiteStr) {
        this.siteStr = pSiteStr;
    }

    /**
     * Get the enum corresponding to the string.
     * @param pSiteStr
     *            The site in String format.
     * @return The site in enum format.
     */
    public static SiteEnum fromString(final String pSiteStr) {
        for (SiteEnum lSite : SiteEnum.values()) {
            if (lSite.siteStr.equalsIgnoreCase(pSiteStr)) {
                return lSite;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return siteStr;
    }
}
