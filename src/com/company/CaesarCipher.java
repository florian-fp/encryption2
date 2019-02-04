package com.company;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class CaesarCipher {

    // ------------------------------- constructor ------------------------------------
    private char [] alphabet;
    private int key;

    public CaesarCipher (int N) {
        alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        key = N;
    }


    // ---------------------------  helper functions --------------------------------------

    // return a HashMap<original alphabet, shifted alphabet> where the shifted alphabet is shifted by N vs. the original one
    public HashMap<Character, Character> shiftAlphabet(){
        HashMap<Character, Character> originalToShifted = new HashMap<>();
        for (int i=0; i<this.alphabet.length; i++){
            int newIndex = (i + this.alphabet.length - this.key)% this.alphabet.length;
            originalToShifted.put(this.alphabet[i], this.alphabet[newIndex]);
        }
        return originalToShifted;
    }

    // ---------------------------- callable methods ------------------------------------

    // return the encrypted String message with the CaesarCipher class key
    public String encrypt(String original){
        // returns the String original shifted by N in the alphabet backwards
        HashMap<Character, Character> originalToShifted = shiftAlphabet();
        char [] shiftedWord = original.toLowerCase().toCharArray();
        char [] originalWord = original.toLowerCase().toCharArray();

        for (int i=0; i<originalWord.length; i++){
            Character newLetter = originalToShifted.get(originalWord[i]);
            if (newLetter != null){
                //System.out.println("Original: " + originalWord[i] + "- shifted: " + newLetter);
                shiftedWord[i] = newLetter;
            }
            else{
                //System.out.println("Original: " + originalWord[i] + " - shifted: " + originalWord[i]);
                shiftedWord[i] = originalWord[i];
            }
        }
        return String.valueOf(shiftedWord);
    }

    // return the decrypted String message with the CaesarCipher class key
    public String decrypt(String encrypted){
        //System.out.println("Encrypted: " + encrypted);
        CaesarCipher cc = new CaesarCipher(26 - this.key);
        String decrypted = cc.encrypt(encrypted);
        //System.out.println("Decrypted: " + decrypted);
        return decrypted;
    }



    // -------------------------------- testing --------------------------------------------

    public static void testEncryptAndDecrypt(){
        try {
            String encryptedFileName = "/Users/florianfontaine-papion/Google Drive/1 - Learning/Software Development (Coursera)/CaesarCipher/src/com/company/romeo.txt";
            FileReader reader = new FileReader(encryptedFileName);
            BufferedReader bfReader = new BufferedReader(reader);

            String line = bfReader.readLine();
            //String line = "a";
            System.out.println("Original line in file: " + line);

            // encrypt original line
            int key = 3;
            char mostCommonLetter = 'e';
            CaesarCipher cc = new CaesarCipher(key);
            String encrypted = cc.encrypt("Encrypted line: " + line);
            System.out.println("Encrypted: " + encrypted);

            // decrypt encrypted line
            String decrypted = cc.decrypt(encrypted);
            System.out.println("Decrypted: " + decrypted);
        }

        catch (IOException e){
            System.out.println(e);
        }
    }

}

