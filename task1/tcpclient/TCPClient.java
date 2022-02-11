package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    
    public TCPClient() {
    }

    /**
     * This method receives an hostname and port and the client establishes a connection to that host.
     * User input gets sent (a string that was encoded into byte form) through an outputstream.
     * @param hostname
     * @param port
     * @param toServerBytes a byte array of the user input that is sent to a server.
     * @return a byte array from a ByteArrayOutputStream that got the sockets inputsstream (in byte array form) written to it.
     * @throws IOException
     */
    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        Socket clientSocket = new Socket(hostname, port);
        byte buffer[] = new byte[1024];
        OutputStream outputStream = clientSocket.getOutputStream();
        InputStream inputStream = clientSocket.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        outputStream.write(toServerBytes);

        for(int i = inputStream.read(buffer); i != -1; i = inputStream.read(buffer)){
            byteArrayOutputStream.write(buffer, 0, i);
        }

        clientSocket.close();
        return byteArrayOutputStream.toByteArray();
    }
}