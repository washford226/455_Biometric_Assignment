import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatServerGUI {
    private JFrame frame;                // Main window for the server chat interface
    private JTextArea chatBox;          // Scrollable text area to display chat messages
    private JTextField messageEntry;    // Entry box for server to type messages
    private JButton sendButton;         // Button to send messages

    public ChatServerGUI() {
        // Create main window
        frame = new JFrame("Server Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create a scrollable text area for chat messages
        chatBox = new JTextArea(20, 50);
        chatBox.setEditable(false); // Make the text area read-only
        JScrollPane scrollPane = new JScrollPane(chatBox);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        frame.add(scrollPane, gbc);

        // Entry box for server to type messages
        messageEntry = new JTextField(40);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.add(messageEntry, gbc);

        // Button to send messages
        sendButton = new JButton("Send");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.add(sendButton, gbc);

        // Add action listener for the send button
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Start the server in a separate thread to handle incoming connections
        new Thread(() -> startServer()).start();

        // Display the GUI
        frame.setVisible(true);
    }

    // Method to send a message
    private void sendMessage() {
        String message = messageEntry.getText();
        if (!message.isEmpty()) {
            // Display the message in the chat box
            chatBox.append("Server: " + message + "\n");
            messageEntry.setText(""); // Clear the entry field
            // Logic to send the message to the client (to be implemented)
        }
    }

    // Placeholder for starting the server
    private void startServer() {
        // Logic for handling incoming connections (to be implemented)
        chatBox.append("Server started and listening for connections...\n");
    }

    public static void main(String[] args) {
        // Launch the server GUI
        SwingUtilities.invokeLater(ChatServerGUI::new);
    }
}
