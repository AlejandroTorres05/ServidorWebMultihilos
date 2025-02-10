import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
    public static void main(String[] args) throws IOException{
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        ServerSocket serverSocket = new ServerSocket(8080);

        while (true) {
            System.out.println("Server waiting for conntection");
            Socket socket = serverSocket.accept();
            System.out.println("Connection accept");

            executor.execute(new Handler(socket));
        }
    }    
}