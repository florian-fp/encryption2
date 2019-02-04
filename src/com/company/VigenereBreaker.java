package com.company;

import java.io.*;
import java.util.*;

public class VigenereBreaker {

    // ------------------------------- constructor ------------------------------------

    private char [] alphabet;
    private String dictionnaryPath = "./src/dictionaries/";

    public VigenereBreaker(){
        alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    }

    // ---------------------------- helper methods ------------------------------------

    // return a slice of the String message
    public String sliceString(String message, int whichSlice, int totalSlices){
        StringBuilder slice = new StringBuilder();
        for (int i=whichSlice;   i<message.length(); i+=totalSlices){
            slice.append(message.substring(i, i+1));
        }
        return String.valueOf(slice);
    }

    // return an int [] of the encryption keys  used for encrypted
    public int [] tryKeyLength(String encrypted, int klength, char mostCommon){
        int [] keyValues = new int [klength];
        String slice = "";
        for (int i=0; i<klength; i++){
            slice = sliceString(encrypted, i, klength);
            CaesarCracker ck = new CaesarCracker(mostCommon);
            keyValues[i] = ck.getKey(slice);
            //System.out.println("tryKeyLength iteration: " + i + ", value: " + keyValues[i] + ", slice: " + slice);
        }

        // TO DO - temporary fix addressing the shift in letter
        int temp = keyValues[0];
        for (int j=0; j<klength-1; j++){
            keyValues[j] = keyValues[j+1];
        }
        keyValues[klength-1] = temp;

        return keyValues;
    }

    // return a String representing the key for the int [] key of this VigenereCipher
    public String toString(int [] keys){
        String keyWord = "";
        for (int i=0; i<keys.length; i++){
            keyWord += alphabet[keys[i]];
        }
        return keyWord;
    }

    // return a dictionnary from a string fileName
    public HashSet<String> buildDictionnary(String fileName) {
        HashSet<String> dictionnary = new HashSet<String>();

        try {
            BufferedReader bf = new BufferedReader(new FileReader((fileName)));

            String line;
            while ((line = bf.readLine()) != null){
                if (!dictionnary.contains(line)){
                    dictionnary.add(line);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace(); }

        return dictionnary;
    }


    // ---------------------------- callable methods ------------------------------------

    // return a hasmap <klength, decrypted text using klength>
    public HashMap<Integer, String> breakVigenereAllKeys(String fileName, char mostCommon){
        HashMap<Integer, String> decrypted =  new HashMap<Integer, String>();

        try {
            // read fileName
            BufferedReader bf = new BufferedReader(new FileReader(fileName));
            String line;
            String encryptedText = "";
            while ((line = bf.readLine()) != null) {
                encryptedText = encryptedText + " " + line;
            }
            System.out.println("Original: " + encryptedText);

            // get the encryption key for each encryption key length
            for (int klength=1; klength<10; klength++){
                int [] keyValues = tryKeyLength(encryptedText, klength, mostCommon);

                // print the key
                for (int i=0; i<klength; i++){
                    //System.out.println("klength: " + klength + " - key: " + i + " Index : "+ keyValues[i]);
                }

                // decrypt the message
                VigenereCipher vc = new VigenereCipher(keyValues, mostCommon);
                String decryptedMessage = vc.decrypt(encryptedText);
                //System.out.println("Decrypted: " + decryptedMessage);

                // store the decrypted message
                decrypted.put(klength, decryptedMessage);
            }
            return decrypted;
        }


        catch (FileNotFoundException e) {
            e.printStackTrace(); }
        catch (IOException e) {
            e.printStackTrace(); }

        return decrypted;
    }

    // return a hashmap <klengh, # of words in a dictionnary>
    public HashMap<Integer, Integer> countWords(HashMap<Integer, String> decrypted, String dictionnaryFileName){

        // retrieve dictionnary
        HashMap<Integer, Integer> wordCount = new HashMap<Integer, Integer>();
        HashSet<String> dictionnary = buildDictionnary(dictionnaryFileName);

        for (Map.Entry<Integer, String> singleDecrypted : decrypted.entrySet()){
            //System.out.println("Klength: " + singleDecrypted.getKey() + " - Decrypted: " + singleDecrypted.getValue());
            String [] decryptedWords = singleDecrypted.getValue().split("\\W");

            int klength = singleDecrypted.getKey();
            wordCount.put(klength, 0);

            for (String word : decryptedWords){
                if (dictionnary.contains(word)){
                    int currCountWords = wordCount.get(klength);
                    wordCount.replace(klength, currCountWords + 1);
                }
            }
        }
        return wordCount;
    }

    // return the Key of the maximum value in a Hashmap<Integer, Integer>
    public Map.Entry<Integer, Integer> getMaxEntry(HashMap<Integer, Integer> wordCount){

        Map.Entry<Integer, Integer> output = new AbstractMap.SimpleEntry<Integer, Integer>(0, 0);
        // maxCounter
        int maxCounter = 0;

        for (Map.Entry<Integer, Integer> entry : wordCount.entrySet()){
            if (entry.getValue()>maxCounter){
                output = entry;
            }
        }
        return output;
    }

    // return the most common character in a language
    public char getMostCommon(HashSet<String> words){

        // build a char counter for a given dictionnary
        HashMap<Character, Integer> charCount = new HashMap<Character, Integer>();
        for (String word : words){
            for (char c : word.toCharArray()){
                if (!charCount.containsKey(c)){
                    charCount.put(c, 1);
                }
                else {
                    charCount.replace(c, charCount.get(c) + 1);
                }
            }
        }

        // get the char with the most occurence
        char mostCommon = '@';
        int maxEntry = 0;
        for (Map.Entry<Character, Integer> entry : charCount.entrySet()){
            if (entry.getValue() > maxEntry){
                maxEntry = entry.getValue();
                mostCommon = entry.getKey();
            }
        }

        return mostCommon;

    }

    // return the 2 values (in a hashmap) <int klength, int countWords> for a given fileName using a given language VigenereBreaker
    public Map.Entry<Integer, Integer> getVigenereKey(String fileName, String dictionnaryFileName, char mostCommon) {

        VigenereBreaker vb = new VigenereBreaker();
        HashMap<Integer, String> decryptedAllKeys = vb.breakVigenereAllKeys(fileName, mostCommon);
        HashMap<Integer, Integer> countWords = vb.countWords(decryptedAllKeys, dictionnaryFileName);
        System.out.println("countWords: " + countWords);

        Map.Entry<Integer, Integer> maxOutput = getMaxEntry(countWords);
        return maxOutput;
    }

    // return the klength and the language of the encrypted message
    public int [] getVigenereKeyMultipleLanguage (String fileName, ArrayList<String> languages) {

        int maxWords = 0;
        int klength = 0;

        int [] output = new int[2];
        output[0] = 0; output[1] = 0;

        String encryptedLanguage = "";

        for (String language : languages){
            System.out.println(" --------- Language: " + language + " -------------");
            String dictionnaryFileName = dictionnaryPath + language;
            char mostCommon = getMostCommon(buildDictionnary(dictionnaryFileName));
            Map.Entry<Integer, Integer> maxOutput = getVigenereKey(fileName, dictionnaryFileName, mostCommon);
            int maxWordCounter = maxOutput.getValue();

            if (maxWordCounter > maxWords){
                klength = maxOutput.getKey();
                maxWords = maxWordCounter;
                encryptedLanguage = language;
            }

            System.out.println("klength for current language: " + maxOutput.getKey() + " - with wordCount: " + maxOutput.getValue());
            System.out.println("overall klength: " + klength + " - language: " + encryptedLanguage);
            System.out.println();
        }

        output[0] = klength;

        System.out.println("-------- Final output -------------");
        System.out.println("klength max: " + klength);
        System.out.println("language:" + encryptedLanguage);

        return output;
    }

    // -------------------------------- testing --------------------------------------------

    // unit testing of sliceString
    public static void testSliceString(){
        int[] key = new int[4];
        key[0] = 17;
        key[1] = 14;
        key[2] = 12;
        key[3] = 4;

        VigenereBreaker vb = new VigenereBreaker();

        String slice = vb.sliceString("abcdefghijklm", 3, 5);
        System.out.println("Slice: " + slice);
    }

    // unit testing  tryKeyLength
    public static void testTryKeyLength(){
        try {
            // read fileName
            String fileName = "./src/com/company/encryptedFlute.txt";
            char mostCommon = 'e';

            BufferedReader bf = new BufferedReader(new FileReader(fileName));
            String line;
            String originalText="";
            while ((line = bf.readLine()) != null){
                originalText = originalText + " " + line;
            }
            System.out.println("Original: " + originalText);

            //
            VigenereBreaker vb = new VigenereBreaker();
            String key = "flute";
            int [] keys = vb.tryKeyLength(originalText, key.length(), mostCommon);

            for (int i=0; i<keys.length; i++){
                System.out.println("Keys " + i + " index are: "+keys[i]);
            }
            System.out.println("Keyword: " + vb.toString(keys));

        }
        catch (FileNotFoundException e) {
            e.printStackTrace(); }
        catch (IOException e) {
            e.printStackTrace(); }
    }

    // unit testing buildDictionnary
    public static void testBuildDictionnary() {
        String language = "French";
        char mostCommon = 'e';

        VigenereBreaker vb = new VigenereBreaker();
        String dictionnaryFileName = vb.dictionnaryPath + language;
        System.out.println(dictionnaryFileName);
        HashSet<String> dictionnary = vb.buildDictionnary(dictionnaryFileName);
        System.out.println(dictionnary);
    }

    // unit testing of getMostCommon
    public static void testGetMostCommon(){
        String language = "Dutch";

        VigenereBreaker vb = new VigenereBreaker();
        String dictionnaryFileName = vb.dictionnaryPath + language;

        HashSet<String> words = vb.buildDictionnary(dictionnaryFileName);
        System.out.println("Built dictionnary: " + dictionnaryFileName);

        char mostCommon = vb.getMostCommon(words);
        System.out.println("Most common: " + mostCommon);

    }

    // unit testing of getVigenereKeySingleLanguage
    public static void testBreakVigenereSingleLangugage(){
        String fileName = "./src/com/company/encryptedFlute.txt";
        String dictionnaryFileName = "./src/dictionaries/English";
        char mostCommon = 'e';

        VigenereBreaker vb = new VigenereBreaker();
        Map.Entry<Integer, Integer> output = vb.getVigenereKey(fileName, dictionnaryFileName, mostCommon);
        System.out.println("klength: " + output.getKey() + " - Words from dictionnary: " + output.getValue());
    }

    // unit testing of getVigenereKeyMultipleLanguage
    public static void testBreakVigenereMultipleLanguage(){
        ArrayList<String> languages = new ArrayList<String>(Arrays.asList("English", "French", "German", "Italian", "Danish", "Dutch"));
        String fileName = "./src/com/company/encryptedFlute.txt";

        VigenereBreaker vb = new VigenereBreaker();
        int [] output = vb.getVigenereKeyMultipleLanguage(fileName, languages);
        //System.out.println("klength:" + output[0]);
    }




}
