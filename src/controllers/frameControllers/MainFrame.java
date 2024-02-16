package controllers.frameControllers;

import controllers.Streams;
import controllers.handlers.HandlerHostServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.*;
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
    private JLabel hostPasswordLabel;
    private JButton createServerButton;
    private JLabel joinAServerLabel;
    private JLabel hostIPLabel;
    private JPasswordField hostPasswordField;
    public JTextField joinIPField;
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
    public static Socket clientSocket;
    private BufferedReader clientReader;
    private PrintWriter clientWriter;
    private BufferedReader consoleReader;
    private ArrayList<String>[] profiles;
    public static String clientHashedPassword;
    public static String hostHashedPassword;
    public static String joinIP;
    private ServerSocket serverSocket;

    public static void startUI() {
        mainFrame = new JFrame("WideRoom");
        mainFrame.setContentPane(new MainFrame().mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setBounds(0,0,WIDTH,HEIGHT);
    }

    public MainFrame() {

        File filesDir = new File("src/files");
        if(!filesDir.exists()){
            filesDir.mkdir();
        }
        File clientDir = new File("src/files/client");
        if(!clientDir.exists()){
            clientDir.mkdir();
        }
        File serverDir = new File("src/files/server");
        if(!serverDir.exists()){
            serverDir.mkdir();
        }


        profiles = new ArrayList[10];
        try {
            profiles = Streams.importarPreferences();
            hostPasswordField.setText(profiles[profilesComboBox.getSelectedIndex()].get(0));
            joinIPField.setText(profiles[profilesComboBox.getSelectedIndex()].get(1));
            joinPasswordField.setText(profiles[profilesComboBox.getSelectedIndex()].get(2));
            joinUsernameField.setText(profiles[profilesComboBox.getSelectedIndex()].get(3));
        }catch(Exception e){
            for(int i = 0; i < profiles.length; i++){
                profiles[i] = new ArrayList<String>();
            }
            profiles[profilesComboBox.getSelectedIndex()].add(hostPasswordField.getText());
            profiles[profilesComboBox.getSelectedIndex()].add(joinIPField.getText());
            profiles[profilesComboBox.getSelectedIndex()].add(joinPasswordField.getText());
            profiles[profilesComboBox.getSelectedIndex()].add(joinUsernameField.getText());
            e.printStackTrace();
        }
        createServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!String.valueOf(hostPasswordField.getText()).equals("")) {
                    hostHashedPassword = hashPassword(hostPasswordField.getText());
                    controllers.frameControllers.HostServerFrame.startUI();

                    // Cerrar el servidor anterior si existe
                    if (serverSocket != null && !serverSocket.isClosed()) {
                        try {
                            serverSocket.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    // Iniciar un nuevo servidor
                    startServer();
                }
            }
        });;

        joinServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!String.valueOf(joinIPField.getText()).equals("") && !String.valueOf(joinPasswordField.getText()).equals("")  && !String.valueOf(joinUsernameField.getText()).equals("")) {
                    try {
                        clientHashedPassword = hashPassword(joinPasswordField.getText());
                        clientSocket = new Socket(joinIPField.getText(), Streams.importarTextPortClient());
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
                    profiles[profilesComboBox.getSelectedIndex()].set(0, hostPasswordField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].set(1, joinIPField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].set(2, joinPasswordField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].set(3, joinUsernameField.getText());
                    Streams.exportarPreferences(profiles);
                }catch (IndexOutOfBoundsException ex){
                    profiles[profilesComboBox.getSelectedIndex()] = new ArrayList<String>();
                    profiles[profilesComboBox.getSelectedIndex()].add(0, hostPasswordField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].add(1, joinIPField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].add(2, joinPasswordField.getText());
                    profiles[profilesComboBox.getSelectedIndex()].add(3, joinUsernameField.getText());
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
                    hostPasswordField.setText(profiles[profilesComboBox.getSelectedIndex()].get(0));
                    joinIPField.setText(profiles[profilesComboBox.getSelectedIndex()].get(1));
                    joinPasswordField.setText(profiles[profilesComboBox.getSelectedIndex()].get(2));
                    joinUsernameField.setText(profiles[profilesComboBox.getSelectedIndex()].get(3));
                } catch (IndexOutOfBoundsException ex) {
                    hostPasswordField.setText("");
                    joinIPField.setText("");
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

    private void startServer() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    serverSocket = new ServerSocket(Streams.importarTextPortServer());
                    serverMessages = new LinkedList<String>();
                    serverMessages.add("Server:Chat Server is running...");
                    HandlerHostServer handlerHostServer= new HandlerHostServer(serverSocket.accept(), writers);
                    handlerHostServer.start();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
            }
        };
        worker.execute();
    }

    private void joinServer() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    clientMessages = new LinkedList<String>();
                    String username = joinUsernameField.getText();
                        Thread receiverThread = new Thread(() -> {
                            try {
                                String serverMessage;
                                while ((serverMessage = clientReader.readLine()) != null) {
                                    clientMessages.add(serverMessage);
                                }
                            } catch (SocketException e) {
                                if (e.getMessage().equals("Socket closed"))
                                    clientMessages.add("Server:Exited from server.");
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

