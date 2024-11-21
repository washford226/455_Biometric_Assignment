import java.io.IOException;
import java.util.List;

//This is Broadcast Server 
public class ChatServer5 {

    private List<ClientConnection> clients;  // A list of client connections

    public ChatServer5(List<ClientConnection> clients) {
        this.clients = clients;
    }

    // Method to broadcast a message to all clients except the sender
    public void broadcast(String message, ClientConnection senderConn) {
        for (ClientConnection client : clients) {
            if (client != senderConn) {  // Skip sending the message to the sender
                try {
                    // Send the message to the client
                    client.sendMessage(message);
                } catch (IOException e) {
                    System.out.println("[ERROR] Could not send message to " + client.getAddress());
                }
            }
        }
    }

    public static void main(String[] args) {
        // Server setup and initialization here...
    }
}
