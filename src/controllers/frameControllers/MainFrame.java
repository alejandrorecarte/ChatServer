package controllers.frameControllers;

import controllers.Streams;
import controllers.handlers.HandlerHostServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import static controllers.Encoding.*;

public class MainFrame {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;

    public static JFrame mainFrame;
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
    public JTextField joinIPField;
    private JTextField joinPortField;
    private JPasswordField joinPasswordField;
    private JLabel joinUsernameLabel;
    private JTextField joinUsernameField;
    private JButton joinServerButton;
    private JLabel joinPasswordLabel;
    private JPanel mainPanel;
    private JButton savePreferencesButton;
    private JComboBox profilesComboBox;
    private JLabel wideRoomLabel;
    private JButton settingsButton;
    public static LinkedList<String> serverMessages = new LinkedList<String>();
    public static LinkedList<String> clientMessages = new LinkedList<String>();
    private static final Set<PrintWriter> writers = new HashSet<>();
    public static ServerSocket serverSocket;
    public static Socket clientSocket;
    private BufferedReader clientReader;
    private PrintWriter clientWriter;
    private BufferedReader consoleReader;
    private ArrayList<String>[] profiles;
    public static String clientHashedPassword;
    public static String hostHashedPassword;
    public static String joinIP;


    public MainFrame() {
        profiles = new ArrayList[10];
        try {
            profiles = Streams.importarPreferences();
            hostPortField.setText(profiles[profilesComboBox.getSelectedIndex()].get(0));
            hostPasswordField.setText(profiles[profilesComboBox.getSelectedIndex()].get(1));
            joinIPField.setText(profiles[profilesComboBox.getSelectedIndex()].get(2));
            joinPortField.setText(profiles[profilesComboBox.getSelectedIndex()].get(3));
            joinPasswordField.setText(profiles[profilesComboBox.getSelectedIndex()].get(4));
            joinUsernameField.setText(profiles[profilesComboBox.getSelectedIndex()].get(5));
        }catch(Exception e){
            for(int i = 0; i < profiles.length; i++){
                profiles[i] = new ArrayList<String>();
            }
            profiles[profilesComboBox.getSelectedIndex()].add(hostPortField.getText());
            profiles[profilesComboBox.getSelectedIndex()].add(hostPasswordField.getText());
            profiles[profilesComboBox.getSelectedIndex()].add(joinIPField.getText());
            profiles[profilesComboBox.getSelectedIndex()].add(joinPortField.getText());
            profiles[profilesComboBox.getSelectedIndex()].add(joinPasswordField.getText());
            profiles[profilesComboBox.getSelectedIndex()].add(joinUsernameField.getText());
            e.printStackTrace();
        }
        createServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if( !String.valueOf(hostPortField.getText()).equals("") && !String.valueOf(hostPasswordField.getText()).equals("")) {
                    hostHashedPassword = hashPassword(hostPasswordField.getText());
                    controllers.frameControllers.HostServerFrame.startUI();
                    serverMessages = new LinkedList<String>();
                    startServer();
                }
            }
        });

        joinServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!String.valueOf(joinIPField.getText()).equals("") && !String.valueOf(joinPortField.getText()).equals("") && !String.valueOf(joinPasswordField.getText()).equals("")  && !String.valueOf(joinUsernameField.getText()).equals("")) {
                    try {
                        clientHashedPassword = hashPassword(joinPasswordField.getText());
                        clientSocket = new Socket(joinIPField.getText(), Integer.parseInt(joinPortField.getText()));
                        clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                        consoleReader = new BufferedReader(new InputStreamReader(System.in));
                        controllers.frameControllers.JoinServerFrame.startUI(joinUsernameField.getText().replace(" ", ""), clientWriter);
                        clientMessages = new LinkedList<String>();
                        joinIP = joinIPField.getText();
                        joinServer();
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controllers.frameControllers.SettingsFrame.startUI();
            }
        });

        savePreferencesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    profiles[profilesComboBox.getSelectedIndex()] = new ArrayList<String>();
                    profiles[profilesComboBox.getSelectedIndex()].set(0, hostPortField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].set(1, hostPasswordField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].set(2, joinIPField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].set(3, joinPortField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].set(4, joinPasswordField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].set(5, joinUsernameField.getText());
                    Streams.exportarPreferences(profiles);
                }catch (IndexOutOfBoundsException ex){
                    profiles[profilesComboBox.getSelectedIndex()] = new ArrayList<String>();
                    profiles[profilesComboBox.getSelectedIndex()].add(0, hostPortField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].add(1, hostPasswordField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].add(2, joinIPField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].add(3, joinPortField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].add(4, joinPasswordField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].add(5, joinUsernameField.getText());
                    try {
                        Streams.exportarPreferences(profiles);
                    } catch (IOException exc) {
                        exc.printStackTrace();
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        profilesComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    hostPortField.setText(profiles[profilesComboBox.getSelectedIndex()].get(0));
                    hostPasswordField.setText(profiles[profilesComboBox.getSelectedIndex()].get(1));
                    joinIPField.setText(profiles[profilesComboBox.getSelectedIndex()].get(2));
                    joinPortField.setText(profiles[profilesComboBox.getSelectedIndex()].get(3));
                    joinPasswordField.setText(profiles[profilesComboBox.getSelectedIndex()].get(4));
                    joinUsernameField.setText(profiles[profilesComboBox.getSelectedIndex()].get(5));
                } catch (IndexOutOfBoundsException ex) {
                    hostPortField.setText("");
                    hostPasswordField.setText("");
                    joinIPField.setText("");
                    joinPortField.setText("");
                    joinPasswordField.setText("");
                    joinUsernameField.setText("");
                }
            }
        });

        hostPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    createServerButton.doClick();
                }
            }
        });


        joinPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    joinServerButton.doClick();
                }
            }
        });
    }

    public static void startUI() {
        mainFrame = new JFrame("Chat Server");
        mainFrame.setContentPane(new MainFrame().mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setBounds(0,0,WIDTH,HEIGHT);
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
                        serverMessages.add("-- Server closed");
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

                        Thread receiverThread = new Thread(() -> {
                            try {
                                String serverMessage;
                                while ((serverMessage = clientReader.readLine()) != null) {
                                    clientMessages.add(serverMessage);
                                }
                            } catch (SocketException e) {
                                if (e.getMessage().equals("Socket closed"))
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

