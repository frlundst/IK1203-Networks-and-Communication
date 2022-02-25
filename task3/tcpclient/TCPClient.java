package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    private boolean shutdown = false;
    private Integer timeout = null;
    private Integer limit = null;
    private static int BUFFERSIZE = 1024;

    /**
     * 
     * @param shutdown
     * @param timeout
     * @param limit
     */
    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    /**
     * This method receives an hostname and port and the client establishes a
     * connection to that host.
     * User input gets sent (a string that was encoded into byte form) through an
     * outputstream.
     * 
     * @param hostname
     * @param port
     * @param toServerBytes a byte array of the user input that is sent to a server.
     * @return a byte array from a ByteArrayOutputStream that got the sockets
     *         inputsstream (in byte array form) written to it.
     * @throws IOException
     */
    public byte[] askServer(String hostname, int port, byte[] toServerBytes) throws IOException {
        byte buffer[] = new byte[BUFFERSIZE];
        if (limit != null && limit < BUFFERSIZE) buffer = new byte[limit];

        Socket clientSocket = new Socket(hostname, port);

        OutputStream outputStream = clientSocket.getOutputStream();
        outputStream.write(toServerBytes);
        if (shutdown)clientSocket.shutdownOutput();

        InputStream inputStream = clientSocket.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            if (timeout != null) clientSocket.setSoTimeout(timeout);
            
            if (limit != null) {
                for (int i = inputStream.read(buffer), bytesRead = 0; i != -1; i = inputStream.read(buffer), bytesRead += i) {
                    byteArrayOutputStream.write(buffer, 0, i);
                    if (bytesRead >= limit)
                        break;
                }
            } else {
                for (int i = inputStream.read(buffer); i != -1; i = inputStream.read(buffer)) {
                    byteArrayOutputStream.write(buffer, 0, i);
                }
            }
        } catch (SocketTimeoutException e) {
            
        }

        clientSocket.close();
        return byteArrayOutputStream.toByteArray();
    }
}
