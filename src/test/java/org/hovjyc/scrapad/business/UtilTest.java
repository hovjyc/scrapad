package org.hovjyc.scrapad.business;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.hovjyc.scrapad.business.Util;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeFieldType;

import junit.framework.TestCase;

/**
 * Test Util class.
 */
public class UtilTest extends TestCase {

    /** The current year.*/
    private static final int CURRENT_YEAR = Calendar.getInstance().get(Calendar.YEAR);
    /**
     * Test de containsKeyWord()
     */
    public void testcontainsKeyWord() {
        Set<String> lSet = new HashSet<String>();
        lSet.add("en couple");
        lSet.add("union libre");
        lSet.add("mari");
        lSet.add("sexfriend");
        assertNotNull(Util.containsKeyWord("Jf black 22a cherche sexfriend idf", lSet));
        assertNotNull(Util.containsKeyWord("Je suis ici pour mettre du gros piment dans ma vie sex, mon mari est trop traintrain mais je ne veux pas le quitter. Je suis joueuse et je veux rencontrer quelqu'un comme moi, qui a des fantasmes à réaliser. On en discute si possible par telephone avant de se rencontrer.\n" + 
                "la discrétion est indispensable", lSet));
        assertNotNull(Util.containsKeyWord("Femme secteur Asnières vivant en union libre souhaite rencontrer homme ou couple pour partager un vrai bon plan.\n" + 
                "Je suis déjà sortie en club sur Paris et j'ai même fait des plans au bois et dans d'autres lieux insolites.\n" + 
                "Je peux vous appeler ou vous envoyer un sms si vous proposez des plans originaux: parking, cinéma, cabine essayage... etc....", lSet));
        assertNull(Util.containsKeyWord("Je suis une jeune fille Asiatique qui désir faire des belles rencontres avec des hommes courtois et respectueux.", lSet));
    }
    
	/**
	 * Test dateFromString().
	 */
	public void testDateFromString() {
		DateTimeComparator lComparator = DateTimeComparator.getInstance(
		         DateTimeFieldType.minuteOfHour());
		
		assertEquals(0, lComparator.compare(new DateTime().minusHours(1).toDate(), Util.dateFromString("Il y a 1 heure")));
		assertEquals(0, lComparator.compare(new DateTime().minusMinutes(1).toDate(), Util.dateFromString("Il y a 1 minute")));
		assertEquals(0, lComparator.compare(new DateTime().minusMinutes(11).toDate(), Util.dateFromString("Il y a 11 minutes")));
		assertEquals(0, lComparator.compare(new DateTime().minusHours(6).toDate(), Util.dateFromString("Il y a 6 heures")));
		assertEquals(0, lComparator.compare(new DateTime().minusMinutes(21).toDate(), Util.dateFromString("Il y a 21 minutes")));
		assertEquals(0, lComparator.compare(new DateTime().minusMinutes(1).toDate(), Util.dateFromString("Il y a 1 minute")));
		assertEquals(0, lComparator.compare(new DateTime().minusHours(10).toDate(), Util.dateFromString("Il y a 10 heures")));
		assertEquals(0, lComparator.compare(new DateTime().minusHours(14).toDate(), Util.dateFromString("Il y a 14 heures")));
		assertEquals(0, lComparator.compare(new DateTime().minusHours(24).toDate(), Util.dateFromString("Il y a 24 heures")));
		
		assertEquals(0, lComparator.compare(new DateTime(CURRENT_YEAR, 12, 21, 0, 0).toDate(), Util.dateFromString("21 décembre")));
		assertEquals(0, lComparator.compare(new DateTime(CURRENT_YEAR, 12, 31, 0, 0).toDate(), Util.dateFromString("31 décembre")));
		assertEquals(0, lComparator.compare(new DateTime(CURRENT_YEAR, 1, 1, 0, 0).toDate(), Util.dateFromString("1 janvier")));
		
		assertEquals(0, lComparator.compare(new DateTime(2014, 12, 21, 0, 0).toDate(), Util.dateFromString("21 décembre 2014")));
		assertEquals(0, lComparator.compare(new DateTime(2016, 8, 12, 0, 0).toDate(), Util.dateFromString("12 août 2016")));
		assertEquals(0, lComparator.compare(new DateTime(2015, 2, 14, 0, 0).toDate(), Util.dateFromString("14 février 2015")));
	}
	
	/**
	 * Test isFat().
	 */
	public void testIsFat() {
	    assertTrue(Util.isFat("170 cm", "80 kg"));
	    assertTrue(Util.isFat("160 cm", "65 kg"));
	    assertTrue(Util.isFat("149 cm", "40 kg"));
	    assertTrue(Util.isFat("160 cm", "62 kg"));
	    assertFalse(Util.isFat("160 cm", "60 kg"));
	    assertFalse(Util.isFat("-", "60 kg"));
	    assertFalse(Util.isFat("160 cm", "-"));
	    assertFalse(Util.isFat("-", "-"));
	}

	/**
	 * Test stringFromDate()
	 */
	public void testStringFromDate() {   
        assertEquals(Util.stringFromDate(new DateTime(CURRENT_YEAR, 12, 21, 0, 0).toDate()), "21/12/" + CURRENT_YEAR);
        assertEquals(Util.stringFromDate(new DateTime(CURRENT_YEAR, 12, 31, 0, 0).toDate()), "31/12/" + CURRENT_YEAR);
        assertEquals(Util.stringFromDate(new DateTime(CURRENT_YEAR, 1, 1, 0, 0).toDate()), "01/01/" + CURRENT_YEAR);
        
        assertEquals(Util.stringFromDate(new DateTime(2014, 12, 21, 0, 0).toDate()), "21/12/2014");
        assertEquals(Util.stringFromDate(new DateTime(2016, 8, 12, 0, 0).toDate()), "12/08/2016");
        assertEquals(Util.stringFromDate(new DateTime(2015, 2, 14, 0, 0).toDate()), "14/02/2015");
	}
}
