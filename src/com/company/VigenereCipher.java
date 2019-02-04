package com.company;
import java.lang.Math;

public class VigenereCipher {



    // ------------------------------- constructor ------------------------------------
    private int [] key;
    private char mostCommonLetter;
    private CaesarCipher [] cipher;
    private char [] alphabet;

    public VigenereCipher(int[] keyNumber, char letter){
        key = keyNumber;
        mostCommonLetter = letter;
        cipher = new CaesarCipher[keyNumber.length];
        alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (int i=0; i<keyNumber.length; i++){
            cipher[i] = new CaesarCipher(key[i]);
        }
    }


    // ---------------------------- callable methods ------------------------------------

    // return the String original encrypted using the keyword
    public String encrypt(String original){
        String encrypted = new String();

        for (int i=0; i<original.length(); i++){
            encrypted+=(cipher[i % key.length].encrypt(original.substring(i, i+1)));
        }
        return encrypted;
    }

    // return the String decrypted which is the original message decrypted (knowing the encryption key)
    public String decrypt(String encrypted){

        String decrypted = "";

        for (int i=0; i< encrypted.length(); i++){
            CaesarCipher thisCipher = cipher[i % key.length];
            decrypted += thisCipher.decrypt(encrypted.substring(i, i+1));
        }
        return decrypted;
    }


    // -------------------------------- testing --------------------------------------------

    // unit testing of the encrypt method
    public static void testEncryptSimple(){
        String original = "aaa";
        int [] key = new int [2]; key[0]=4; key[1]=3;
        char mostCommonLetter = 'e';

        VigenereCipher vc = new VigenereCipher(key, mostCommonLetter);
        String encrypted = vc.encrypt(original);
        System.out.println(encrypted);
    }

    // unit testing of the decrypt method
    public static void testDecryptSimple(){
        String encrypted = "XNYGI TC. Febxrx. JYNYWX'X bhyxp.  Jynxv BOBRHP, JQFNX, DHHYY";
        int [] key = new int [5]; key[0]=5; key[1]=2; key[2]=20; key[3]=19; key[4]=4;
        //String encrypted = "aa";
        //int [] key = new int[2]; key[0]=5; key[1]=5;
        char mostCommonLetter = 'e';

        VigenereCipher vc = new VigenereCipher(key, mostCommonLetter);
        String decrypted = vc.decrypt(encrypted);
        System.out.println(decrypted);
    }

    // unit testing encrypt & decrypt
    public static void testEncryptAndDecrypt(){
        String original = "this is a test of the encrypt & decrypt method";
        int [] key = new int [3]; key[0]=4; key[1]=2; key[2]=0;
        char mostCommonLetter = 'e';
        System.out.println("Original: " + original);

        VigenereCipher vc = new VigenereCipher(key, mostCommonLetter);
        String encrypted = vc.encrypt(original);
        System.out.println("Encrypted: " + encrypted);

        String decrypted = vc.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
    }

    // unit testing of toString
    public static void testToString() {
        String original = "eeee";
        int[] key = new int[2];
        key[0] = 4;
        key[1] = 3;
        char mostCommonLetter = 'e';

        VigenereCipher vc = new VigenereCipher(key, mostCommonLetter);
        System.out.println("Key: " + vc.toString());
    }

}
