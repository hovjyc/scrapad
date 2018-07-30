package org.hovjyc.scrapad.common;

/**
 * Interface containing the application constants 
 */
public interface IScrapadConstants {
	
	/** Folder containing all ressources. */
    public static final String RES_DIR = "src/main/resources/";
	
	/** File containing the bad keywords. */
	public static final String BAD_KEYWORDS = RES_DIR + "badKeywords";
    
    /** File containing the bad keywords. */
	public static final String GOOD_KEYWORDS = RES_DIR + "goodKeywords";
    
    /** File containing the ignored pseudos. */
	public static final String PSEUDOS = RES_DIR + "pseudos";
    
    /** Referer header. */
    public static final String REFERER = "http://www.google.com";
    
    /** User agent used for each connection. */
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:59.0) Gecko/20100101 Firefox/59.0";
    
}
