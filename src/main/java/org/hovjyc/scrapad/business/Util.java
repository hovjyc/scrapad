package org.hovjyc.scrapad.business;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.google.common.base.CharMatcher;
import com.google.common.primitives.Ints;

/**
 * Util class
 */
public class Util {

	/** Date format for ads older than one day and younger than one year. */
	private static final String DD_MMMMM = "dd MMMMM";

	/** Date format saved in ressources. */
	private static final String DDMMYYYY = "dd/MM/yyyy";

	/** Date format for ads older than one year. */
	private static final String DD_MMMMM_YYYY = "dd MMMMM yyyy";

	/** The different wannonce date formats */
	private static final String[] DATE_FORMATS = { DD_MMMMM, DD_MMMMM_YYYY, DDMMYYYY };

	/** Logger of the class. */
	private static final Logger LOG = Logger.getLogger(Util.class);

	/**
	 * Search keywords in pText.
	 * 
	 * @param pText
	 *            The text to browse.
	 * @param pKeyWords
	 *            The collection of keywords to search in the text.
	 * @return The first word of the collection found in the text if exists, null
	 *         otherwise.
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
		if (pDateStr != null) {
			Pattern lPattern = Pattern.compile("moins de \\d{1,2} heure[s]?");
			Matcher lMatcher = lPattern.matcher(pDateStr);
			if (lMatcher.matches()) {
				Integer lHours = Ints.tryParse(CharMatcher.DIGIT.retainFrom(pDateStr));
				if (lHours != null) {
					return new DateTime().minusHours(lHours).toDate();
				} else {
					LOG.error("Date incorrecte: " + pDateStr);
				}
			}
			Pattern lPattern2 = Pattern.compile("moins de \\d{1,2} minute[s]?");
            Matcher lMatcher2 = lPattern2.matcher(pDateStr);
            if (lMatcher2.matches()) {
                Integer lMinutes = Ints.tryParse(CharMatcher.DIGIT.retainFrom(pDateStr));
                if (lMinutes != null) {
                    return new DateTime().minusMinutes(lMinutes).toDate();
                } else {
                    LOG.error("Date incorrecte: " + pDateStr);
                }
            }
            
			for (String lFormat : DATE_FORMATS) {
				try {
					if (lFormat.equals(DD_MMMMM)) {
						String lDateStr = pDateStr;
						// short date: we specify the current year.
						lDateStr += " " + Calendar.getInstance().get(Calendar.YEAR);
						// The format goes from DD_MMMM to DD_MMMM_YYYY.
						return new SimpleDateFormat(DD_MMMMM_YYYY).parse(lDateStr);
					}
					// 	We test all formats until one works.
					return new SimpleDateFormat(lFormat).parse(pDateStr);
				} catch (ParseException e) {
					// Bad format, continue the loop.
					LOG.debug(pDateStr + " n'est pas du format: " + lFormat);
				}
			}
			try {
				// All wannonce formats does not work. Let's test Gtrouve date format.
				return new SimpleDateFormat(DD_MMMMM_YYYY, Locale.ENGLISH).parse(pDateStr);
			} catch (ParseException e) {
				// Bad format.
				LOG.debug(pDateStr + " n'est pas du format en anglais: " + DD_MMMMM_YYYY);
			}
		}
		LOG.error("Date incorrecte: " + pDateStr);
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
