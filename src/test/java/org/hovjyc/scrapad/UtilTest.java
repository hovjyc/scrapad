package org.hovjyc.scrapad;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hovjyc.scrapad.Util;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeFieldType;

import junit.framework.TestCase;

/**
 * Teste la classe Util
 */
public class UtilTest extends TestCase {

    /**
     * Test de containsKeyWord()
     */
    public void testcontainsKeyWord() {
        List<String> lList = new ArrayList<String>();
        lList.add("en couple");
        lList.add("union libre");
        lList.add("mari");
        lList.add("sexfriend");
        assertTrue(Util.containsKeyWord("Jf black 22a cherche sexfriend idf", lList));
        assertTrue(Util.containsKeyWord("Je suis ici pour mettre du gros piment dans ma vie sex, mon mari est trop traintrain mais je ne veux pas le quitter. Je suis joueuse et je veux rencontrer quelqu'un comme moi, qui a des fantasmes à réaliser. On en discute si possible par telephone avant de se rencontrer.\n" + 
                "la discrétion est indispensable", lList));
        assertTrue(Util.containsKeyWord("Femme secteur Asnières vivant en union libre souhaite rencontrer homme ou couple pour partager un vrai bon plan.\n" + 
                "Je suis déjà sortie en club sur Paris et j'ai même fait des plans au bois et dans d'autres lieux insolites.\n" + 
                "Je peux vous appeler ou vous envoyer un sms si vous proposez des plans originaux: parking, cinéma, cabine essayage... etc....", lList));
        assertFalse(Util.containsKeyWord("Je suis une jeune fille Asiatique qui désir faire des belles rencontres avec des hommes courtois et respectueux.", lList));
    }
    
	/**
	 * Test de dateFromString()
	 */
	public void testDateFromString() {
		DateTimeComparator lComparator = DateTimeComparator.getInstance(
		         DateTimeFieldType.minuteOfHour());
		
		assertEquals(0, lComparator.compare(new DateTime().minusHours(1).toDate(), Util.dateFromString("moins de 1 heure")));
		assertEquals(0, lComparator.compare(new DateTime().minusHours(6).toDate(), Util.dateFromString("moins de 6 heures")));
		assertEquals(0, lComparator.compare(new DateTime().minusHours(10).toDate(), Util.dateFromString("moins de 10 heures")));
		assertEquals(0, lComparator.compare(new DateTime().minusHours(14).toDate(), Util.dateFromString("moins de 14 heures")));
		assertEquals(0, lComparator.compare(new DateTime().minusHours(24).toDate(), Util.dateFromString("moins de 24 heures")));
		
		assertEquals(0, lComparator.compare(new DateTime(Calendar.getInstance().get(Calendar.YEAR), 12, 21, 0, 0).toDate(), Util.dateFromString("21 décembre")));
		assertEquals(0, lComparator.compare(new DateTime(Calendar.getInstance().get(Calendar.YEAR), 12, 31, 0, 0).toDate(), Util.dateFromString("31 décembre")));
		assertEquals(0, lComparator.compare(new DateTime(Calendar.getInstance().get(Calendar.YEAR), 1, 1, 0, 0).toDate(), Util.dateFromString("1 janvier")));
		
		assertEquals(0, lComparator.compare(new DateTime(2014, 12, 21, 0, 0).toDate(), Util.dateFromString("21 décembre 2014")));
		assertEquals(0, lComparator.compare(new DateTime(2016, 8, 12, 0, 0).toDate(), Util.dateFromString("12 août 2016")));
		assertEquals(0, lComparator.compare(new DateTime(2015, 2, 14, 0, 0).toDate(), Util.dateFromString("14 février 2015")));
	}

	/**
	 * Test de stringFromDate()
	 */
	public void testStringFromDate() {   
        assertEquals(Util.stringFromDate(new DateTime(Calendar.getInstance().get(Calendar.YEAR), 12, 21, 0, 0).toDate()), "21/12/2018");
        assertEquals(Util.stringFromDate(new DateTime(Calendar.getInstance().get(Calendar.YEAR), 12, 31, 0, 0).toDate()), "31/12/2018");
        assertEquals(Util.stringFromDate(new DateTime(Calendar.getInstance().get(Calendar.YEAR), 1, 1, 0, 0).toDate()), "01/01/2018");
        
        assertEquals(Util.stringFromDate(new DateTime(2014, 12, 21, 0, 0).toDate()), "21/12/2014");
        assertEquals(Util.stringFromDate(new DateTime(2016, 8, 12, 0, 0).toDate()), "12/08/2016");
        assertEquals(Util.stringFromDate(new DateTime(2015, 2, 14, 0, 0).toDate()), "14/02/2015");
	}
}
