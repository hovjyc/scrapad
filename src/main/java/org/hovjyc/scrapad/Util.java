package org.hovjyc.scrapad;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.google.common.base.CharMatcher;
import com.google.common.primitives.Ints;

/**
 * Classe de fonctions utilitaires
 */
public class Util {

    /** Format de date pour les annonces de plus d'un jour et de moins d'un an. */
    private static final String DD_MMMMM = "dd MMMMM";

    /** Format de date enregistré dans les ressources. */
    private static final String DDMMYYYY = "dd/MM/yyyy";

    /** Format de date pour les annonces de plus d'un an. */
    private static final String DD_MMMMM_YYYY = "dd MMMMM yyyy";

    /** The differents wannonce date formats */
    private static final String[] DATE_FORMATS = { DD_MMMMM, DD_MMMMM_YYYY, DDMMYYYY };

    /** Logger of the class. */
    private static final Logger LOG = Logger.getLogger(Util.class);

    /**
     * Cherche les mots clés à dans pText.
     * 
     * @param pText
     *            Le texte à analyser.
     * @param pKeyWords
     *            La collection des mots clés à chercher dans le texte.
     * @return le premier mot de la collection trouvé dans le texte s'il existe, null sinon.
     */
    public static String containsKeyWord(String pText, Collection<String> pKeyWords) {
        for (String lKeyWord : pKeyWords) {
            if (pText.contains(lKeyWord)) {
                return lKeyWord;
            }
        }
        return null;
    }

    /**
     * Convert date from wannonce or dd/MM/yyyy string format to Date object.
     * 
     * @param pDateStr
     *            The date from wannonce or dd/MM/yyyy string format to Date object
     * @return A Date object
     */
    public static Date dateFromString(final String pDateStr) {
        String lDateStr = pDateStr;
        if (lDateStr != null) {
            Pattern lPattern = Pattern.compile("moins de \\d{1,2} heure[s]?");
            Matcher lMatcher = lPattern.matcher(lDateStr);
            if (lMatcher.matches()) {
                Integer lHours = Ints.tryParse(CharMatcher.DIGIT.retainFrom(lDateStr));
                if (lHours != null) {
                    return new DateTime().minusHours(lHours).toDate();
                } else {
                    LOG.error("Date incorrecte: " + lDateStr);
                }
            }
            for (String lFormat : DATE_FORMATS) {
                try {
                    if (lFormat.equals(DD_MMMMM)) {
                        // date courte: on précise l'année courante.
                        lDateStr += " " + Calendar.getInstance().get(Calendar.YEAR);
                        // Le format passe donc de DD_MMMM à DD_MMMM_YYYY.
                        return new SimpleDateFormat(DD_MMMMM_YYYY).parse(lDateStr);
                    }
                    // On teste les différents format jusqu'à ce qu'il y en ai un qui fonctionne.
                    return new SimpleDateFormat(lFormat).parse(lDateStr);
                } catch (ParseException e) {
                    // Mauvais format, on reprend dans la boucle.
                    LOG.debug(lDateStr + " n'est pas du format: " + lFormat);
                }
            }
        }
        LOG.error("Date incorrecte: " + lDateStr);
        return null;
    }

    /**
     * Convert Date to dd/MM/yyyy string format.
     * 
     * @param pDate
     *            The Date object
     * @return a dd/MM/yyyy string date format
     */
    public static String stringFromDate(final Date pDate) {
        if (pDate == null) {
            return null;
        }
        DateFormat lDateFormat = new SimpleDateFormat(DDMMYYYY);
        String lDate = lDateFormat.format(pDate);
        return lDate;
    }
}
