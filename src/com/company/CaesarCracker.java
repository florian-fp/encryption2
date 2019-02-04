package com.company;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CaesarCracker {

    // ------------------------------- constructor ------------------------------------

    private char mostCommonLetter;
    private char [] alphabet;

    public CaesarCracker (char letter) {
        mostCommonLetter = letter;
        alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    }


    // ---------------------------  helper functions --------------------------------------

    // return an array counting the frequency of letters in word
    public int [] countLetter(String word, int [] counts) {
        String alphabet = String.valueOf(this.alphabet);
        String newWord = word.toLowerCase().replaceAll("[.,?!]", "");

        for (int i = 0; i < newWord.length(); i++) {
            // increment by 1 the counts at the index of the letter in the alphabet
            int idx = alphabet.indexOf(newWord.charAt(i));

            if (idx != -1) {
                counts[idx] += 1;
            }
        }
        return counts;
    }


    // return the index of the first occurence of the char Letter in the char [] array
    public int indexOfLetter(char letter){
        for (int j=0; j<alphabet.length; j++){
            if (alphabet[j]==letter){
                return j;
            }
        }
        return -1;
    }

    // return index of the maximum value in an array values
    public int indexOfMax(int [] values){
        int maxValue = 0;
        int indexMaxValue = 0;

        for (int i=0; i<values.length; i++){
            if (maxValue<values[i]){
                maxValue = values[i];
                indexMaxValue = i;
            }
        }

        return indexMaxValue;
    }

    // ---------------------------- callable methods ------------------------------------

    // return the encryption key by identifying the most common letter in the encrypted text
    public int getKey(String input){

        // return the encryption key used for this file
        // calculate the letter frequency for all words in the file fileName
        int [] counts = new int[26];

        input = input.replaceAll("[.,?!]", "");
        String [] words = input.split(" ");

        for (int i=0; i<words.length; i++){
            counts = countLetter(words[i], counts);
        }

        // find the index of the letter with the highest frequency
        int indexOfMax = indexOfMax(counts);
        int mostCommonLetterIndex = indexOfLetter(mostCommonLetter);

        int key = (26 - (mostCommonLetterIndex - indexOfMax)) % 26;
        //System.out.println("Index of most common letter in encrypted String: " + indexOfMax + " - Key is: " + key);

        return key;
    }

    // return the decrypted String message by using the getKey() and the decrypt() methods
    public String breakCaesarCypher(String input){
        int calculatedKey = getKey(input);
        System.out.println("Calculated key: " + calculatedKey);
        CaesarCipher cc = new CaesarCipher(calculatedKey);
        String decrypted = cc.decrypt(input);
        return decrypted;
    }


    // -------------------------------- testing --------------------------------------------

    public static void testIndexOfLetter(){
        System.out.println("---Testing IndexOfLetter---");
        char mostCommonLetter = 'h';

        CaesarCracker cc = new CaesarCracker(mostCommonLetter);
        int indexOfLetter = cc.indexOfLetter(mostCommonLetter);
        System.out.println("Index of letter: " + indexOfLetter);
    }

    public static void decodingTest(){
        System.out.println("--- DecodingTest is running ---");
        //String original = "This is a test! eee";
        String fileName = "/Users/florianfontaine-papion/Google Drive/1 - Learning/Software Development (Coursera)/CaesarCipher/src/com/company/originalText.txt";
        try {
            BufferedReader bf = new BufferedReader(new FileReader(fileName));
            String originalText = "";
            String line;
            while ((line = bf.readLine()) != null){
                originalText = originalText + " " + line;
            }
            //System.out.println("Original: " + originalText);

            // encrypt the original message
            int key = 18;
            CaesarCipher cc = new CaesarCipher(key);
            String encrypted = cc.encrypt(originalText);
            //System.out.println("Encrypted: " + encrypted);

            // decrypt the encrytped message by calculating the key
            char mostCommonLetter = 'e';
            CaesarCracker ck = new CaesarCracker(mostCommonLetter);
            String decrypted = ck.breakCaesarCypher(encrypted);
            //System.out.println("Decrypted: " + decrypted);

        }
        catch (FileNotFoundException e) {
            e.printStackTrace(); }
        catch (IOException e) {
            e.printStackTrace(); }

    }

}
