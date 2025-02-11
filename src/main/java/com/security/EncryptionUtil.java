package com.security;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.*;
import java.util.regex.*;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;

    private SecretKey secretKey;
    private byte[] salt;

    public EncryptionUtil() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE);
        this.secretKey = keyGen.generateKey();
        this.salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
    }

    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedData);
    }
}

class PIIDetector {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern SSN_PATTERN = Pattern.compile("\\d{3}-\\d{2}-\\d{4}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");

    public boolean isPII(String data) {
        return isEmail(data) || isSSN(data) || isPhoneNumber(data);
    }

    private boolean isEmail(String data) {
        return EMAIL_PATTERN.matcher(data).matches();
    }

    private boolean isSSN(String data) {
        return SSN_PATTERN.matcher(data).matches();
    }

    private boolean isPhoneNumber(String data) {
        return PHONE_PATTERN.matcher(data).matches();
    }
}

class PasswordManager {
    private Map<String, String> passwordStore;
    private EncryptionUtil encryptionUtil;

    public PasswordManager(EncryptionUtil encryptionUtil) {
        this.passwordStore = new HashMap<>();
        this.encryptionUtil = encryptionUtil;
    }

    public void savePassword(String service, String password) throws Exception {
        String encryptedPassword = encryptionUtil.encrypt(password);
        passwordStore.put(service, encryptedPassword);
    }

    public String retrievePassword(String service) throws Exception {
        String encryptedPassword = passwordStore.get(service);
        if (encryptedPassword == null) {
            throw new IllegalArgumentException("No password found for service: " + service);
        }
        return encryptionUtil.decrypt(encryptedPassword);
    }
}

class PIISecuritySystem {
    private PIIDetector piiDetector;
    private PasswordManager passwordManager;
    private EncryptionUtil encryptionUtil;

    public PIISecuritySystem() throws NoSuchAlgorithmException {
        this.piiDetector = new PIIDetector();
        this.encryptionUtil = new EncryptionUtil();
        this.passwordManager = new PasswordManager(encryptionUtil);
    }

    public boolean monitorInput(String input) {
        if (piiDetector.isPII(input)) {
            return true;
        }
        return false;
    }

    public void savePassword(String service, String password) {
        try {
            passwordManager.savePassword(service, password);
        } catch (Exception e) {
            throw new RuntimeException("Error saving password: " + e.getMessage());
        }
    }

    public String getPassword(String service) {
        try {
            return passwordManager.retrievePassword(service);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving password: " + e.getMessage());
        }
    }
}