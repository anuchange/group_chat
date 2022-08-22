import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatServer {

    private ServerSocket socket;
    private List<Socket> clientSockets;
    private boolean running;
    private BlockingQueue<KV<Integer, String>> queue;
    private BlockingQueue<Socket> notifierQueue;
    private Thread notifierThread;

    public ChatServer() throws IOException {
        socket = new ServerSocket(6666);
        running = true;
        queue = new LinkedBlockingQueue<>(5000);
        notifierQueue = new LinkedBlockingQueue<>(5000);
        clientSockets = new ArrayList<>();
        notifierThread = new ChatServerNotifier(notifierQueue, queue);
        registerShutDownHook();
    }

    private void registerShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    running = false;
                    socket.close();
                    for (Socket clientSocket : clientSockets) {
                        if(!clientSocket.isClosed())
                            clientSocket.close();
                    }
                    notifierThread.interrupt();
                    System.out.println("Server down");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void Start() {

        notifierThread.start();
        while (running) {
            try {
                Socket s = socket.accept();
                new ChatServerHandler(s, queue).start();
                clientSockets.add(s);
                notifierQueue.put(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {

        new ChatServer().Start();
    }
}
