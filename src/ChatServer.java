import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ChatServer {
    
    private String host;
    private int port;
    private ServerSocket serverSocket;

    // Constructor to initialize the server
    public ChatServer(String host, int port) throws IOException {
        // Set default values if no arguments are passed
        if (host == null) host = "127.0.0.1";
        if (port == 0) port = 12345;

        this.host = host;
        this.port = port;
        
        // Create a new ServerSocket object
        serverSocket = new ServerSocket(port);

        // Bind the server socket to the specified host and port
        System.out.println("Server started on " + host + ":" + port);
        serverSocket.bind(new java.net.InetSocketAddress(host, port));
        
        // Start listening for client connections
        serverSocket.listen(1);
    }



//Code from RSA key pair that might go here

    private PrivateKey privateKey; // Server's private RSA key
    private PublicKey publicKey;  // Server's public RSA key
    private byte[] aesKey;        // Placeholder for AES key (shared with client)
    private Object clientConnection; // Placeholder for client connection
    private Object clientAddress;    // Placeholder for client address

    public ChatServer() {
        try {
            // Generate RSA key pair (2048 bits) for secure AES key exchange
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();

            // Placeholder for AES key and client connection details
            this.aesKey = null;
            this.clientConnection = null;
            this.clientAddress = null;

            // Set up the graphical user interface (GUI) for the chat server
            setupGui();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupGui() {
        // Placeholder for GUI setup logic
        System.out.println("Setting up GUI...");
    }




    //Start server slide stuff

    private Socket clientSocket;        // Client connection
   

    public ChatServer(JTextArea chatBox) {
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

            ChatServer server = new ChatServer(chatBox);
            server.startServer();
        });
    }




    //This is from Sending Messages slide
    private SecretKey aesKey;
    private DataOutputStream outStream;  // Output stream to send data to client
    private JTextArea chatBox;  // GUI text area to display chat messages
    private JTextField messageEntry;  // GUI entry field for typing messages

    public ChatServer(SecretKey aesKey, DataOutputStream outStream, JTextArea chatBox, JTextField messageEntry) {
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


    //This is from Encrypting Messages slide
    private SecretKey aesKey;

    public ChatServer(SecretKey aesKey) {
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
            ChatServer server = new ChatServer(aesKey);

            // Encrypt a sample message
            String message = "Hello, Client!";
            byte[] encryptedMessage = server.encryptMessage(message);

            // Output the encrypted message (for demonstration purposes)
            System.out.println("Encrypted Message: " + new String(encryptedMessage));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //This is from the Broadcast Messages to all clients slide
    private List<ClientConnection> clients;  // A list of client connections

    public ChatServer(List<ClientConnection> clients) {
        this.clients = clients;
    }

    // Method to broadcast a message to all clients except the sender
    public void broadcast(String message, ClientConnection senderConn) {
        for (ClientConnection client : clients) {
            if (client != senderConn) {  // Skip sending the message to the sender
                try {
                    // Send the message to the client
                    client.sendMessage(message);
                } catch (IOException e) {
                    System.out.println("[ERROR] Could not send message to " + client.getAddress());
                }
            }
        }
    }
}

