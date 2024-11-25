import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * BiometricAuthenticator simulates a fingerprint authentication system.
 * It provides a simple interface for authenticating users and manages
 * failed attempt lockouts for enhanced security.
 */
public class BiometricAuthenticator {
    // Simulated correct fingerprint - in a real system, this would be securely stored
    private static final String CORRECT_FINGERPRINT = "fingerprint123";
    
    // Maximum number of failed attempts before lockout
    private static final int MAX_ATTEMPTS = 3;
    
    // Duration of lockout in seconds
    private static final long LOCKOUT_DURATION = 30;

    // Counter for failed authentication attempts
    private int failedAttempts;
    
    // Timestamp of the last failed attempt
    private long lastFailedAttemptTime;

    /**
     * Attempts to authenticate the user using a simulated fingerprint.
     * 
     * @return true if authentication is successful, false otherwise
     */
    public boolean authenticate() {
        // Check if the account is currently locked out
        if (isLockedOut()) {
            System.out.println("Account is temporarily locked. Please try again later.");
            return false;
        }

        System.out.println("Biometric Authentication System");
        System.out.println("-------------------------------");
        System.out.print("Please scan your fingerprint (enter fingerprint ID): ");
        
        // Use try-with-resources to ensure the Scanner is properly closed
        try (Scanner scanner = new Scanner(System.in)) {
            String fingerprintInput = scanner.nextLine().trim();

            // Check if the input matches the correct fingerprint
            if (fingerprintInput.equals(CORRECT_FINGERPRINT)) {
                System.out.println("Fingerprint recognized. Authentication successful!");
                resetFailedAttempts();
                return true;
            } else {
                System.out.println("Fingerprint not recognized. Authentication failed.");
                handleFailedAttempt();
                return false;
            }
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during authentication: " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles a failed authentication attempt.
     * Increments the failed attempt counter and updates the last failed attempt time.
     */
    private void handleFailedAttempt() {
        failedAttempts++;
        lastFailedAttemptTime = System.currentTimeMillis();

        if (failedAttempts >= MAX_ATTEMPTS) {
            System.out.println("Maximum failed attempts reached. Account is now locked.");
        } else {
            int remainingAttempts = MAX_ATTEMPTS - failedAttempts;
            System.out.println("Attempts remaining: " + remainingAttempts);
        }
    }

    /**
     * Checks if the account is currently locked out due to too many failed attempts.
     * 
     * @return true if the account is locked out, false otherwise
     */
    private boolean isLockedOut() {
        if (failedAttempts >= MAX_ATTEMPTS) {
            long elapsedTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastFailedAttemptTime);
            long remainingLockTime = LOCKOUT_DURATION - elapsedTime;

            if (remainingLockTime > 0) {
                System.out.println("Account is locked. Please wait " + remainingLockTime + " seconds before trying again.");
                return true;
            } else {
                // Lockout period has passed, reset the failed attempts
                resetFailedAttempts();
            }
        }
        return false;
    }

    /**
     * Resets the failed attempts counter.
     * Called after a successful authentication or when the lockout period expires.
     */
    private void resetFailedAttempts() {
        failedAttempts = 0;
        lastFailedAttemptTime = 0;
    }
}
