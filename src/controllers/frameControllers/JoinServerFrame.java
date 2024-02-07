package controllers.frameControllers;

import javax.swing.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.LinkedList;

import static controllers.Encoding.decrypt;
import static controllers.Encoding.encrypt;

public class JoinServerFrame {
    public JPanel mainPanel;
    private JLabel chatServerLabel;
    private JButton sendButton;
    private JTextArea chatOutputTextArea;
    private JTextField chatInputField;
    public String username;
    private static LinkedList<String> messages;
    private boolean access;
    private SwingWorker<Void, Void> worker;
    private PrintWriter writer;

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

        MainFrame.clientServerFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(MainFrame.clientServerFrame, "Do you want to exit this chat server?", "Exit confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    sendMessage(encrypt("-- " + username + " left the server.", MainFrame.clientHashedPassword), writer);
                } else {
                    MainFrame.clientServerFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
                JoinServerFrame.messages.add(MainFrame.clientMessages.getLast());
                chatOutputTextArea.append(decrypt(JoinServerFrame.messages.getLast(), MainFrame.clientHashedPassword) + "\n");
                chatOutputTextArea.repaint();
                chatOutputTextArea.revalidate();
                Thread.sleep(100);

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
                    JOptionPane.showMessageDialog(MainFrame.clientServerFrame, "The password is incorrect", "Error", JOptionPane.ERROR_MESSAGE);
                    MainFrame.clientServerFrame.dispose();
                    return false;
                } else {
                    access = true;
                    chatOutputTextArea.setText("");
                    messages = new LinkedList<String>();
                    MainFrame.clientMessages = new LinkedList<String>();
                    MainFrame.clientMessages.add(encrypt("Connected to the server. Type 'exit' to quit.", MainFrame.clientHashedPassword));
                    sendMessage(encrypt("-- " + username + " joined the server.", MainFrame.clientHashedPassword), writer);
                    MainFrame.clientServerFrame.setVisible(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true; // Devuelve true si la contraseña es correcta
    }

    private class Handler extends Thread {

        public Handler() {
        }

        @Override

        public void run() {
            actualizarChat();
        }
    }
}
