package controllers.frameControllers;

import controllers.Misc;
import controllers.Streams;
import controllers.handlers.HandlerHostServer;
import models.Servidor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
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
    private JPasswordField hostPasswordField;
    private JButton joinServerButton;
    private JPanel mainPanel;
    private JComboBox profilesComboBox;
    private JLabel wideRoomLabel;
    private JButton settingsButton;
    private JButton addServerButton;
    private JScrollPane serversScrollPane;
    private JPanel serversPanel;
    private JButton removeServerButton;
    private JButton modifyServerButton;
    private JTextField usernameField;
    private JLabel usernameLabel;
    private JButton actualizarListaButton;
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
    public static ArrayList<Servidor> servidores;
    public static Servidor servidorEscogido;

    public static void startUI() {
        mainFrame = new JFrame("WideRoom");
        mainFrame.setContentPane(new MainFrame().mainPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setBounds(0,0,WIDTH,HEIGHT);
    }

    public MainFrame() {
        try{
            usernameField.setText(Streams.importarUsername());
        }catch(Exception ex){
        }

        try{
            servidores = Streams.importarServidores();
        }catch (Exception e){
            servidores = new ArrayList<Servidor>();
        }

        actualizarServerList();

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
                if(!String.valueOf(usernameField.getText()).equals("") && servidorEscogido != null) {
                    try {
                        clientHashedPassword = servidorEscogido.getHashedPassword();
                        clientSocket = new Socket(servidorEscogido.getIp(), servidorEscogido.getTextPort());
                        clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                        consoleReader = new BufferedReader(new InputStreamReader(System.in));
                        controllers.frameControllers.JoinServerFrame.startUI(usernameField.getText().replace(" ", ""), clientWriter);
                        clientMessages = new LinkedList<String>();
                        joinIP = servidorEscogido.getIp();
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

        hostPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    createServerButton.doClick();
                }
            }
        });

        addServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddServerFrame.startUI();
            }
        });

        removeServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                servidores.remove(servidorEscogido);
                servidorEscogido = null;
                actualizarServerList();
                try {
                    Streams.exportarServidores(servidores);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        modifyServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(servidorEscogido != null){
                    ModifyServerFrame.startUI(servidorEscogido);
                }
            }
        });

        actualizarListaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarServerList();
            }
        });

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                try {
                    Streams.exportarUsername(usernameField.getText());
                    Misc.removeClientImages();
                    Misc.removeServerImages();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    public void actualizarServerList() {
        try {
            serversPanel.removeAll();
            serversPanel.setLayout(new BoxLayout(serversPanel, BoxLayout.Y_AXIS));
            for (int i = 0; i < servidores.size(); i++) {
                final int finalI = i;
                JButton b = new JButton(servidores.get(i).getName());
                serversPanel.add(b);
                b.setBackground(Color.decode("#272727"));
                b.setForeground(Color.WHITE);
                b.setFocusPainted(false);
                b.setMaximumSize(new Dimension(Integer.MAX_VALUE, b.getPreferredSize().height));

                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for(int i = 0; i < serversPanel.getComponentCount(); i++){
                            serversPanel.getComponent(i).setBackground(Color.decode("#272727"));
                            serversPanel.getComponent(i).setForeground(Color.WHITE);
                        }
                        b.setBackground(Color.ORANGE);
                        b.setForeground(Color.BLACK);
                        servidorEscogido = servidores.get(finalI);
                        modifyServerButton.setEnabled(true);
                        removeServerButton.setEnabled(true);
                        joinServerButton.setEnabled(true);
                    }
                });
            }

            mainFrame.revalidate();
            mainFrame.repaint();
        }catch(Exception e){
            System.out.println("Lista de servidores vacía");
        }
    }

    private void startServer() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    serverSocket = new ServerSocket(Streams.importarTextPortServer());
                    serverMessages = new LinkedList<String>();
                    serverMessages.add("Server:Chat Server is running...");
                    while (true) {
                        new HandlerHostServer(serverSocket.accept(), writers).start();
                    }
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
                    String username = usernameField.getText();
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

