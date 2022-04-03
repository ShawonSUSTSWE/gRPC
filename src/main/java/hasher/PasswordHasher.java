package hasher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordHasher {

    static String data = "";
    static String algorithm = "SHA-256";
    static byte[] salt;

    public PasswordHasher(String data, byte salt[]) {
        this.data = data;
        salt = createSalt();
    }

    private byte[] createSalt() {
        byte[] saltBytes = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(saltBytes);
        return saltBytes;
    }

    public static byte[] generateHash () throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        messageDigest.reset();
        messageDigest.update(salt);
        byte[] hashedPassword = messageDigest.digest(data.getBytes());
        return hashedPassword;
    }



}
