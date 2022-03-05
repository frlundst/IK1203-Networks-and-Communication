package myrunnable;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import tcpclient.TCPClient;

public class MyRunnable extends Thread {
    private static int BUFFERSIZE = 1024;
    private Socket clientSocket = null;

    public MyRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        System.out.println("New thread started: Thread" + Thread.currentThread());

        String requestHostname = "";
        String requestString = "";
        boolean requestShutdown = false;
        Integer requestTimeout = null;
        Integer requestLimit = null;
        Integer requestPort = null;

        try{
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            String STATUS = "";

            byte buffer[] = new byte[BUFFERSIZE];

            try {
                // Read input from connected user
                inputStream.read(buffer);
                String request = new String(buffer, StandardCharsets.UTF_8);

                // First split the whole request line on every line break
                // System.out.println(request);
                String[] lines = request.split("\r\n");

                // Save the first line which is the GET url line
                // GET /ask?hostname=time.nist.gov&limit=1200&port=13 HTTP/1.1
                String GETLine = lines[0];
                String[] GETLineArray = GETLine.split(" ");

                // If its not a GET command throw exception
                if (!GETLineArray[0].equals("GET") || !GETLineArray[2].equals("HTTP/1.1")) {
                    STATUS = "HTTP/1.1 400 Bad Request";
                    throw new Exception("HTTP/1.1 400 Bad Request");
                }

                String url = GETLineArray[1];

                // Check if /ask
                // This tells us to set up a tcp connection to another server
                String[] urlArray = url.split("\\?");
                // System.out.println(urlArray[0]);

                if (!urlArray[0].equals("/ask") || urlArray.length == 0) {
                    STATUS = "HTTP/1.1 404 Not Found";
                    throw new Exception("HTTP/1.1 404 Not Found");
                }
                if (urlArray.length < 2) {
                    STATUS = "HTTP/1.1 400 Bad Request";
                    throw new Exception("HTTP/1.1 400 Bad Request");
                }

                String[] parameters = urlArray[1].split("&");

                for (int i = 0; i < parameters.length; i++) {
                    String[] parameter = parameters[i].split("=");

                    if (parameter.length < 2) {
                        STATUS = "HTTP/1.1 400 Bad Request";
                        throw new Exception("HTTP/1.1 400 Bad Request");
                    }

                    if (parameter[0].equals("hostname")) {
                        requestHostname = parameter[1];
                    }
                    if (parameter[0].equals("shutdown")) {
                        if (parameter[1].equals("true")) {
                            requestShutdown = true;
                        } else {
                            requestShutdown = false;
                        }
                    }
                    if (parameter[0].equals("timeout")) {
                        requestTimeout = Integer.parseInt(parameter[1]);
                    }
                    if (parameter[0].equals("limit")) {
                        requestLimit = Integer.parseInt(parameter[1]);
                    }
                    if (parameter[0].equals("port")) {
                        requestPort = Integer.parseInt(parameter[1]);
                    }
                    if (parameter[0].equals("string")) {
                        requestString = parameter[1];
                    }
                }

                if (requestHostname == "" || requestPort == null) {
                    STATUS = "HTTP/1.1 400 Bad Request";
                    throw new Exception("HTTP/1.1 400 Bad Request");
                }

                try {
                    TCPClient tcpClient = new tcpclient.TCPClient(requestShutdown, requestTimeout, requestLimit);

                    byte[] serverBytes = tcpClient.askServer(requestHostname, requestPort, requestString.getBytes());

                    outputStream.write(("HTTP/1.1 200 OK \r\n\r\n").getBytes());
                    outputStream.write(serverBytes);
                    clientSocket.close();
                } catch (Exception e) {
                    STATUS = "HTTP/1.1 500 Internal Server Error";
                    throw new Exception("HTTP/1.1 500 Internal Server Error");
                }

            } catch (Exception e) {
                outputStream.write((STATUS + "\r\n").getBytes());
                clientSocket.close();
                e.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
