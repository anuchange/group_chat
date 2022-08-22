import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatClient {

    private Socket socket;
    private String name;
    private boolean running;
    private Thread reader;

    public ChatClient(String name) throws IOException {
        socket = new Socket("localhost",6666);
        this.name = name;
        running = true;
        reader = new ChatClientMessageReader(socket);
        registerShutDownHook();
    }

    private void registerShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    socket.close();
                    running = false;
                    reader.interrupt();
                    System.out.println("Client down");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void start() {

        reader.start();
        boolean commandSent = false;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            while(running) {
                if(!commandSent) {
                    System.out.println("Please add your message");
                    commandSent = true;
                }
                try {
                    String line = reader.readLine();
//                    System.out.println("Sending : " + line);
                    writer.writeBytes("Message sent from : " + name + " " + line  + "\n\r");
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient(args[0]);
        client.start();
    }
}
