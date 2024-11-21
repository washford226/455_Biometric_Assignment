import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

public class AESEncryption {

    public static byte[] aesEncrypt(byte[] key, String data) throws Exception {
        // Generate a random Initialization Vector (IV)
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16]; // 16 bytes = 128 bits
        secureRandom.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Create AES cipher instance in CBC mode with PKCS5 padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        // Initialize cipher for encryption
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

        // Encrypt and return IV concatenated with ciphertext
        byte[] ciphertext = cipher.doFinal(data.getBytes("UTF-8"));
        byte[] ivAndCiphertext = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, ivAndCiphertext, 0, iv.length);
        System.arraycopy(ciphertext, 0, ivAndCiphertext, iv.length, ciphertext.length);

        return ivAndCiphertext; // Prepend IV to the ciphertext
    }

    public static void main(String[] args) {
        try {
            // Example usage
            String data = "Hello, World!";
            // Generate a 256-bit AES key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();

            byte[] encryptedData = aesEncrypt(secretKey.getEncoded(), data);
            System.out.println("Encrypted Data: " + Arrays.toString(encryptedData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
