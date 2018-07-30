package org.hovjyc.scrapad.model;

import java.util.Date;

/**
 * Model class for ad from the website.
 */
public class Ad {
	/** Ad title */
	private String title;

	/** Ad description */
	private String description;

	/** Location */
	private String location;

	/** Ad publication date. */
	private Date date;

	/** Ad owner pseudo. */
	private String pseudo;

	/** Ad URL. */
	private String url;

	/**
	 * Constructor
	 * 
	 * @param pTitle
	 *            The title
	 * @param pPseudo
	 *            The pseudo
	 * @param pDescription
	 *            The description
	 * @param pLocation
	 *            The location
	 * @param pDate
	 *            The date
	 * @param pURL
	 *            The URL
	 */
	public Ad(String pTitle, String pPseudo, String pDescription, String pLocation, Date pDate, String pURL) {
		super();
		this.title = pTitle;
		this.pseudo = pPseudo;
		this.description = pDescription;
		this.location = pLocation;
		this.date = pDate;
		this.url = pURL;
	}

	/**
	 * Get the ad title
	 * 
	 * @return The ad title
	 */
	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
	
	/**
	 * Get the location
	 * 
	 * @return The location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Get the publication date
	 * 
	 * @return The publication date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Get the pseudo
	 * 
	 * @return The pseudo
	 */
	public String getPseudo() {
		return pseudo;
	}

	/**
	 * Get the URL
	 * 
	 * @return The URL
	 */
	public String getUrl() {
		return url;
	}
}
