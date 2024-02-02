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

    public joinServerFrame(String username, PrintWriter writer) {
        this.username = username;
        messages  = new LinkedList<String>();
        actualizar();
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

    public void actualizar() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    while (true) {
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
                System.out.println(controllers.joinServerFrame.messages.get(controllers.joinServerFrame.messages.size() - 1));
                chatOutputTextArea.repaint();
                chatOutputTextArea.revalidate();
                Thread.sleep(200);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
