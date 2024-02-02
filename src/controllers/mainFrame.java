package controllers;

import controllers.handlers.HandlerHostServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class mainFrame {
    private JPanel hostPanel;
    private JPanel joinPanel;
    private JLabel hostAChatServerLabel;
    private JTextField hostIpField;
    private JTextField hostPortField;
    private JLabel yourHostPortLabel;
    private JLabel hostPasswordLabel;
    private JButton createServerButton;
    private JLabel joinAServerLabel;
    private JLabel hostIPLabel;
    private JPasswordField hostPasswordField;
    private JLabel hostPortLabel;
    private JTextField joinIPField;
    private JTextField joinPortField;
    private JPasswordField joinPasswordField;
    private JLabel joinUsernameLabel;
    private JTextField joinUsernameField;
    private JButton joinServerButton;
    private JLabel joinPasswordLabel;
    private JPanel mainPanel;
    private JButton savePreferencesButton;
    public static LinkedList<String> serverMessages = new LinkedList<String>();
    public static LinkedList<String> clientMessages = new LinkedList<String>();
    private static final Set<PrintWriter> writers = new HashSet<>();
    public static ServerSocket serverSocket;
    public static JFrame hostServerFrame;

    public static JFrame clientServerFrame;

    private Socket clientSocket;
    private BufferedReader clientReader;
    private PrintWriter clientWriter;
    private BufferedReader consoleReader;
    private ArrayList<String> preferences;


    public mainFrame() {
        try {
            preferences = Streams.importarPreferences();
            hostPortField.setText(preferences.get(0));
            hostPasswordField.setText(preferences.get(1));
            joinIPField.setText(preferences.get(2));
            joinPortField.setText(preferences.get(3));
            joinPasswordField.setText(preferences.get(4));
            joinUsernameField.setText(preferences.get(5));
        }catch(Exception e){
            preferences = new ArrayList<String>();
            preferences.add(hostPortField.getText());
            preferences.add(hostPasswordField.getText());
            preferences.add(joinIPField.getText());
            preferences.add(joinPortField.getText());
            preferences.add(joinPasswordField.getText());
            preferences.add(joinUsernameField.getText());

            e.printStackTrace();
        }
        createServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if( !String.valueOf(hostPortField.getText()).equals("") && !String.valueOf(hostPasswordField.getText()).equals("")) {
                    hostServerFrame = new JFrame("Chat Server");
                    hostServerFrame.setContentPane(new hostServerFrame().mainPanel);
                    hostServerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    hostServerFrame.pack();
                    hostServerFrame.setVisible(true);
                    hostServerFrame.setBounds(0, 0, 600, 400);

                    startServer();
                }
            }
        });

        joinServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!String.valueOf(joinIPField.getText()).equals("") && !String.valueOf(joinPortField.getText()).equals("") && !String.valueOf(joinPasswordField.getText()).equals("")  && !String.valueOf(joinUsernameField.getText()).equals("")) {
                    try {
                        clientSocket = new Socket(joinIPField.getText(), Integer.parseInt(joinPortField.getText()));
                        clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                        consoleReader = new BufferedReader(new InputStreamReader(System.in));
                        hostServerFrame = new JFrame("Chat Client");
                        hostServerFrame.setContentPane(new joinServerFrame(joinUsernameField.getText(), clientWriter).mainPanel);
                        hostServerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        hostServerFrame.pack();
                        hostServerFrame.setVisible(true);
                        hostServerFrame.setBounds(0, 0, 600, 400);
                        joinServer();
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });

        savePreferencesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    preferences.set(0, hostPortField.getText());
                    preferences.set(1, hostPasswordField.getText());
                    preferences.set(2, joinIPField.getText());
                    preferences.set(3, joinPortField.getText());
                    preferences.set(4, joinPasswordField.getText());
                    preferences.set(5, joinUsernameField.getText());
                    Streams.exportarPreferences(preferences);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chat Server");
        frame.setContentPane(new mainFrame().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setBounds(0,0,600,400);
    }

    private void startServer() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    serverSocket = new ServerSocket(Integer.parseInt(hostPortField.getText()));
                    serverMessages.add("Chat Server is running...");
                    while (true) {
                        new HandlerHostServer(serverSocket.accept(), writers).start();
                    }
                } catch (SocketException e) {
                    if(e.getMessage().equals("Interrupted function call: accept failed")){
                        serverMessages.add("Server closed");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                // Puedes realizar acciones después de que el servidor haya terminado
                // Esto se ejecutará en el hilo de despacho de eventos de Swing
            }
        };
        worker.execute();
    }

    private void joinServer() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {

                    String username = joinUsernameField.getText();

                    clientMessages.add("Connected to the server. Type 'exit' to quit.");

                    Thread receiverThread = new Thread(() -> {
                        try {
                            String serverMessage;
                            while ((serverMessage = clientReader.readLine()) != null) {
                                clientMessages.add(serverMessage);
                            }
                        } catch (SocketException e){
                            if(e.getMessage().equals("Socket closed"))
                                clientMessages.add("-- Exited from server.");
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
                        clientWriter.println("> " + username + ": " + userInput);
                        clientMessages.add("> " + username + ": " + userInput);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                // Puedes realizar acciones después de que el servidor haya terminado
                // Esto se ejecutará en el hilo de despacho de eventos de Swing
            }
        };
        worker.execute();
    }
}

