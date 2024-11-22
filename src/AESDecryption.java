import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESDecryption {

    public static String aesDecrypt(byte[] key, byte[] encryptedData) throws Exception {
        // Extract the IV from the start of the encrypted data
        byte[] iv = new byte[16];
        System.arraycopy(encryptedData, 0, iv, 0, 16);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Extract the ciphertext (the remaining bytes after the IV)
        byte[] ciphertext = new byte[encryptedData.length - 16];
        System.arraycopy(encryptedData, 16, ciphertext, 0, ciphertext.length);

        // Create AES cipher instance in CBC mode with PKCS5 padding
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        // Initialize cipher for decryption
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
        System.out.println("Decryption key: " + new String(key, "UTF-8"));

        // Decrypt the ciphertext and return the plaintext as a string
        byte[] plaintextBytes = cipher.doFinal(ciphertext);
        return new String(plaintextBytes, "UTF-8");
    }

    public static void main(String[] args) {
        try {
            // Example usage
            // Assume we have the same AES key and encrypted data as from the encryption example
            byte[] key = {/* AES key as a byte array */};
            byte[] encryptedData = {/* Encrypted data as a byte array */};

            String decryptedData = aesDecrypt(key, encryptedData);
            System.out.println("Decrypted Data: " + decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
