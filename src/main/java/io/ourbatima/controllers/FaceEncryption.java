package io.ourbatima.controllers;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class FaceEncryption {
    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    // Clé statique sécurisée (à stocker dans un keystore en production)
    private static final SecretKey SECRET_KEY;

    static {
        try {
            byte[] keyBytes = new byte[32]; // 256 bits
            Arrays.fill(keyBytes, (byte) 0x01); // Clé fixe pour les tests
            SECRET_KEY = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Erreur de génération de clé", e);
        }
    }
    public static byte[] encrypt(byte[] data) throws Exception {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv); // Générer un IV aléatoire

        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, new GCMParameterSpec(TAG_LENGTH, iv));

        byte[] encrypted = cipher.doFinal(data);
        byte[] output = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, output, 0, iv.length); // Ajouter l'IV au début
        System.arraycopy(encrypted, 0, output, iv.length, encrypted.length);

        return output;
    }

    public static byte[] decrypt(byte[] encryptedData) throws Exception {
        if (encryptedData.length < IV_LENGTH) {
            throw new IllegalArgumentException("Données chiffrées invalides (trop courtes)");
        }

        byte[] iv = new byte[IV_LENGTH];
        System.arraycopy(encryptedData, 0, iv, 0, iv.length); // Extraire l'IV

        byte[] data = new byte[encryptedData.length - IV_LENGTH];
        System.arraycopy(encryptedData, IV_LENGTH, data, 0, data.length); // Extraire les données chiffrées

        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, new GCMParameterSpec(TAG_LENGTH, iv));

        return cipher.doFinal(data); // Déchiffrer
    }
}