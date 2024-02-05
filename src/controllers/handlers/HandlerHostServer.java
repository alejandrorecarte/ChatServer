package controllers.handlers;

import controllers.mainFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

public class HandlerHostServer extends Thread {
    private final Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private static Set<PrintWriter> writers;

    public HandlerHostServer(Socket socket, Set<PrintWriter> writers) {
        this.socket = socket;
        this.writers = writers;
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
                try {
                    if (message.split("/")[1].equals("requestHashedPassword")) {
                        writer.println("HashedPassword: " + mainFrame.hostHashedPassword);
                    }
                }catch(ArrayIndexOutOfBoundsException e){
                    System.out.println("(" +socket.getInetAddress() + ")" + message);
                    controllers.mainFrame.serverMessages.add("(" +socket.getInetAddress() + ")" + message);
                    broadcast(message, writer);
                    Thread.sleep(100);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
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


    private static void broadcast(String message, PrintWriter messageWriter) {
        for (PrintWriter writer : writers) {
            if(!writer.equals(messageWriter)){
                writer.println(message);
            }
        }
    }
}