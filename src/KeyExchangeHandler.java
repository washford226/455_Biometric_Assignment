import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;


//This is from Key Exchange slide
public class KeyExchangeHandler {
    private PublicKey clientPublicKey;
    private PrivateKey serverPrivateKey;
    private PublicKey serverPublicKey;
    private SecretKey aesKey;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public KeyExchangeHandler(PrivateKey serverPrivateKey, PublicKey serverPublicKey, ObjectInputStream input, ObjectOutputStream output) {
        this.serverPrivateKey = serverPrivateKey;
        this.serverPublicKey = serverPublicKey;
        this.input = input;
        this.output = output;
    }

    public void handleKeyExchange() {
        try {
            // Step 1: Send the server's public RSA key to the client
            output.writeObject(serverPublicKey.getEncoded());
            output.flush();

            // Step 2: Receive the client's public RSA key
            byte[] clientPubKeyData = (byte[]) input.readObject();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            clientPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(clientPubKeyData));

            // Step 3: Generate a random AES key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // AES-128
            aesKey = keyGen.generateKey();

            // Step 4: Encrypt the AES key with the client's public RSA key
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            rsaCipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
            byte[] encryptedAesKey = rsaCipher.doFinal(aesKey.getEncoded());

            // Step 5: Send the encrypted AES key to the client
            output.writeObject(encryptedAesKey);
            output.flush();

            System.out.println("AES key exchange successfully completed!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error during key exchange: " + e.getMessage());
        }
    }

    public SecretKey getAesKey() {
        return aesKey;
    }
}
