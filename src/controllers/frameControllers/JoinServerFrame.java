package controllers.frameControllers;

import org.w3c.dom.ls.LSOutput;
import views.ImageChooserComponent;
import views.MessagePanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
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
import static controllers.frameControllers.MainFrame.*;

public class JoinServerFrame {
    public static JFrame clientFrame;
    public JPanel mainPanel;
    private JLabel wideRoomClientLabel;
    private JButton sendButton;
    private JTextField chatInputField;
    private JScrollPane chatOutputScrollPane;
    private JScrollPane chatInputScrollPane;
    private ImageChooserComponent imageChooserComponent;
    private JPanel messagesPanel;
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
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        access = false;
        messages  = new LinkedList<String>();
        actualizar();
        sendMessage("/requestHashedPassword", writer);

        sendButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(!chatInputField.getText().equals("")) {
                    sendMessage(writer);
                    chatInputField.setText("");
                }
                if(imageChooserComponent.getPath()!=null){
                    writer.println(encrypt("Server: " + username + " sent an image.", MainFrame.clientHashedPassword));
                    MainFrame.clientMessages.add(encrypt("Server: " + username + " sent an image.", MainFrame.clientHashedPassword));
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
                    imageChooserComponent.getChooseButton().setText("Seleccionar imagen");
                }
            }
        });

        imageChooserComponent.getChooseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chatInputField.requestFocus();
            }
        });

        chatInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    sendButton.doClick();
                }
            }
        });

       clientFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(clientFrame, "Do you want to exit this chat server?", "Exit confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    sendMessage(encrypt("Server: " + username + " left the server.", MainFrame.clientHashedPassword), writer);
                } else {
                    clientFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    private void sendMessage(PrintWriter writer){
        if(!chatInputField.getText().equals("")) {
            writer.println(encrypt(username + ": " + chatInputField.getText(), MainFrame.clientHashedPassword));
            MainFrame.clientMessages.add(encrypt(username + ": " + chatInputField.getText(), MainFrame.clientHashedPassword));
            chatInputField.setText("");
        }
    }

    public void sendMessage(String message, PrintWriter writer){
        writer.println(message);
    }

    public void actualizar() {
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkPassword()) {
                    new Handler().start();
                }else{
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }
    public synchronized void actualizarChat(){
        if (JoinServerFrame.messages.size() < (MainFrame.clientMessages.size())) {
            try {
                JScrollBar verticalScrollBar = chatOutputScrollPane.getVerticalScrollBar();
                boolean keepBottom = false;
                double oldValue = verticalScrollBar.getSize().getHeight() + verticalScrollBar.getValue();
                if(oldValue >= verticalScrollBar.getMaximum() - 15){
                    keepBottom = true;
                }
                JoinServerFrame.messages.add(MainFrame.clientMessages.getLast());
                String message = decrypt(JoinServerFrame.messages.getLast(), MainFrame.clientHashedPassword).split(":")[1];
                String username = decrypt(JoinServerFrame.messages.getLast(), MainFrame.clientHashedPassword).split(":")[0];

                try {
                    MessagePanel messagePanel = new MessagePanel(username, message);
                    messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                    messagesPanel.add(messagePanel);
                    System.out.println(username);
                    if(!username.equals("Server")){
                        messagePanel.getMessageLabel().setFont(new Font("arial", Font.PLAIN, 12));
                        messagePanel.getMessageLabel().setBackground(Color.CYAN);
                    }if(username.equals("Local")){
                        ImageIcon image = new ImageIcon(messagePanel.getMessageLabel().getText().split(" ")[4]);
                        Image imageScaled = image.getImage().getScaledInstance(200,200, Image.SCALE_SMOOTH);
                        ImageIcon scaledImageIcon = new ImageIcon(imageScaled);
                        messagePanel.getMessageLabel().setText("");
                        messagePanel.getMessageLabel().setIcon(scaledImageIcon);
                        messagePanel.getMessageLabel().repaint();
                    }
                }catch (ArrayIndexOutOfBoundsException ex){
                }

                clientFrame.revalidate();
                clientFrame.repaint();

                try {
                    if (decrypt(MainFrame.clientMessages.getLast(), MainFrame.clientHashedPassword).split(" ")[2].equals("sent")) {
                        ServerSocket imageSocketServer = new ServerSocket(2021);
                        Socket imageSocket = imageSocketServer.accept();
                        Thread handlerThread = new Thread(new ImageConnectionHandler(imageSocket, decrypt(MainFrame.clientMessages.getLast(), MainFrame.clientHashedPassword).split(" ")[1]));
                        handlerThread.start();
                    }
                }catch (SocketException e) {
                    e.printStackTrace();
                }catch(Exception e){
                }

                Thread.sleep(100);

                if(keepBottom){
                    SwingUtilities.invokeLater(() -> {
                        JScrollBar newVerticalScrollBar = chatOutputScrollPane.getVerticalScrollBar();
                        newVerticalScrollBar.setValue(newVerticalScrollBar.getMaximum());
                    });

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
                    messagesPanel.removeAll();
                    messages = new LinkedList<String>();
                    MainFrame.clientMessages = new LinkedList<String>();
                    MainFrame.clientMessages.add(encrypt("Server: Welcome " + username + " to the server!", MainFrame.clientHashedPassword));
                    sendMessage(encrypt("Server: " + username + " joined the server.", MainFrame.clientHashedPassword), writer);
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
                MainFrame.clientMessages.add(encrypt("Local: Archivo guardado en " + fileName, MainFrame.clientHashedPassword));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
