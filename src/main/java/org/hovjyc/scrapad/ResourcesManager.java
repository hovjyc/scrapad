package org.hovjyc.scrapad;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    /** Le fichiers contenants les pseudos bannis. */
    private static final String PSEUDOS_FILE = RES_DIR + "pseudos";

    /** Mots clefs disqualifiant une annonce */
    private Set<String> badKeywords;

    /** Date limite de récupération des annonces */
    private Date date;

    /** Les préférences de genre */
    private Set<Gender> genders;

    /** Mots clefs validant une annonces dans TOUS les cas */
    private Set<String> goodKeywords;

    /** Set des annonceurs ignorés */
    private Set<String> pseudos;

    /**
     * Constructor.
     */
    public ResourcesManager() {

    }

    /**
     * Retourne le set des mots clefs ignorés
     * 
     * @return Le set des mots clefs ignorés
     */
    public Set<String> getBadKeywords() {
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
     * Retourne le set des préférences de genre.
     * 
     * @return Set de genres.
     */
    public Set<Gender> getGenders() {
        return genders;
    }

    /**
     * Retourne le set des bon mots clefs.
     * 
     * @return Le set de bons mots clés.
     */
    public Set<String> getGoodKeywords() {
        return goodKeywords;
    }

    /** Point d'accès pour l'instance unique du singleton */
    public static synchronized ResourcesManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ResourcesManager();
        }
        return INSTANCE;
    }

    /**
     * Retourne le set des pseudos ignorés.
     * 
     * @return Le set des pseudos ignorés.
     */
    public Set<String> getPseudos() {
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
            genders = new HashSet<Gender>();

            readProperties();
            badKeywords = readFile(new File(RES_DIR + "badKeywords"));
            goodKeywords = readFile(new File(RES_DIR + "goodKeywords"));
            pseudos = readFile(new File(PSEUDOS_FILE));

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
        savePseudos();
    }

    /**
     * Lit un fichier texte et retourne un set de string correspondant à ses
     * lignes
     * 
     * @param pFile
     *            Le fichier à lire
     * @return le fichier sous forme de set.
     * @throws IOException
     *             Si pFile est null.
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

    /**
     * Sauvegarde le set de pseudos dans un fichiers.
     * 
     * @throws IOException
     *             Si le fichier est inaccessible.
     */
    private void savePseudos() {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(PSEUDOS_FILE));
            LOG.info("Sauvegarde des pseudos");
            for (String pseudo : pseudos) {
                writer.write(pseudo + "\n");
            }
            writer.close();
        } catch (IOException e) {
            LOG.error("Impossible de sauvegarder les pseudos: " + e.getMessage());
        }
    }
}
