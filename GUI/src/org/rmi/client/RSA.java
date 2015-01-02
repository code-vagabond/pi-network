/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rmi.client;

import java.math.BigInteger;

/**
 *
 * @author Viktor
 */
abstract public class RSA {
    /**
	 * Encodes the given String with RSA.
	 * N = 15493675010221197444736090408304038903590709045187
	 * e = 65537
	 * Transforms s to an integer called value
	 * calculates (value ^ e) % N
	 * @param s The string to encode.
	 * @return The encoded string. It has a length of about 50 and consists out of digits
	 */
	public static String encode(String s){
		BigInteger value = new BigInteger("0");
		BigInteger base = new BigInteger("256");
		BigInteger place;
		
		for (int i = 0; i < s.length(); i++){
			place = new BigInteger(String.valueOf((int)s.charAt(i)));
			value = value.multiply(base);
			value = value.add(place);
		}
		value = value.modPow(new BigInteger("65537"), new BigInteger("15493675010221197444736090408304038903590709045187"));
		
		return value.toString();
	}
}
