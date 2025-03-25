package com.travplan.utils;

import java.security.MessageDigest;

public class HashString {

    public static boolean compare(final String data, final String hashData) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] inputHash = md.digest(data.getBytes());

            StringBuilder sb1 = new StringBuilder();
            for (byte b : inputHash) {
                sb1.append(String.format("%02X", b));
            }
            String hashedInputPassword = sb1.toString();

            String hashedPassword = hashData.substring(1);

            byte[] hash = md.digest(hashedPassword.getBytes());

            StringBuilder sb2 = new StringBuilder();
            for (byte b : hash) {
                sb2.append(String.format("%02X", b));
            }
            String hashedPasswordFromDB = sb2.toString();

            return hashedInputPassword.equals(hashedPasswordFromDB);
        } catch (Exception e) {
            return false;
        }
    }

    public static String hash(final String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] inputHash = md.digest(data.getBytes());

            StringBuilder sb1 = new StringBuilder();
            for (byte b : inputHash) {
                sb1.append(String.format("%02X", b));
            }

            return sb1.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
