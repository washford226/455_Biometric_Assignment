import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.ObjectInputStream;


//From Message Handling slide
public class MessageReceiver implements Runnable {
    private ObjectInputStream input;
    private SecretKey aesKey;

    public MessageReceiver(ObjectInputStream input, SecretKey aesKey) {
        this.input = input;
        this.aesKey = aesKey;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Step 1: Receive encrypted message from the client
                byte[] encryptedMessage = (byte[]) input.readObject();

                if (encryptedMessage != null) {
                    // Step 2: Decrypt the received message
                    String message = decryptMessage(encryptedMessage);

                    // Step 3: Display the decrypted message in the console (or GUI)
                    System.out.println("Client: " + message);
                }
            } catch (Exception e) {
                // Handle connection loss or other exceptions
                System.err.println("Connection lost or error occurred: " + e.getMessage());
                break; // Exit the loop if an error occurs
            }
        }
    }

    private String decryptMessage(byte[] encryptedMessage) throws Exception {
        // Decrypt the message using AES
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Extract IV from the first 16 bytes of the encrypted message
        byte[] iv = new byte[16];
        System.arraycopy(encryptedMessage, 0, iv, 0, 16);

        // Initialize the cipher with the IV and the AES key
        javax.crypto.spec.IvParameterSpec ivSpec = new javax.crypto.spec.IvParameterSpec(iv);
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);

        // Decrypt the message (remaining bytes after the IV)
        byte[] ciphertext = new byte[encryptedMessage.length - 16];
        System.arraycopy(encryptedMessage, 16, ciphertext, 0, ciphertext.length);

        byte[] decryptedBytes = aesCipher.doFinal(ciphertext);

        // Convert the decrypted bytes to a string
        return new String(decryptedBytes, "UTF-8");
    }
}
