import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class BiometricAuthenticatorGUI extends JFrame {
    private static final String CORRECT_FINGERPRINT = "fingerprint123";
    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCKOUT_DURATION = 30;

    private int failedAttempts;
    private long lastFailedAttemptTime;

    private JTextField fingerprintField;
    private JTextArea resultArea;

    public BiometricAuthenticatorGUI() {
        setupGUI();
    }

    private void setupGUI() {
        setTitle("Biometric Authentication");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Center the window

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1)); // Use GridLayout for 4 rows

        

        // Prompt label for fingerprint ID entry
        JLabel promptLabel = new JLabel("Please enter your fingerprint ID:");
        promptLabel.setOpaque(true);
        promptLabel.setBackground(Color.WHITE);
        promptLabel.setForeground(Color.BLACK);
        promptLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center text
        panel.add(promptLabel);

        // Fingerprint input field
        fingerprintField = new JTextField();
        panel.add(fingerprintField);

        // Result area for displaying authentication messages
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        panel.add(new JScrollPane(resultArea));

        // Authenticate button with bluish-orange background
        JButton authenticateButton = new JButton("Authenticate");
        authenticateButton.setBackground(new Color(173, 216, 230)); // Bluish orange color
        authenticateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (authenticate()) {
                    dispose(); // Close the authentication window on success
                    new ChatClient("localhost", 12345); // Launch ChatClient after successful authentication
                }
            }
        });
        
        panel.add(authenticateButton);

        getContentPane().add(panel);
        setVisible(true); // Show the GUI
    }

    public boolean authenticate() {
        String fingerprintInput = fingerprintField.getText().trim(); // Get user input

        if (fingerprintInput.isEmpty()) { // Check for empty input
            resultArea.append("Please enter a fingerprint ID.\n");
            return false; // Return false if no input
        }

        if (isLockedOut()) {
            resultArea.append("Account is temporarily locked. Please try again later.\n");
            return false; // Return false if locked out
        }

        if (fingerprintInput.equals(CORRECT_FINGERPRINT)) {
            resultArea.append("Fingerprint recognized. Authentication successful!\n");
            resetFailedAttempts();
            return true; // Return true for successful authentication
        } else {
            resultArea.append("Fingerprint not recognized. Authentication failed.\n");
            handleFailedAttempt();
            return false; // Return false for failed authentication
        }
    }

    private void handleFailedAttempt() {
        failedAttempts++;
        lastFailedAttemptTime = System.currentTimeMillis();

        if (failedAttempts >= MAX_ATTEMPTS) {
            resultArea.append("Maximum failed attempts reached. Account is now locked.\n");
        } else {
            int remainingAttempts = MAX_ATTEMPTS - failedAttempts;
            resultArea.append("Attempts remaining: " + remainingAttempts + "\n");
        }
    }

    private boolean isLockedOut() {
        if (failedAttempts >= MAX_ATTEMPTS) {
            long elapsedTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastFailedAttemptTime);
            long remainingLockTime = LOCKOUT_DURATION - elapsedTime;

            if (remainingLockTime > 0) {
                resultArea.append("Account is locked. Please wait " + remainingLockTime + " seconds before trying again.\n");
                return true;
            } else {
                resetFailedAttempts();
            }
        }
        return false;
    }

    private void resetFailedAttempts() {
        failedAttempts = 0;
        lastFailedAttemptTime = 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BiometricAuthenticatorGUI::new);
    }
}