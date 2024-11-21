import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryptionMessage {

    private SecretKey aesKey; // AES key for encryption

    public AESEncryptionMessage(SecretKey aesKey) {
        this.aesKey = aesKey;
    }

    // Method to encrypt a message using AES in EAX mode
    public byte[] encryptMessage(String message) throws Exception {
        // Initialize AES cipher in EAX mode
        Cipher cipher = Cipher.getInstance("AES/EAX/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);

        // Get the nonce (IV) used during encryption
        byte[] nonce = cipher.getIV();

        // Encrypt the message
        byte[] ciphertext = cipher.doFinal(message.getBytes("UTF-8"));

        // Combine nonce and ciphertext for transmission
        byte[] encryptedMessage = new byte[nonce.length + ciphertext.length];
        System.arraycopy(nonce, 0, encryptedMessage, 0, nonce.length);
        System.arraycopy(ciphertext, 0, encryptedMessage, nonce.length, ciphertext.length);

        return encryptedMessage; // Return nonce + ciphertext
    }

    // Main method for testing
    public static void main(String[] args) {
        try {
            // Generate AES key (128 bits)
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey aesKey = keyGen.generateKey();

            AESEncryptionMessage aesEncryption = new AESEncryptionMessage(aesKey);

            // Test message
            String message = "Hello, World!";
            byte[] encryptedMessage = aesEncryption.encryptMessage(message);

            System.out.println("Encrypted Message: " + java.util.Base64.getEncoder().encodeToString(encryptedMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
