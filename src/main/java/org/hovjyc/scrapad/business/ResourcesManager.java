package org.hovjyc.scrapad.business;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.log4j.Logger;
import org.hovjyc.scrapad.business.enums.GenderEnum;
import org.hovjyc.scrapad.business.enums.SiteEnum;
import org.hovjyc.scrapad.common.IScrapadConstants;
import org.joda.time.DateTime;

/**
 * The class communicating with ressources.
 */
public class ResourcesManager {

	/** Instance unique non initialized */
	private static ResourcesManager INSTANCE = null;

	/** Logger of the class. */
	private static final Logger LOG = Logger.getLogger(ResourcesManager.class);

	/** Keywords disqualifying for an ad */
	private Set<String> badKeywords;

	/** Limit date to get an ad. */
	private Date date;

	/** Gender preferences */
	private Set<GenderEnum> genders;

	/** Key words validating an ad in ALL cases */
	private Set<String> goodKeywords;

	/** Maximum number of ads. */
	private int maxNbAds = 25;

	/** All ignored pseudos */
	private Set<String> pseudos;

	/** The site to scrap. */
	private SiteEnum site;

	/**
	 * Constructor.
	 */
	public ResourcesManager() {

	}

	/**
	 * Return the bad keywords set
	 * 
	 * @return The bad keywords set
	 */
	public Set<String> getBadKeywords() {
		return badKeywords;
	}

	/**
	 * Get the date
	 * 
	 * @return The date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Get gender preferences
	 * 
	 * @return Gender set.
	 */
	public Set<GenderEnum> getGenders() {
		return genders;
	}

	/**
	 * Get the good keywords set.
	 * 
	 * @return The good keywords set.
	 */
	public Set<String> getGoodKeywords() {
		return goodKeywords;
	}

	/** The instance */
	public static synchronized ResourcesManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ResourcesManager();
		}
		return INSTANCE;
	}

	/**
	 * Get the maximum number of ads.
	 * 
	 * @return The maximum number of ads.
	 */
	public int getMaxNbAds() {
		return maxNbAds;
	}

	/**
	 * Get the set of ignored pseudos.
	 * 
	 * @return The ignored pseudos set.
	 */
	public Set<String> getPseudos() {
		return pseudos;
	}

	/**
	 * Get the site to scrap
	 * 
	 * @return The site to scrap.
	 */
	public SiteEnum getSite() {
		return site;
	}

	/**
	 * Load resources file
	 */
	public void load() {
		try {
			readProperties();
			loadBadKeywords();
			loadGoodKeywords();
			loadPseudos();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Load the bad keywords from the file
	 */
	public void loadBadKeywords() {
		badKeywords = null;
		try {
			badKeywords = readFile(new File(IScrapadConstants.BAD_KEYWORDS));
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Load the good keywords from the file
	 */
	public void loadGoodKeywords() {
		goodKeywords = null;
		try {
			goodKeywords = readFile(new File(IScrapadConstants.GOOD_KEYWORDS));
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Load the pseudos from the file
	 */
	public void loadPseudos() {
		pseudos = null;
		try {
			pseudos = readFile(new File(IScrapadConstants.PSEUDOS));
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Save information in the resources file
	 */
	public void save() {
		saveSet(pseudos, IScrapadConstants.PSEUDOS);
		saveSet(badKeywords, IScrapadConstants.BAD_KEYWORDS);
		saveSet(goodKeywords, IScrapadConstants.GOOD_KEYWORDS);
	}

	/**
	 * Save the bad keywords in the file
	 */
	public void saveBadKeywords() {
		saveSet(badKeywords, IScrapadConstants.BAD_KEYWORDS);
	}

	/**
	 * Save the good keywords in the file
	 */
	public void saveGoodKeywords() {
		saveSet(goodKeywords, IScrapadConstants.GOOD_KEYWORDS);
	}

	/**
	 * Save the pseudos in the file.
	 */
	public void savePseudos() {
		saveSet(pseudos, IScrapadConstants.PSEUDOS);
	}

	/**
	 * Save the properties
	 * 
	 * @throws IOException
	 */
	public void saveProperties() throws IOException {
		FileOutputStream lFileOutput = new FileOutputStream(new File(IScrapadConstants.RES_DIR + "scrapad.properties"));
		Properties lProperties = new Properties();
		lProperties.setProperty("maxads", String.valueOf(maxNbAds));
		lProperties.setProperty("date", Util.stringFromDate(date));
		StringBuilder lStringBuilder = new StringBuilder();
		for (GenderEnum lGender : genders) {
			if (lStringBuilder.length() > 0) {
				lStringBuilder.append(",");
			}
			lStringBuilder.append(lGender.toString());
		}
		if (lStringBuilder.length() > 0) {
			lProperties.setProperty("gender", lStringBuilder.toString());
		}
		lProperties.setProperty("site", site.toString());
		lProperties.save(lFileOutput, null);
		lFileOutput.close();
	}

	/**
	 * Set the date
	 * 
	 * @param lDate
	 *            The date
	 */
	public void setDate(Date lDate) {
		this.date = lDate;
	}

	/**
	 * Set the maximum number of ads.
	 * 
	 * @param lMaxNbAds
	 *            The maximum number of ads.
	 */
	public void setMaxNbAds(int lMaxNbAds) {
		this.maxNbAds = lMaxNbAds;
	}

	/**
	 * Set the site to scrap.
	 * 
	 * @param site
	 *            The site name
	 */
	public void setSite(SiteEnum site) {
		this.site = site;
	}

	/**
	 * 
	 * Reads a text file and returns a set of string corresponding to its lines
	 * 
	 * @param pFile
	 *            The file to read
	 * @return The file in set representation.
	 * @throws IOException
	 *             If pFile is null.
	 */
	private Set<String> readFile(final File pFile) throws IOException {
		Set<String> lSet = new HashSet<String>();
		FileInputStream lFileInputStream = new FileInputStream(pFile);
		BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(lFileInputStream));

		String lLine = null;
		while ((lLine = lBufferedReader.readLine()) != null) {
			lSet.add(lLine);
		}
		lBufferedReader.close();
		return lSet;
	}

	/**
	 * Reads scrapad.properties
	 * 
	 * @throws IOException
	 *             if scrapad.properties doesn't exist.
	 */
	private void readProperties() throws IOException {
		date = null;
		genders = new HashSet<GenderEnum>();
		FileInputStream lFileInput = new FileInputStream(new File(IScrapadConstants.RES_DIR + "scrapad.properties"));
		Properties lProperties = new Properties();
		lProperties.load(lFileInput);
		lFileInput.close();

		String lMaxAds = lProperties.getProperty("maxads");
		try {
			maxNbAds = Integer.parseInt(lMaxAds);
		} catch (NumberFormatException e) {
			LOG.warn("scrapad.properties: propriété maxads inexistante ou non numérique");
		}
		String lDate = lProperties.getProperty("date");
		// Si la date est nulle, on scrappe par défaut les annonces de moins d'un mois.
		if (lDate == null || lDate.isEmpty()) {
			date = new DateTime().minusMonths(1).toDate();
		} else {
			date = Util.dateFromString(lDate);
		}
		LOG.info("date butoire: " + date);
		Collection<String> lGendersStr = new DefaultListDelimiterHandler(',').split(lProperties.getProperty("gender"),
				true);

		if (lGendersStr != null) {
			for (String lGenderStr : lGendersStr) {
				GenderEnum lGender = GenderEnum.fromString(lGenderStr);
				if (lGender != null) {
					genders.add(lGender);
				}
			}
		}
		site = SiteEnum.fromString(lProperties.getProperty("site"));
		if (site == null) {
			LOG.warn("scrapad.properties: pas de site trouvé. " + "Attention à bien orthographier les site."
					+ "Le site 'wannonce' sera utilisé par défaut.");
			site = SiteEnum.WANNONCE;
		}
		if (genders.isEmpty()) {
			LOG.warn("scrapad.properties: pas de genre trouvé. "
					+ "Attention à bien orthographier les genres au singulier."
					+ "Le genre 'femme' sera utilisé par défaut.");
			genders.add(GenderEnum.FEMME);
		}
	}

	/**
	 * Save the set in a file.
	 * 
	 * @throws IOException
	 *             If the file is inaccessible.
	 */
	private void saveSet(Set<String> pSet, String pFile) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(pFile));
			LOG.info("Sauvegarde dans " + pFile);
			for (String lElement : pSet) {
				writer.write(lElement + "\n");
			}
			writer.close();
		} catch (IOException e) {
			LOG.error("Impossible de sauvegarder: " + e.getMessage());
		}
	}
}
