import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.Base64;

public class ChatServer {
    private JFrame frame;
    private JTextArea chatBox;
    private JTextField messageField;
    private JButton sendButton;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    private SecretKey aesKey;
    private PrivateKey privateKey;
    private PublicKey clientPublicKey;

    public ChatServer(int port) {
        try {
            // Generate RSA key pair for the server
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            // Start server socket
            serverSocket = new ServerSocket(port);
            System.out.println("Server started. Waiting for client connection...");

            // Accept client connection
            clientSocket = serverSocket.accept();
            System.out.println("Client connected!");

            // Set up input and output streams
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Send server's public RSA key to the client
            ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOut.writeObject(publicKey);

            // Receive client's public RSA key
            ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());
            clientPublicKey = (PublicKey) objectIn.readObject();

            // Generate AES key
            aesKey = KeyGenerator.getInstance("AES").generateKey();

            // Encrypt AES key using the client's public RSA key and send it
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
            byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());
            out.println(Base64.getEncoder().encodeToString(encryptedAesKey));

            // Set up GUI
            setupGUI();

            // Start receiving messages
            new Thread(this::receiveMessages).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupGUI() {
        frame = new JFrame("Chat Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocation(50, 10);
       

        chatBox = new JTextArea();
        chatBox.setEditable(false);
        chatBox.setFont(new Font("Rockwell", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(chatBox);

        messageField = new JTextField(30);
        messageField.setFont(new Font("Rockwell", Font.PLAIN, 12));
        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void receiveMessages() {
        try {
            String encryptedMessage;
            while ((encryptedMessage = in.readLine()) != null) {
                String message = decryptMessage(encryptedMessage);
                chatBox.append("Client: " + message + "\n");
            }
        } catch (Exception e) {
            chatBox.append("Connection closed.\n");
        }
    }

    private void sendMessage() {
        try {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                String encryptedMessage = encryptMessage(message);
                out.println(encryptedMessage);
                chatBox.append("Server: " + message + "\n");
                messageField.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String encryptMessage(String message) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedBytes = aesCipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private String decryptMessage(String encryptedMessage) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] decryptedBytes = aesCipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatServer(12345));
    }
} //Created with the help of ChatGPT and my Copilot