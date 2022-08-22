import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;


public class ChatServerNotifier extends Thread {

    private List<Socket> clientSockets;
    private BlockingQueue<KV<Integer, String>> queue;
    private BlockingQueue<Socket> notifierQueue;
    private boolean running;

    public ChatServerNotifier(BlockingQueue<Socket> notifierQueue, BlockingQueue<KV<Integer, String>> queue) {
        clientSockets = new ArrayList<>();
        this.queue = queue;
        this.notifierQueue = notifierQueue;
        running = true;
    }

    @Override
    public void run() {
        while (running) {
            try {
                while (!notifierQueue.isEmpty()) {
                    clientSockets.add(notifierQueue.take());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                while (!queue.isEmpty()) {
                    System.out.println("queue not empty");
                    KV<Integer, String> value = queue.take();
                    clientSockets.forEach(s -> {
                        if(value.getKey() != s.hashCode() && !s.isClosed()) {
                            try {
                                DataOutputStream writer = new DataOutputStream(s.getOutputStream());
                                writer.writeBytes(value.getValue() + "\n\r");
                                writer.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        running = false;
    }
}

