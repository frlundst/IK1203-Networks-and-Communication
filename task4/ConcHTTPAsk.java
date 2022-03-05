import java.net.*;
import java.io.*;
import myrunnable.MyRunnable;

//http://localhost:8888/ask?hostname=time.nist.gov&limit=1200&port=13

public class ConcHTTPAsk {
    public static void main(String[] args) throws InterruptedException {
        int port = Integer.parseInt(args[0]);

        try{
            ServerSocket serverSocket = new ServerSocket(port); 
            while(true){
                Socket clientSocket = serverSocket.accept();
                //System.out.println("New connection, starting new thread...");
                MyRunnable myRunnable = new MyRunnable(clientSocket);
                myRunnable.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
