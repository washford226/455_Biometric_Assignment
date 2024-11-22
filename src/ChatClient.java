import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private JFrame frame;
    private JTextArea chatBox;
    private JTextField messageField;
    private JButton sendButton;

    public ChatClient(String host, int port) {
        try {
            // Connect to the server
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Set up the GUI
            setupGUI();

            // Start a thread to listen for incoming messages
            new Thread(this::receiveMessages).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupGUI() {
        frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        // Chat box to display messages
        chatBox = new JTextArea();
        chatBox.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatBox);

        // Message field to type messages
        messageField = new JTextField(30);

        // Send button to send messages
        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Bottom panel for the message field and send button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // Add components to frame
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                chatBox.append("Server: " + message + "\n");
            }
        } catch (IOException e) {
            chatBox.append("Connection closed.\n");
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            out.println(message); // Send message to the server
            chatBox.append("You: " + message + "\n"); // Show it in the chat box
            messageField.setText(""); // Clear the input field
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClient("127.0.0.1", 12345));
    }
}
