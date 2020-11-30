package com.turismo.scripts;

import java.security.SecureRandom;

/**
 * Class that have a method for generate a random String
 * 
 * @author Brayan Hernandez
 *
 */
public class RandomString {

	private SecureRandom rnd = new SecureRandom();

	/**
	 * Generate a random string of length = 6
	 * 
	 * @return String with chars between [A-Z] and length 6
	 */
	public String getString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			sb.append((char) (this.rnd.nextInt(25) + 65));
		}
		return sb.toString();
	}
}
