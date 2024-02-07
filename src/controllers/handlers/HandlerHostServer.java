package controllers.handlers;

import controllers.frameControllers.MainFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;

import static controllers.Encoding.*;

public class HandlerHostServer extends Thread {
    private final Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private static Set<PrintWriter> writers;
    private static ArrayList<String> connectedUsers;

    public HandlerHostServer(Socket socket, Set<PrintWriter> writers) {
        connectedUsers = new ArrayList<String>();
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
                boolean isCommand = false;
                try {
                    if (message.split("/")[1].equals("requestHashedPassword")) {
                        writer.println("HashedPassword: " + MainFrame.hostHashedPassword);
                        isCommand = true;
                    }
                }catch(ArrayIndexOutOfBoundsException e){}
                if(!isCommand) {
                    message = decrypt(message, MainFrame.hostHashedPassword);
                    MainFrame.serverMessages.add("(" + socket.getInetAddress() + ")" + message);
                    broadcast(message, writer);
                    try {
                        if (message.split("/")[1].equals("list")) {
                            String users = "-- Connected users: ";
                            for (int i = 0; i < connectedUsers.size(); i++) {
                                users += "\n" + connectedUsers.get(i);
                            }
                            writer.println(encrypt(users, MainFrame.hostHashedPassword));
                        }
                    }catch(ArrayIndexOutOfBoundsException e){}
                    try {

                        if (message.split(" ")[2].equals("joined")) {
                            String ip = message.split(" ")[0];
                            String ipSplitted[] = ip.split("");
                            if ((ipSplitted[ipSplitted.length - 1] + ipSplitted[ipSplitted.length - 2]).equals("--")) {
                                connectedUsers.add(message.split(" ")[1]);
                            }
                        }if (message.split(" ")[2].equals("left")) {
                            String ip = message.split(" ")[0];
                            String ipSplitted[] = ip.split("");
                            if ((ipSplitted[ipSplitted.length - 1] + ipSplitted[ipSplitted.length - 2]).equals("--")) {
                                connectedUsers.remove(message.split(" ")[1]);
                            }
                        }
                    }catch (ArrayIndexOutOfBoundsException e){}
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
                writer.println(encrypt(message, MainFrame.hostHashedPassword));
            }
        }
    }

    public static void broadcastServerMessage(String message){
        for (PrintWriter writer : writers) {
            writer.println(encrypt(message, MainFrame.hostHashedPassword));
        }
    }
}