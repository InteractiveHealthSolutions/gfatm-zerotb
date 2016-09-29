package com.ihsinformatics.qrgenerator.test;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.ihsinformatics.qrgenerator.NumberGenerator;
import com.ihsinformatics.util.DatabaseUtil;

public class NumberGeneratorTest {

	static NumberGenerator obj;
	static String url = "jdbc:mysql://199.172.1.13:3306/gfatm_dw_test";
	static String dbName = "gfatm_dw_test";
	static String driverName = "com.mysql.jdbc.Driver";
	static String userName = "haris";
	static String password = "haris123*";

	@BeforeClass
	public static void onceExecutedBeforeAll() {
		DatabaseUtil dbUtil = new DatabaseUtil(url, dbName, driverName,
				userName, password);
		try {
			dbUtil.truncateTable("_identifier");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		obj = new NumberGenerator(url, dbName, driverName, userName, password);
	}

	@Test
	@Ignore
	public void testGenerateSerial() {
		List<String> list = obj.generateSerial(2, 1, 10, false, true);
		assertTrue("Unable to generate serial codes of desired range.",
				list.size() == 10);
		Set<String> uniqueSet = new HashSet<String>();
		for (String s : list) {
			uniqueSet.add(s);
		}
		assertTrue("Unable to generate unique serial codes of desired range.",
				list.size() == uniqueSet.size());
	}

	@Test
	@Ignore
	public void testGenerateSerialWithPrefix() {
		NumberGenerator obj = new NumberGenerator(url, dbName, driverName,
				userName, password);
		List<String> list = obj.generateSerial("H01", 3, 1, 500, false, true);
		assertTrue("Unable to generate serial codes of desired range.",
				list.size() == 500);
		assertTrue("Unable to prepend prefix to serial codes.", list.get(0)
				.startsWith("H01"));
		Set<String> uniqueSet = new HashSet<String>();
		for (String s : list) {
			uniqueSet.add(s);
		}
		assertTrue("Unable to generate unique serial codes of desired range.",
				list.size() == uniqueSet.size());
	}

	@Test
	@Ignore
	public void testGenerateSerialWithDate() {
		NumberGenerator obj = new NumberGenerator(url, dbName, driverName,
				userName, password);
		List<String> list = obj.generateSerial(null, 3, 1, 100, new Date(),
				"yyyyMM", false, true);
		assertTrue("Unable to generate serial codes of desired range.",
				list.size() == 100);
		Set<String> uniqueSet = new HashSet<String>();
		for (String s : list) {
			uniqueSet.add(s);
		}
		assertTrue("Unable to generate unique serial codes of desired range.",
				list.size() == uniqueSet.size());
	}

	@Test
	public void testGenerateRandom() throws Exception {
		List<String> list;
		int range = 600;
		list = obj.generateRandom(2, range, true, false, true);
		assertTrue("Unable to generate random codes of desired range.",
				list.size() == range);
	}

	@Test
	@Ignore
	public void testGenerateUniqueRandom() {
		List<String> list;
		try {
			list = obj.generateRandom(2, 300, true, false, true);
			assertTrue("Unable to generate random codes of desired range.",
					list.size() == 300);
			Set<String> uniqueSet = new HashSet<String>();
			for (String s : list) {
				uniqueSet.add(s);
				System.out.println(s);
			}
			assertTrue(
					"Unable to generate unique random codes of desired range.",
					list.size() == uniqueSet.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
