package controllers;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Encoding {

    public static String hashPassword(String password) {
        try {
            // Obtener una instancia de MessageDigest con el algoritmo SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Convertir la contraseña a bytes y aplicar el hash
            byte[] hashedBytes = md.digest(password.getBytes());

            // Convertir los bytes hasheados a una representación hexadecimal manualmente
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                stringBuilder.append(String.format("%02X", b));
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            // Manejar la excepción si el algoritmo no está disponible
            e.printStackTrace();
            return null;
        }
    }

    public synchronized static String encrypt(String plainText, String password) {
        try {
            // Generar una clave secreta basada en la contraseña usando PBKDF2
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), "salt".getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            // Inicializar el cifrado
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // Cifrar el texto
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

            // Convertir los bytes cifrados a una representación base64
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized static String decrypt(String cipherText, String password) {
        try {
            // Generar una clave secreta basada en la contraseña usando PBKDF2
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), "salt".getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            // Inicializar el cifrado para descifrar
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // Decodificar la representación base64 del texto cifrado
            byte[] cipherBytes = Base64.getDecoder().decode(cipherText);

            // Descifrar los bytes
            byte[] decryptedBytes = cipher.doFinal(cipherBytes);

            // Convertir los bytes descifrados a una cadena
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
