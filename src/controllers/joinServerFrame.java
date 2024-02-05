package controllers;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.util.LinkedList;

public class joinServerFrame {
    public JPanel mainPanel;
    private JLabel chatServerLabel;
    private JButton sendButton;
    private JTextArea messageTextArea;
    private JTextArea chatOutputTextArea;
    public String username;
    private static LinkedList<String> messages;
    private boolean access;
    private SwingWorker<Void, Void> worker;
    private PrintWriter writer;

    public joinServerFrame(String username, PrintWriter writer) {
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

        messageTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    sendMessage(writer);
                    messageTextArea.setText("");
                }
            }
        });
    }

    private void sendMessage(PrintWriter writer){
        if(!messageTextArea.getText().equals("")) {
            writer.println("> " + username + ": " + messageTextArea.getText());
            mainFrame.clientMessages.add("> " + username + ": " + messageTextArea.getText());
            messageTextArea.setText("");
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
        if (controllers.joinServerFrame.messages.size() < (controllers.mainFrame.clientMessages.size())) {
            try {
                controllers.joinServerFrame.messages.add(controllers.mainFrame.clientMessages.getLast());
                chatOutputTextArea.append(controllers.joinServerFrame.messages.getLast() + "\n");
                chatOutputTextArea.repaint();
                chatOutputTextArea.revalidate();
                Thread.sleep(200);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkPassword() {
        if (!access) {
            try {
                Thread.sleep(100);
                if (!controllers.mainFrame.clientMessages.getLast().split(" ")[1].equals(controllers.mainFrame.clientHashedPassword)) {
                    JOptionPane.showMessageDialog(mainFrame.clientServerFrame, "The password is incorrect", "Error", JOptionPane.ERROR_MESSAGE);
                    controllers.mainFrame.clientServerFrame.dispose();
                    return false;
                } else {
                    access = true;
                    chatOutputTextArea.setText("");
                    messages = new LinkedList<String>();
                    mainFrame.clientMessages = new LinkedList<String>();
                    mainFrame.clientMessages.add("Connected to the server. Type 'exit' to quit.");
                    sendMessage("-- " + username + " joined the server.", writer);
                    controllers.mainFrame.clientServerFrame.setVisible(true);
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
