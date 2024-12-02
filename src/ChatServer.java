import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ChatServer {

   private JFrame frame;
   private JTextArea chatBox;
   private JTextField messageField;
   private JButton sendButton;

   private ServerSocket serverSocket;
   private Socket clientSocket;
   private BufferedReader in;
   private PrintWriter out;

   private SecretKey aesKey;

   public ChatServer(int port) {
       try {

           KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
           keyGen.initialize(2048);
           var keyPair= keyGen.generateKeyPair();
           PrivateKey privateKey= keyPair.getPrivate();
           PublicKey publicKey= keyPair.getPublic();

           serverSocket= new ServerSocket(port);
           System.out.println("Server started. Waiting for client connection...");

           clientSocket= serverSocket.accept();
           System.out.println("Client connected!");

           in= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
           out= new PrintWriter(clientSocket.getOutputStream(), true);

           ObjectOutputStream objectOut= new ObjectOutputStream(clientSocket.getOutputStream());
           objectOut.writeObject(publicKey);

           ObjectInputStream objectIn= new ObjectInputStream(clientSocket.getInputStream());
           PublicKey clientPublicKey= (PublicKey) objectIn.readObject();

           aesKey= KeyGenerator.getInstance("AES").generateKey();

           Cipher rsaCipher= Cipher.getInstance("RSA");
           rsaCipher.init(Cipher.ENCRYPT_MODE, clientPublicKey);
           byte[] encryptedAesKey= rsaCipher.doFinal(aesKey.getEncoded());
           out.println(Base64.getEncoder().encodeToString(encryptedAesKey));

           setupGUI();

           new Thread(this::receiveMessages).start();

       } catch (Exception e) {
           e.printStackTrace();
       }
   }

   private void setupGUI() {

       frame= new JFrame("Chat Server");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(500, 400);

       chatBox= new JTextArea();
       chatBox.setEditable(false); 
       chatBox.setFont(new Font("Rockwell", Font.PLAIN, 12));
       
       JScrollPane scrollPane= new JScrollPane(chatBox);

       messageField= new JTextField(30); 
       
       sendButton= new JButton("Send"); 
       
       sendButton.addActionListener(e -> sendMessage());

       JPanel panel= new JPanel(new BorderLayout());
       
       panel.add(messageField, BorderLayout.CENTER); 
       
       panel.add(sendButton, BorderLayout.EAST); 

       frame.add(scrollPane, BorderLayout.CENTER); 
       
      frame.add(panel, BorderLayout.SOUTH); 

      frame.setVisible(true); 
   }

   private void receiveMessages() { 
      try { 
          String encryptedMessage; 

          while ((encryptedMessage= in.readLine()) != null) { 

              String message= decryptMessage(encryptedMessage); 

              SwingUtilities.invokeLater(() -> chatBox.append("Client: " + message + "\n")); 

          } 

      } catch (Exception e) { 

          SwingUtilities.invokeLater(() -> chatBox.append("Connection closed.\n")); 

      } 

  }

  private void sendMessage() { 

      try { 

          String message= messageField.getText(); 

          if (!message.isEmpty()) { 

              String encryptedMessage= encryptMessage(message); 

              out.println(encryptedMessage); 

              chatBox.append("Server: " + message + "\n"); 

              messageField.setText(""); 

          } 

      } catch (Exception e) { 

          e.printStackTrace(); 

      } 

  }

  private String encryptMessage(String message) throws Exception { 

      Cipher aesCipher= Cipher.getInstance("AES"); 

      aesCipher.init(Cipher.ENCRYPT_MODE, aesKey); 

      byte[] encryptedBytes= aesCipher.doFinal(message.getBytes()); 

      return Base64.getEncoder().encodeToString(encryptedBytes); 

  }

  private String decryptMessage(String encryptedMessage) throws Exception { 

      Cipher aesCipher= Cipher.getInstance("AES"); 

      aesCipher.init(Cipher.DECRYPT_MODE, aesKey); 

      byte[] decodedBytes= Base64.getDecoder().decode(encryptedMessage); 

      byte[] decryptedBytes= aesCipher.doFinal(decodedBytes); 

      return new String(decryptedBytes); 

  }

  public static void main(String[] args) { 
    
     SwingUtilities.invokeLater(() -> new ChatServer(12345));  
     
     }
}