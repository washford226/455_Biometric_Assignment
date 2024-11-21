import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class ChatClient4 {

    private RSAPrivateKey privateKey;  // Client's private RSA key

    // Method to decrypt the RSA-encrypted AES key using the client's private RSA key
    public SecretKey decryptRsa(byte[] encryptedKey) throws Exception {
        // Initialize the Cipher instance for RSA with OAEP padding
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        // Decrypt the AES key
        byte[] decryptedAesKey = cipher.doFinal(encryptedKey);

        // Assuming AES key is 16 bytes (AES-128)
        SecretKey aesKey = new SecretKeySpec(decryptedAesKey, "AES");
        return aesKey;
    }

    // Example main method for testing
    public static void main(String[] args) {
        try {
            // Initialize the client with private RSA key and encrypted AES key
            ChatClient4 client = new ChatClient4();
            byte[] encryptedAesKey = Base64.getDecoder().decode("ENCRYPTED_AES_KEY_HERE"); // Use actual encrypted key
            
            // Decrypt the AES key using the client's private RSA key
            SecretKey aesKey = client.decryptRsa(encryptedAesKey);
            System.out.println("Decrypted AES Key: " + Base64.getEncoder().encodeToString(aesKey.getEncoded()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
