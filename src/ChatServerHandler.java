import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;


public class ChatServerHandler extends Thread {

    private Socket socket;
    private BlockingQueue<KV<Integer, String>> queue;

    public ChatServerHandler(Socket s, BlockingQueue<KV<Integer, String>> queue) {
        this.socket = s;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            while(true) {
                if(socket.isClosed())
                    return;
                String line = reader.readLine();
                if (line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                }
                System.out.println(line);
                queue.put(new KV<>(socket.hashCode(), line));
                System.out.println("added to queue");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

