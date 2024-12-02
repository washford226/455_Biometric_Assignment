import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * BiometricAuthenticator simulates a fingerprint authentication system.
 * It provides a simple interface for authenticating users and manages
 * failed attempt lockouts for enhanced security.
 */
public class BiometricAuthenticator {
    private static final String CORRECT_FINGERPRINT = "fingerprint123";
    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCKOUT_DURATION = 30;

    private int failedAttempts;
    private long lastFailedAttemptTime;

    public boolean authenticate() {
        if (isLockedOut()) {
            System.out.println("Account is temporarily locked. Please try again later.");
            return false;
        }

        System.out.println("Biometric Authentication System");
        System.out.println("-------------------------------");
        System.out.print("Please scan your fingerprint (enter fingerprint ID): ");

        // Use Scanner outside of try-with-resources to avoid closing it prematurely
        Scanner scanner = new Scanner(System.in);
        String fingerprintInput = scanner.nextLine().trim();

        if (fingerprintInput.equals(CORRECT_FINGERPRINT)) {
            System.out.println("Fingerprint recognized. Authentication successful!");
            resetFailedAttempts();
            return true;
        } else {
            System.out.println("Fingerprint not recognized. Authentication failed.");
            handleFailedAttempt();
            return false;
        }
    }

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

    private boolean isLockedOut() {
        if (failedAttempts >= MAX_ATTEMPTS) {
            long elapsedTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastFailedAttemptTime);
            long remainingLockTime = LOCKOUT_DURATION - elapsedTime;

            if (remainingLockTime > 0) {
                System.out.println("Account is locked. Please wait " + remainingLockTime + " seconds before trying again.");
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
}