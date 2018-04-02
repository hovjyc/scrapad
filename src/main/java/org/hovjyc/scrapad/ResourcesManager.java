package org.hovjyc.scrapad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

/**
 * La classe de communiquant avec les ressources.
 */
public class ResourcesManager {
	
	/** Instance unique non préinitialisée */
    private static ResourcesManager INSTANCE = null;

	/** Logger de la classe. */
	private static final Logger LOG = Logger.getLogger(ResourcesManager.class);
	
	/** Le dossier contenant toutes les ressources. */
	private static final String RES_DIR = "src/main/resources/";

	/** Mots clefs disqualifiant une annonce */
	private List<String> badKeywords;

	/** Date limite de récupération des annonces */
	private Date date;

	/** Les préférences de genre */
	private List<Gender> genders;

	/** Mots clefs validant une annonces dans TOUS les cas */
	private List<String> goodKeywords;

	/** Liste des annonceurs ignorés */
	private List<String> pseudos;

	/**
	 * Constructor.
	 */
	public ResourcesManager() {

	}

	/**
	 * Retourne la liste des mots clefs ignorés
	 * 
	 * @return La liste des mots clefs ignorés
	 */
	public List<String> getBadKeywords() {
		return badKeywords;
	}

	/**
	 * Retourne la date limite de récupération des annonces.
	 * 
	 * @return La date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Retourne la liste des préférences de genre.
	 * 
	 * @return Liste de genres.
	 */
	public List<Gender> getGenders() {
		return genders;
	}

	/**
	 * Retourne la liste des bon mots clefs.
	 * 
	 * @return La liste de bons mots clés.
	 */
	public List<String> getGoodKeywords() {
		return goodKeywords;
	}
	
	/** Point d'accès pour l'instance unique du singleton */
    public static synchronized ResourcesManager getInstance()
    {           
        if (INSTANCE == null)
        {   INSTANCE = new ResourcesManager(); 
        }
        return INSTANCE;
    }

	/**
	 * Retourne la liste des pseudos ignorés.
	 * 
	 * @return La liste des pseudos ignorés.
	 */
	public List<String> getPseudos() {
		return pseudos;
	}

	/**
	 * Charge les fichiers de ressources
	 */
	public void load() {
		try {
			date = null;
			badKeywords = null;
			goodKeywords = null;
			pseudos = null;
			genders = new ArrayList<Gender>();

			readProperties();
			badKeywords = readFile(new File(RES_DIR + "badKeywords"));
			goodKeywords = readFile(new File(RES_DIR + "goodKeywords"));
			pseudos = readFile(new File(RES_DIR + "pseudos"));

		} catch (FileNotFoundException e) {
			LOG.error(e.getMessage());
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Sauvegarde les informations dans les fichiers de ressources
	 */
	public void save() {

	}

	/**
	 * Lit un fichier texte et retourne une liste de string correspondant à ses lignes
	 * 
	 * @param pFile
	 *            Le fichier à lire
	 * @return le fichier sours forme de liste.
	 * @throws IOException
	 *             Si pFile est null.
	 */
	private List<String> readFile(final File pFile) throws IOException {
		List<String> lList = new ArrayList<String>();
		FileInputStream lFileInputStream = new FileInputStream(pFile);
		BufferedReader lBufferedReader = new BufferedReader(new InputStreamReader(lFileInputStream));

		String lLine = null;
		while ((lLine = lBufferedReader.readLine()) != null) {
			lList.add(lLine);
		}
		lBufferedReader.close();
		return lList;
	}

	/**
	 * Lis scrapad.properties
	 * 
	 * @throws IOException
	 *             si scrapad.properties n'existe pas.
	 */
	private void readProperties() throws IOException {
		FileInputStream lFileInput = new FileInputStream(new File(RES_DIR + "scrapad.properties"));
		Properties lProperties = new Properties();
		lProperties.load(lFileInput);
		lFileInput.close();

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
		for (String lGenderStr : lGendersStr) {
			Gender lGender = Gender.fromString(lGenderStr);
			if (lGender != null) {
				genders.add(lGender);
			}
		}
		if (genders.isEmpty()) {
			LOG.warn("Pas de genre trouvé dans scrapad.properties. "
					+ "Attention à bien orthographier les genres au singulier."
					+ "Le genre 'femme' sera utilisé par défaut.");
			genders.add(Gender.FEMME);
		}
	}
}
