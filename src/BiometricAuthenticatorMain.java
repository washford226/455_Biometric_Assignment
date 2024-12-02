import javax.swing.SwingUtilities;

public class BiometricAuthenticatorMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BiometricAuthenticatorGUI::new);
    }
}