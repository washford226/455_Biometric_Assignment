import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient {
    private String host;
    private int port;
    private Socket clientSocket;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private byte[] aesKey; // Placeholder for AES session key

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            // Initialize client connection settings
            this.clientSocket = new Socket(host, port);

            // Generate RSA keys for encryption
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();

            // Placeholder for AES session key
            this.aesKey = null;

            // Setup GUI for chat interface
            setupGui();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupGui() {
        // Placeholder for GUI setup logic
        System.out.println("Setting up GUI...");
    }

    public static void main(String[] args) {
        // Example usage
        ChatClient client = new ChatClient("127.0.0.1", 12345);
    }



    //This is from the connect to server slide

    private BufferedReader in;  // To read messages from the server
    private PrintWriter out;  // To send messages to the server
    

    private JTextArea chatBox;  // GUI text area for displaying chat messages

    public ChatClient(String host, int port, JTextArea chatBox) {
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
        
        ChatClient client = new ChatClient(host, port, chatBox);
        
        try {
            // Connect to the server
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //This is from the Handling Key Slide
    
    private PublicKey serverPubKey;
    private PrivateKey clientPrivKey;
    private SecretKey aesKey;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    // Method to handle the RSA key exchange
    public void handleKeyExchange() throws Exception {
        // Receive the server's public key
        byte[] serverPubKeyData = new byte[1024];
        in.read(serverPubKeyData);
        
        // Import the server's public key
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(serverPubKeyData);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        serverPubKey = keyFactory.generatePublic(keySpec);
        
        // Send the client's public key to the server
        byte[] clientPubKeyData = clientPrivKey.getPublic().getEncoded();  // Assuming clientPrivKey is already set
        out.write(clientPubKeyData);
        out.flush();
        
        // Receive the encrypted AES key
        byte[] encryptedAesKey = new byte[1024];
        in.read(encryptedAesKey);
        
        // Decrypt the AES key using the client's private RSA key
        aesKey = decryptRsa(encryptedAesKey);
    }

    // Method to decrypt the AES key using the private RSA key
    private SecretKey decryptRsa(byte[] encryptedAesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, clientPrivKey);
        
        byte[] decryptedAesKey = cipher.doFinal(encryptedAesKey);
        
        // Assuming AES key is 16 bytes for AES-128
        SecretKey aesKey = new SecretKeySpec(decryptedAesKey, 0, 16, "AES");
        return aesKey;
    }

    // Example main method for setting up the client and connecting to the server
    public static void main(String[] args) {
        try {
            // Setup the client (host, port, etc.)
            ChatClient client = new ChatClient();
            client.connectToServer("localhost", 12345);  // Connect to server
            
            // Perform the key exchange
            client.handleKeyExchange();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void connectToServer(String host, int port) throws IOException {
        clientSocket = new Socket(host, port);
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());
    }



    //This is from Getting AES Key Slide
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
            ChatClient client = new ChatClient();
            byte[] encryptedAesKey = Base64.getDecoder().decode("ENCRYPTED_AES_KEY_HERE"); // Use actual encrypted key
            
            // Decrypt the AES key using the client's private RSA key
            SecretKey aesKey = client.decryptRsa(encryptedAesKey);
            System.out.println("Decrypted AES Key: " + Base64.getEncoder().encodeToString(aesKey.getEncoded()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //This is from Receiving Messages Slide
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
            ChatClient client = new ChatClient();
            client.connectToServer("localhost", 12345);  // Connect to the server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //This is from Sending Messages Slide
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
            ChatClient client = new ChatClient();
            client.connectToServer("localhost", 12345);  // Connect to the server
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

