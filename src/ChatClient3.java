import javax.crypto.Cipher;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ChatClient3 {

    private Socket clientSocket;
    private PublicKey serverPubKey;
    private PrivateKey clientPrivKey;
    private SecretKey aesKey;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    // Method to handle the RSA key exchange
    public void handleKeyExchange() throws Exception {
        // Receive the server's public key
        byte[] serverPubKeyData = new byte[1024];
        in.read(serverPubKeyData);
        
        // Import the server's public key
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(serverPubKeyData);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        serverPubKey = keyFactory.generatePublic(keySpec);
        
        // Send the client's public key to the server
        byte[] clientPubKeyData = clientPrivKey.getPublic().getEncoded();  // Assuming clientPrivKey is already set
        out.write(clientPubKeyData);
        out.flush();
        
        // Receive the encrypted AES key
        byte[] encryptedAesKey = new byte[1024];
        in.read(encryptedAesKey);
        
        // Decrypt the AES key using the client's private RSA key
        aesKey = decryptRsa(encryptedAesKey);
    }

    // Method to decrypt the AES key using the private RSA key
    private SecretKey decryptRsa(byte[] encryptedAesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, clientPrivKey);
        
        byte[] decryptedAesKey = cipher.doFinal(encryptedAesKey);
        
        // Assuming AES key is 16 bytes for AES-128
        SecretKey aesKey = new SecretKeySpec(decryptedAesKey, 0, 16, "AES");
        return aesKey;
    }

    // Example main method for setting up the client and connecting to the server
    public static void main(String[] args) {
        try {
            // Setup the client (host, port, etc.)
            ChatClient3 client = new ChatClient3();
            client.connectToServer("localhost", 12345);  // Connect to server
            
            // Perform the key exchange
            client.handleKeyExchange();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void connectToServer(String host, int port) throws IOException {
        clientSocket = new Socket(host, port);
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());
    }
}
