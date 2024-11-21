import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.DataOutputStream;
import java.io.IOException;

public class ChatServer3 {

    private SecretKey aesKey;
    private DataOutputStream outStream;  // Output stream to send data to client
    private JTextArea chatBox;  // GUI text area to display chat messages
    private JTextField messageEntry;  // GUI entry field for typing messages

    public ChatServer3(SecretKey aesKey, DataOutputStream outStream, JTextArea chatBox, JTextField messageEntry) {
        this.aesKey = aesKey;
        this.outStream = outStream;
        this.chatBox = chatBox;
        this.messageEntry = messageEntry;
    }

    // Method to encrypt the message
    public byte[] encryptMessage(String message) throws Exception {
        // AES Encryption logic (using AES/GCM/NoPadding or similar mode)
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] nonce = new byte[16];  // Example: Use a random nonce or one generated per session
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        
        // Encrypt the message
        byte[] encryptedMessage = cipher.doFinal(message.getBytes("UTF-8"));

        // Combine the nonce with the encrypted message to send it (nonce first, then ciphertext)
        byte[] combinedMessage = new byte[nonce.length + encryptedMessage.length];
        System.arraycopy(nonce, 0, combinedMessage, 0, nonce.length);
        System.arraycopy(encryptedMessage, 0, combinedMessage, nonce.length, encryptedMessage.length);

        return combinedMessage;
    }

    // Method to send the message
    public void sendMessage() throws IOException, Exception {
        // Get the message from the message entry
        String message = messageEntry.getText();

        if (!message.isEmpty()) {
            // Encrypt the message
            byte[] encryptedMessage = encryptMessage(message);

            // Send the encrypted message to the client
            outStream.write(encryptedMessage);
            outStream.flush();  // Ensure the message is sent

            // Display the sent message in the chat box
            chatBox.append("Server: " + message + "\n");

            // Clear the message entry field
            messageEntry.setText("");
        }
    }
}
