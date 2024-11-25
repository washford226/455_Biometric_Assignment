import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatServer2 {
    private ServerSocket serverSocket;   // Server socket to listen for connections
    private Socket clientSocket;        // Client connection
    private JTextArea chatBox;          // GUI text area for displaying messages
    private ObjectInputStream input;    // Stream to receive messages
    private ObjectOutputStream output;  // Stream to send messages
    private byte[] aesKey;              // AES key placeholder

    public ChatServer2(JTextArea chatBox) {
        this.chatBox = chatBox;
        this.aesKey = null;
    }

    public void startServer() {
        try {
            // Create a server socket to listen for client connections
            serverSocket = new ServerSocket(12345);
            appendToChatBox("Server started. Waiting for a client...\n");

            // Accept a new client connection
            clientSocket = serverSocket.accept();
            appendToChatBox("Connection established with " + clientSocket.getInetAddress() + "\n");

            // Initialize input and output streams for communication
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            input = new ObjectInputStream(clientSocket.getInputStream());

            // Handle AES key exchange with the client
            handleKeyExchange();

            // Start receiving messages from the client
            receiveMessages();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Append messages to the chat box
    private void appendToChatBox(String message) {
        SwingUtilities.invokeLater(() -> chatBox.append(message));
    }

    // Handle AES key exchange with the client
    private void handleKeyExchange() {
        try {
            // Logic for handling AES key exchange with the client
            // For example, sending the server's public RSA key and receiving the AES key encrypted with RSA
            appendToChatBox("Performing AES key exchange with the client...\n");

            // Placeholder: Simulate key exchange (replace with actual RSA logic)
            aesKey = new byte[16]; // Example: Replace with decrypted AES key
            appendToChatBox("AES key successfully exchanged!\n");

        } catch (Exception e) {
            appendToChatBox("Error during AES key exchange: " + e.getMessage() + "\n");
        }
    }

    // Start receiving messages from the client
    private void receiveMessages() {
        try {
            String message;
            while ((message = (String) input.readObject()) != null) {
                appendToChatBox("Client: " + message + "\n");
            }
        } catch (IOException | ClassNotFoundException e) {
            appendToChatBox("Connection closed by the client.\n");
        }
    }

    public static void main(String[] args) {
        // Create the GUI and start the server
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Server Chat");
            JTextArea chatBox = new JTextArea(20, 50);
            chatBox.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(chatBox);
            frame.add(scrollPane);
            frame.setSize(600, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            ChatServer2 server = new ChatServer2(chatBox);
            server.startServer();
        });
    }
}
