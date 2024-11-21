import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient6 {

    private Socket clientSocket;
    private SecretKey aesKey;  // AES key used for encryption
    private JTextArea chatBox;  // GUI text area to display messages
    private JTextField messageEntry;  // GUI input field for message

    // Method to encrypt and send the message to the server
    public void sendMessage() {
        String message = messageEntry.getText();  // Get the message from the input field

        if (message != null && !message.isEmpty()) {
            try {
                // Encrypt the message using AES
                byte[] encryptedMessage = encryptMessage(message);

                // Send the encrypted message to the server
                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write(encryptedMessage);

                // Display the sent message in the chat box
                chatBox.append("Client: " + message + "\n");

                // Clear the input field after sending the message
                messageEntry.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Method to encrypt the message using AES
    private byte[] encryptMessage(String message) throws Exception {
        // Initialize AES cipher in EAX mode for encryption
        Cipher cipher = Cipher.getInstance("AES/EAX/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);

        // Encrypt the message
        byte[] ciphertext = cipher.doFinal(message.getBytes("UTF-8"));

        // Concatenate the nonce (first 16 bytes of the cipher) with the ciphertext
        byte[] nonce = cipher.getIV();  // Nonce is generated during encryption
        byte[] encryptedMessage = new byte[nonce.length + ciphertext.length];
        System.arraycopy(nonce, 0, encryptedMessage, 0, nonce.length);
        System.arraycopy(ciphertext, 0, encryptedMessage, nonce.length, ciphertext.length);

        return encryptedMessage;
    }

    // Example method to connect the client and start the chat
    public void connectToServer(String host, int port) throws Exception {
        clientSocket = new Socket(host, port);
        chatBox = new JTextArea();
        messageEntry = new JTextField();

        // Assuming aesKey is already set (perhaps from RSA key exchange)

        // Start the chat
        // Add a listener to send the message when the Enter key is pressed in the input field
        messageEntry.addActionListener(e -> sendMessage());
    }

    // Main method for testing
    public static void main(String[] args) {
        try {
            ChatClient6 client = new ChatClient6();
            client.connectToServer("localhost", 12345);  // Connect to the server
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
