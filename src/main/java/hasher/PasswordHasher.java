package hasher;

import com.google.api.client.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordHasher {

    String algorithm = "SHA-256";
    byte[] salt;
    public String saltString;
    String password = "";

    public PasswordHasher(String password) {
        this.password = password;
    }

    private static byte[] createSalt() {
        byte[] saltBytes = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(saltBytes);
        return saltBytes;
    }

    public String generateHash () throws NoSuchAlgorithmException {
        salt = createSalt();
        saltString = Base64.encodeBase64String(salt);
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        messageDigest.reset();
        messageDigest.update(salt);
        byte[] hashedPassword = messageDigest.digest(password.getBytes());
        return Base64.encodeBase64String(hashedPassword);
    }

    public String generateHash (byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        messageDigest.reset();
        messageDigest.update(salt);
        byte[] hashedPassword = messageDigest.digest(password.getBytes());
        return Base64.encodeBase64String(hashedPassword);
    }



}
