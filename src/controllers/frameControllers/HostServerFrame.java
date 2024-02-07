package controllers.frameControllers;

import controllers.handlers.HandlerHostServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static controllers.Encoding.encrypt;

public class HostServerFrame {
    private JLabel chatServerLabel;
    private JTextArea chatOutputTextArea;
    public JPanel mainPanel;
    private JButton stopButton;
    public static LinkedList<String> messages = new LinkedList<String>();;
    private static final Set<PrintWriter> writers = new HashSet<>();

    public HostServerFrame() {
        actualizar();
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    HandlerHostServer.broadcastServerMessage("-- Server closed");
                    Thread.sleep(100);
                    MainFrame.serverSocket.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                MainFrame.hostServerFrame.dispose();
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
        if (HostServerFrame.messages.size() < (MainFrame.serverMessages.size())) {
            try {
                HostServerFrame.messages.add(MainFrame.serverMessages.getLast());
                chatOutputTextArea.append(HostServerFrame.messages.getLast() + "\n");
                System.out.println(HostServerFrame.messages.get(HostServerFrame.messages.size() - 1));
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


