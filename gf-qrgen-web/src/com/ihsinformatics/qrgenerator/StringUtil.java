/*
Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package com.ihsinformatics.qrgenerator;

import java.util.Random;

/**
 * This class contains several methods for String manupulation that are not
 * available in built in libraries
 * 
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class StringUtil {

	public StringUtil() {
	}

	/**
	 * Generates a random string of given length
	 * 
	 * @param length
	 * @param numeric
	 *            : when true, 0-9 will be included in the string
	 * @param alpha
	 *            : when true, A-Z will be included in the string
	 * @param caseSensitive
	 *            : when true, A-Z and a-z will be included in the string
	 * @return
	 */
	public static String randomString(int length, boolean numeric,
			boolean alpha, boolean caseSensitive) {
		String characters = "";
		if (numeric) {
			characters = "0123456789";
		}
		if (alpha) {
			characters += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			if (caseSensitive) {
				characters += "abcdefghijklmnopqrstuvwxyz";
			}
		}
		Random rand = new Random();
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rand.nextInt(characters.length()));
		}
		return new String(text);
	}
}