import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.Base64;

public class ChatClient {
    private JFrame frame;
    private JTextArea chatBox;
    private JTextField messageField;
    private JButton sendButton;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private SecretKeySpec aesKey;

    public ChatClient(String host, int port) {
        try {
            // Connect to the server
            socket = new Socket(host, port);
            System.out.println("Connected to the server!");

            // Set up input and output streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Receive server's public RSA key
            ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
            PublicKey serverPublicKey = (PublicKey) objectIn.readObject();

            // Send client's public RSA key
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
            objectOut.writeObject(publicKey);

            // Receive encrypted AES key and decrypt it
            String encryptedAesKey = in.readLine();
            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] aesKeyBytes = rsaCipher.doFinal(Base64.getDecoder().decode(encryptedAesKey));
            aesKey = new SecretKeySpec(aesKeyBytes, "AES");

            // Set up GUI
            setupGUI();

            // Start receiving messages
            new Thread(this::receiveMessages).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupGUI() {
        frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocation(800, 10);
        

        chatBox = new JTextArea();
        chatBox.setFont(new Font("Rockwell", Font.PLAIN, 12));
        chatBox.setEditable(false);
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
                chatBox.append("Server: " + message + "\n");
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
                chatBox.append("You: " + message + "\n");
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
        SwingUtilities.invokeLater(() -> new ChatClient("localhost", 12345));
    }
} //Created with the help of ChatGPT and my Copilot
