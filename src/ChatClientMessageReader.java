import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;


public class ChatClientMessageReader extends Thread {

    private Socket socket;
    private boolean running;

    public ChatClientMessageReader(Socket socket) {
        this.socket = socket;
        running = true;
    }

    @Override
    public void run() {
        while (running) {
            try {
                BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = socketReader.readLine();
                System.out.println(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        running = false;
    }
}

