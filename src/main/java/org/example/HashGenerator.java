package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;


public class HashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar test.jar <RollNumber> <PathToJsonFile>");
            System.exit(1);
        }

        String rollNumber = args[0].toLowerCase();
        String jsonFilePath = args[1];

        try {
            // Parse the JSON file
            File jsonFile = new File(jsonFilePath);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonFile);

            // Find the "destination" key in the JSON file
            String destinationValue = findDestinationValue(rootNode);
            if (destinationValue == null) {
                System.err.println("Key 'destination' not found in the JSON file.");
                System.exit(1);
            }

            // Generate an 8-character alphanumeric random string
            String randomString = generateRandomString(8);

            // Concatenate Roll Number, Destination Value, and Random String
            String concatenatedValue = rollNumber + destinationValue + randomString;

            // Generate MD5 Hash
            String md5Hash = generateMD5Hash(concatenatedValue);

            // Print output in the specified format
            System.out.println(md5Hash + ";" + randomString);
        } catch (Exception e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
            System.exit(1);
        }
    }

    private static String findDestinationValue(JsonNode rootNode) {
        Iterator<String> fieldNames = rootNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode childNode = rootNode.get(fieldName);

            if (fieldName.equals("destination")) {
                return childNode.asText();
            }

            if (childNode.isObject()) {
                String result = findDestinationValue(childNode);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            stringBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return stringBuilder.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}