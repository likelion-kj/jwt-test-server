package com.test.jwt.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public final class SaltHasher {
	private SaltHasher() {
		
	}
	
	public static String hash(String payload, String salt) {
		return bytesToHex(sha256(payload + salt));
	}
	
    private static byte[] sha256(String msg) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
             md.update(msg.getBytes());
             return md.digest();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            throw new UnkownExcpetion();
        }
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b: bytes) {
          builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
    
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class UnkownExcpetion extends RuntimeException {}
    
    public static void main(String[] args) {
    	System.out.println(hash("1234"+"testId1", "salt1"));
    	System.out.println(hash("1234"+"testId2", "salt2"));
    	System.out.println(hash("1234"+"testId3", "salt3"));
    }

}
