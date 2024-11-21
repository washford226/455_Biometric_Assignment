import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import javax.swing.JTextArea;  // Assuming you're using JTextArea for chat box

public class ChatClient5 {

    private Socket clientSocket;
    private SecretKey aesKey;  // AES key used for decryption
    private JTextArea chatBox;  // GUI text area to display messages

    // Method to continuously receive and decrypt messages
    public void receiveMessages() {
        try {
            InputStream inputStream = clientSocket.getInputStream();

            while (true) {
                // Receive encrypted message
                byte[] encryptedMessage = new byte[1024];
                int bytesRead = inputStream.read(encryptedMessage);
                if (bytesRead > 0) {
                    // Decrypt the received message
                    String message = decryptMessage(encryptedMessage);
                    
                    // Display the decrypted message in the chat box
                    chatBox.append("Server: " + message + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  // Handle exception if the connection is lost
        }
    }

    // Method to decrypt the received message using AES
    private String decryptMessage(byte[] encryptedMessage) {
        try {
            // Assuming the first 16 bytes are the nonce and the rest is the ciphertext
            byte[] nonce = new byte[16];
            System.arraycopy(encryptedMessage, 0, nonce, 0, 16);
            byte[] ciphertext = new byte[encryptedMessage.length - 16];
            System.arraycopy(encryptedMessage, 16, ciphertext, 0, ciphertext.length);

            // Initialize AES cipher in EAX mode for decryption
            Cipher cipher = Cipher.getInstance("AES/EAX/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new javax.crypto.spec.IvParameterSpec(nonce));

            // Decrypt the message
            byte[] decryptedMessageBytes = cipher.doFinal(ciphertext);
            return new String(decryptedMessageBytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Example method to connect the client and start receiving messages
    public void connectToServer(String host, int port) throws IOException {
        clientSocket = new Socket(host, port);
        chatBox = new JTextArea();
        chatBox.setEditable(false);  // Make the chat box read-only

        // Start receiving messages in a separate thread
        new Thread(() -> receiveMessages()).start();
    }

    public static void main(String[] args) {
        try {
            ChatClient5 client = new ChatClient5();
            client.connectToServer("localhost", 12345);  // Connect to the server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
