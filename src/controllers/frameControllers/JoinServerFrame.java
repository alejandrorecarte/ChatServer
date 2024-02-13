package controllers.frameControllers;

import org.w3c.dom.ls.LSOutput;
import views.ImageChooserComponent;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import static controllers.Encoding.decrypt;
import static controllers.Encoding.encrypt;
import static controllers.frameControllers.MainFrame.HEIGHT;
import static controllers.frameControllers.MainFrame.mainFrame;

public class JoinServerFrame {
    public static JFrame clientFrame;
    public JPanel mainPanel;
    private JLabel wideRoomClientLabel;
    private JButton sendButton;
    private JTextArea chatOutputTextArea;
    private JTextField chatInputField;
    private JScrollPane chatOutputScrollPane;
    private JScrollPane chatInputScrollPane;
    private ImageChooserComponent imageChooserComponent;
    public String username;
    private static LinkedList<String> messages;
    private boolean access;
    private SwingWorker<Void, Void> worker;
    private PrintWriter writer;

    public static void startUI(String username, PrintWriter writer) {
        clientFrame = new JFrame("Wide Room Client");
        clientFrame.setContentPane(new JoinServerFrame(username, writer).mainPanel);
        clientFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        clientFrame.pack();
        clientFrame.setBounds(mainFrame.getX(), mainFrame.getY() + HEIGHT, 600, 400);
    }

    public JoinServerFrame(String username, PrintWriter writer) {
        this.username = username;
        this.writer = writer;
        access = false;
        messages  = new LinkedList<String>();
        actualizar();
        sendMessage("/requestHashedPassword", writer);

        sendButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                sendMessage(writer);
                if(imageChooserComponent.getPath()!=null){
                    writer.println(encrypt("-- " + username + " sent an image.", MainFrame.clientHashedPassword));
                    MainFrame.clientMessages.add(encrypt("-- " + username + " sent an image.", MainFrame.clientHashedPassword));
                    try (Socket imageSocket = new Socket(MainFrame.joinIP, 2020);
                         OutputStream outputStream = imageSocket.getOutputStream();
                         FileInputStream fileInputStream = new FileInputStream(imageChooserComponent.getPath())) {

                        Thread.sleep(100);

                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    imageChooserComponent.setPath(null);
                    imageChooserComponent.setText("Seleccionar imagen");
                }
            }
        });

        chatInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    sendMessage(writer);
                    chatInputField.setText("");
                }
            }
        });

       clientFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(clientFrame, "Do you want to exit this chat server?", "Exit confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    sendMessage(encrypt("-- " + username + " left the server.", MainFrame.clientHashedPassword), writer);
                } else {
                    clientFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    private void sendMessage(PrintWriter writer){
        if(!chatInputField.getText().equals("")) {
            writer.println(encrypt("> " + username + ": " + chatInputField.getText(), MainFrame.clientHashedPassword));
            MainFrame.clientMessages.add(encrypt("> " + username + ": " + chatInputField.getText(), MainFrame.clientHashedPassword));
            chatInputField.setText("");
        }
    }

    public void sendMessage(String message, PrintWriter writer){
        writer.println(message);
    }

    public void actualizar() {
        worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    while (true) {;
                        if (!checkPassword()) {
                            break;
                        }
                        new Handler().start();
                    }
                } catch (Exception e) {
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

    public synchronized void actualizarChat(){
        if (JoinServerFrame.messages.size() < (MainFrame.clientMessages.size())) {
            try {
                JScrollBar verticalScrollBar = chatOutputScrollPane.getVerticalScrollBar();
                boolean keepBottom = false;
                double oldValue = verticalScrollBar.getSize().getHeight() + verticalScrollBar.getValue();
                if(oldValue >= verticalScrollBar.getMaximum() - 7){
                    keepBottom = true;
                }
                JoinServerFrame.messages.add(MainFrame.clientMessages.getLast());
                chatOutputTextArea.append(decrypt(JoinServerFrame.messages.getLast(), MainFrame.clientHashedPassword) + "\n");
                chatOutputTextArea.repaint();
                chatOutputTextArea.revalidate();

                try {
                    if (decrypt(MainFrame.clientMessages.getLast(), MainFrame.clientHashedPassword).split(" ")[2].equals("sent")) {
                        System.out.println("Creando Imagen Socket");
                        ServerSocket imageSocketServer = new ServerSocket(2021);
                        Socket imageSocket = imageSocketServer.accept();
                        Thread handlerThread = new Thread(new ImageConnectionHandler(imageSocket, decrypt(MainFrame.clientMessages.getLast(), MainFrame.clientHashedPassword).split(" ")[1]));
                        handlerThread.start();
                    }
                }catch (SocketException e) {
                    e.printStackTrace();
                }catch(Exception e){}

                Thread.sleep(100);

                if(keepBottom){
                    verticalScrollBar.setValue(verticalScrollBar.getMaximum());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkPassword() {
        if (!access) {
            try {
                Thread.sleep(100);
                if (!MainFrame.clientMessages.getLast().split(" ")[1].equals(MainFrame.clientHashedPassword)) {
                    JOptionPane.showMessageDialog(clientFrame, "The password is incorrect", "Error", JOptionPane.ERROR_MESSAGE);
                    clientFrame.dispose();
                    return false;
                } else {
                    access = true;
                    chatOutputTextArea.setText("");
                    messages = new LinkedList<String>();
                    MainFrame.clientMessages = new LinkedList<String>();
                    MainFrame.clientMessages.add(encrypt("Connected to the server.", MainFrame.clientHashedPassword));
                    sendMessage(encrypt("-- " + username + " joined the server.", MainFrame.clientHashedPassword), writer);
                    clientFrame.setVisible(true);
                }
            } catch (NoSuchElementException e) {
                System.out.println("Esperando al servidor...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true; // Devuelve true si la contraseña es correcta
    }

    private void createUIComponents() {
        this.imageChooserComponent = new ImageChooserComponent();
    }

    private class Handler extends Thread {

        public Handler() {
        }

        @Override

        public void run() {
            actualizarChat();
        }
    }

    static class ImageConnectionHandler implements Runnable {
        private Socket socket;
        private String sender;

        public ImageConnectionHandler(Socket socket, String sender) {
            this.socket = socket;
            this.sender = sender;
        }

        @Override
        public void run() {
            System.out.println("Cargando imagen");
            try {
                InputStream inputStream = socket.getInputStream();
                Calendar calendar = Calendar.getInstance();
                String fileName = "src/files/client/image" + sender + calendar.get(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.MONTH)
                        + calendar.get(Calendar.YEAR) + "_" + calendar.get(Calendar.HOUR) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) + ".jpg";
                FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                byte[] receiveBuffer = new byte[1024];
                int receiveBytesRead;

                while ((receiveBytesRead = inputStream.read(receiveBuffer)) != -1) {
                    fileOutputStream.write(receiveBuffer, 0, receiveBytesRead);
                }
                MainFrame.clientMessages.add(encrypt("-- Archivo guardado en " + fileName, MainFrame.clientHashedPassword));
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Imagen descargada");
        }
    }
}
