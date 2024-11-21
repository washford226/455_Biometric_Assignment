import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;

public class ChatServer4 {

    private SecretKey aesKey;

    public ChatServer4(SecretKey aesKey) {
        this.aesKey = aesKey;
    }

    // Method to encrypt the message using AES in EAX mode (similar to Python's EAX mode)
    public byte[] encryptMessage(String message) throws Exception {
        // Create a cipher instance for AES in GCM mode (similar to EAX)
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // Generate a random nonce (GCM/NoPadding mode uses a nonce)
        SecureRandom secureRandom = new SecureRandom();
        byte[] nonce = new byte[12];  // Typically, a nonce of 12 bytes is used
        secureRandom.nextBytes(nonce);

        // Initialize the cipher with AES key and nonce
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce); // 128-bit authentication tag length
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);

        // Encrypt the message and get the ciphertext and authentication tag
        byte[] ciphertext = cipher.doFinal(message.getBytes("UTF-8"));

        // Combine nonce and ciphertext
        byte[] encryptedMessage = new byte[nonce.length + ciphertext.length];
        System.arraycopy(nonce, 0, encryptedMessage, 0, nonce.length);
        System.arraycopy(ciphertext, 0, encryptedMessage, nonce.length, ciphertext.length);

        return encryptedMessage;  // Return the combined nonce + ciphertext
    }

    public static void main(String[] args) {
        try {
            // Example AES key (this would normally be shared between the client and server)
            SecretKey aesKey = ...;  // Obtain or generate a SecretKey here

            // Initialize the server with the AES key
            ChatServer4 server = new ChatServer4(aesKey);

            // Encrypt a sample message
            String message = "Hello, Client!";
            byte[] encryptedMessage = server.encryptMessage(message);

            // Output the encrypted message (for demonstration purposes)
            System.out.println("Encrypted Message: " + new String(encryptedMessage));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
