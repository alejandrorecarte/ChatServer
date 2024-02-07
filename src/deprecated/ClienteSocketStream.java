package deprecated;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ClienteSocketStream {
    public static void main(String[] args) {

        String serverIP = "localhost";
        int serverPort = 5555;

        try (Socket socket = new Socket(serverIP, serverPort);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            Scanner sc = new Scanner(System.in);
            System.out.print("Username: ");
            String username = sc.nextLine();

            System.out.println("Connected to the server. Type 'exit' to quit.");

            Thread receiverThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = reader.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (SocketException e){
                    if(e.getMessage().equals("Socket closed"))
                    System.out.println("-- Exited from server.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiverThread.start();

            String userInput;
            while ((userInput = consoleReader.readLine()) != null) {
                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }
                writer.println("> " + username + ": " + userInput);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

