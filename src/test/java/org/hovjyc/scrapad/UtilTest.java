package org.hovjyc.scrapad;

import java.util.Calendar;

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
		//TODO 21 décembre = 21 décembre 2017 et non 21 décembre 1970
		assertEquals(0, lComparator.compare(new DateTime(Calendar.YEAR, 12, 21, 0, 0), Util.dateFromString("21 décembre")));
		assertEquals(0, lComparator.compare(new DateTime(Calendar.YEAR, 12, 31, 0, 0), Util.dateFromString("31 décembre")));
		assertEquals(0, lComparator.compare(new DateTime(Calendar.YEAR, 1, 1, 0, 0), Util.dateFromString("1 janvier")));
		
		assertEquals(0, lComparator.compare(new DateTime(2014, 12, 21, 0, 0), Util.dateFromString("21 décembre 2014")));
		assertEquals(0, lComparator.compare(new DateTime(2016, 8, 12, 0, 0), Util.dateFromString("12 août 2016")));
		assertEquals(0, lComparator.compare(new DateTime(2015, 2, 14, 0, 0), Util.dateFromString("14 février 2015")));
	}

	/**
	 * Test de stringFromDate()
	 */
	public void testStringFromDate() {
		//TODO
	}
}
