import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient2 {

    private String host;
    private int port;
    private Socket clientSocket;
    private BufferedReader in;  // To read messages from the server
    private PrintWriter out;  // To send messages to the server
    private SecretKey aesKey;  // Shared AES key after RSA key exchange

    private JTextArea chatBox;  // GUI text area for displaying chat messages

    public ChatClient2(String host, int port, JTextArea chatBox) {
        this.host = host;
        this.port = port;
        this.chatBox = chatBox;
    }

    // Method to connect to the server and establish the chat connection
    public void connect() throws IOException {
        // Connect to the server
        clientSocket = new Socket(host, port);

        // Create input and output streams for communication with the server
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);

        // Update chat box (GUI) to show that the client is connected
        chatBox.setText("Connected to server.\n");

        // Start the RSA key exchange (e.g., sending and receiving public keys)
        handleKeyExchange();

        // Start receiving messages from the server
        receiveMessages();
    }

    // Method to perform the RSA-based key exchange
    public void handleKeyExchange() {
        // Handle the RSA-based key exchange (receive public key, exchange AES key)
        // Example: Encrypt and send AES key to server, decrypt server's AES key, etc.
    }

    // Method to receive messages from the server
    public void receiveMessages() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    // Display received message in the chat box
                    chatBox.append("Server: " + message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        // Initialize the client with host, port, and GUI chat box
        String host = "localhost";
        int port = 12345;
        JTextArea chatBox = new JTextArea();
        
        ChatClient2 client = new ChatClient2(host, port, chatBox);
        
        try {
            // Connect to the server
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
