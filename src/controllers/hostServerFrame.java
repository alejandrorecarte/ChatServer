package controllers;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class hostServerFrame {
    private JLabel chatServerLabel;
    private JTextArea chatOutputTextArea;
    public JPanel mainPanel;
    private JButton stopButton;
    public static LinkedList<String> messages = new LinkedList<String>();;
    private static final Set<PrintWriter> writers = new HashSet<>();

    public hostServerFrame() {
        actualizar();
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    mainFrame.serverSocket.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                mainFrame.hostServerFrame.dispose();
            }
        });
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
        if (controllers.hostServerFrame.messages.size() < (controllers.mainFrame.serverMessages.size())) {
            try {
                controllers.hostServerFrame.messages.add(controllers.mainFrame.serverMessages.getLast());
                chatOutputTextArea.append(controllers.hostServerFrame.messages.getLast() + "\n");
                System.out.println(controllers.hostServerFrame.messages.get(controllers.hostServerFrame.messages.size() - 1));
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


