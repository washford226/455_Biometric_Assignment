import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class AESDecryptionMessage {

    private SecretKey aesKey;

    public AESDecryptionMessage(SecretKey aesKey) {
        this.aesKey = aesKey;
    }

    public String decryptMessage(byte[] encryptedMessage) throws Exception {
        // Extract the nonce from the first 16 bytes of the encrypted message
        byte[] nonce = new byte[16];
        System.arraycopy(encryptedMessage, 0, nonce, 0, 16);

        // Extract the ciphertext (remaining bytes after the nonce)
        byte[] ciphertext = new byte[encryptedMessage.length - 16];
        System.arraycopy(encryptedMessage, 16, ciphertext, 0, ciphertext.length);

        // Initialize AES cipher in AES/GCM/NoPadding mode
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, nonce);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

        // Decrypt the ciphertext
        byte[] decryptedBytes = cipher.doFinal(ciphertext);

        // Convert the decrypted bytes to a string
        return new String(decryptedBytes, "UTF-8");
    }
}
