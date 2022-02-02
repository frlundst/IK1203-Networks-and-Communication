package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    
    public TCPClient() {
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        try (Socket clientSocket = new Socket(hostname, port)) {
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(toServerBytes);
            InputStream inputStream = clientSocket.getInputStream();
            return inputStream.readAllBytes();
        }
    }
}
