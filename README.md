### **README for Secure Chat Application**

---

## **Overview**

The Secure Chat Application is a Java-based client-server chat system designed with enhanced security features, including **encryption**, **biometric authentication**, and a **graphical user interface (GUI)**. It comprises three main components: 

1. **ChatServer**: The server-side implementation for handling client connections and managing encrypted communication.
2. **ChatClient**: The client-side implementation for interacting with the server.
3. **BiometricAuthenticator**: A simulated biometric authentication module to ensure secure user access.

The system ensures encrypted messaging using **AES** and **RSA**, providing confidentiality and secure key exchange between the server and client.

---

## **Features**
### **Biometric Authentication**
- Simulated fingerprint authentication to verify users before access.
- Limits authentication attempts with a temporary lockout mechanism after multiple failures.

### **Secure Communication**
- **RSA Encryption**: Secures AES key exchange between server and client.
- **AES Encryption**: Encrypts and decrypts chat messages.

### **User-Friendly GUI**
- Built using **Swing**.
- Displays messages in real-time with input fields and buttons for easy interaction.

---

## **System Requirements**
- **Java Development Kit (JDK)**: Version 11 or higher.
- **Operating System**: Cross-platform compatibility (Windows, macOS, Linux).
- **IDE**: IntelliJ IDEA, Eclipse, or any Java-supporting IDE.

---

## **Components**

### 1. **ChatServer**
This class handles the server-side operations of the chat application.
- **RSA Key Pair Generation**: Generates an RSA key pair for secure key exchange.
- **AES Key Management**: Encrypts the AES key using the client's public RSA key and securely transmits it.
- **Encrypted Messaging**: Manages incoming and outgoing encrypted messages.
- **GUI**: Provides an intuitive interface for the server operator.

### 2. **ChatClient**
This class handles the client-side operations.
- **Biometric Authentication**: Simulates fingerprint verification before connecting to the server.
- **Key Exchange**: Receives the server's RSA public key and sends its own for secure key sharing.
- **Encrypted Messaging**: Sends and receives encrypted messages to/from the server.
- **GUI**: Displays chat messages and provides an input field and button for message sending.

### 3. **BiometricAuthenticator**
This class simulates a basic fingerprint authentication system.
- **Authentication Mechanism**: Verifies user-provided input against a stored fingerprint ID.
- **Lockout System**: Locks users out for 30 seconds after 3 failed attempts.
- **Reset Mechanism**: Resets failed attempts after successful authentication or lockout expiry.

---

## **How to Run the Application**

### **Step 1: Start the Server**
1. Compile and run the `ChatServer` class.
2. Ensure it listens on the desired port (default: `12345`).

### **Step 2: Start the Client**
1. Compile and run the `ChatClient` class.
2. Complete the biometric authentication simulation.
3. Connect to the server by entering the correct host and port (default: `localhost` and `12345`).

---

## **Usage**
1. **Authentication**:
   - Enter the correct fingerprint (default: `fingerprint123`) to authenticate.
   - If authentication fails 3 times, wait 30 seconds to retry.
2. **Message Exchange**:
   - Type a message in the input field and click "Send" to transmit it.
   - Messages are encrypted before transmission and decrypted upon reception.

---

## **File Structure**

- **`ChatServer.java`**: Contains the server-side logic, including key exchange, message handling, and GUI setup.
- **`ChatClient.java`**: Contains the client-side logic, including authentication, secure communication, and GUI setup.
- **`BiometricAuthenticator.java`**: Simulates fingerprint authentication with lockout and retry mechanisms.

---

## **Encryption Details**
- **RSA (2048-bit)**:
  - Used for public/private key cryptography during AES key exchange.
- **AES (128-bit)**:
  - Used for encrypting and decrypting chat messages in real-time.
