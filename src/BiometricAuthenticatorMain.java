public class BiometricAuthenticatorMain {
    public static void main(String[] args) {
        BiometricAuthenticator authenticator = new BiometricAuthenticator();
        
        System.out.println("Welcome to the Biometric Authentication Test");
        System.out.println("--------------------------------------------");
        
        // Test successful authentication
        System.out.println("Test 1: Successful Authentication");
        boolean result = authenticator.authenticate();
        System.out.println("Authentication result: " + (result ? "Success" : "Failure"));
        System.out.println();

        // Test failed authentication
        System.out.println("Test 2: Failed Authentication (3 attempts)");
        for (int i = 0; i < 3; i++) {
            result = authenticator.authenticate();
            System.out.println("Authentication result: " + (result ? "Success" : "Failure"));
        }
        System.out.println();

        // Test lockout
        System.out.println("Test 3: Lockout");
        result = authenticator.authenticate();
        System.out.println("Authentication result: " + (result ? "Success" : "Failure"));
        System.out.println();

        // Wait for lockout to expire
        System.out.println("Waiting for lockout to expire...");
        try {
            Thread.sleep(31000); // Wait for 31 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Test after lockout expiry
        System.out.println("Test 4: Authentication after lockout expiry");
        result = authenticator.authenticate();
        System.out.println("Authentication result: " + (result ? "Success" : "Failure"));
    }
}
