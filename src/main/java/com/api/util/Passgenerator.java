package com.api.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Passgenerator {

	 public static void main(String ...args) {
		    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
			System.out.println(bCryptPasswordEncoder.encode("160006703"));
			
		    }
}
