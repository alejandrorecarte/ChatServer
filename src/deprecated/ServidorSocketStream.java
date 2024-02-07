package deprecated;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class ServidorSocketStream {
    private static final int PORT = 5555;
    private static final Set<PrintWriter> writers = new HashSet<>();

    public static synchronized void startServer() {
        System.out.println("Chat Server is running...");
    }

    private static class Handler extends Thread {
        private final Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);
                writers.add(writer);

                while (true) {
                    String message = reader.readLine();
                    if (message == null) {
                        return;
                    }
                    System.out.println(message);
                    broadcast(message, writer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    writers.remove(writer);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void broadcast(String message, PrintWriter messageWriter) {
        for (PrintWriter writer : writers) {
            if(writer.equals(messageWriter)){
                writer.println("-- Message Sent");
            }else{
                writer.println(message);
            }
        }
    }
}
