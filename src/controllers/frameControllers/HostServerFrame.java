package controllers.frameControllers;

import controllers.handlers.HandlerHostServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static controllers.frameControllers.MainFrame.WIDTH;
import static controllers.frameControllers.MainFrame.mainFrame;

public class HostServerFrame {
    public static JFrame hostFrame;
    private JLabel wideRoomServerLabel;
    private JTextArea chatOutputTextArea;
    public JPanel mainPanel;
    private JButton stopButton;
    private JScrollPane chatOutputScrollPane;
    public static LinkedList<String> messages = new LinkedList<String>();;
    private static final Set<PrintWriter> writers = new HashSet<>();

    public static void startUI(){
        hostFrame = new JFrame("Wide Room Server");
        hostFrame.setContentPane(new HostServerFrame().mainPanel);
        hostFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        hostFrame.pack();
        hostFrame.setVisible(true);
        hostFrame.setBounds(mainFrame.getX() + WIDTH, mainFrame.getY(), 600, 400);
    }

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
                hostFrame.dispose();
            }
        });
    }

    public void actualizar() {
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Handler().start();
            }
        });
        timer.start();
    }

    public synchronized void actualizarChat(){
        if (HostServerFrame.messages.size() < (MainFrame.serverMessages.size())) {
            try {
                JScrollBar verticalScrollBar = chatOutputScrollPane.getVerticalScrollBar();
                boolean keepBottom = false;
                double oldValue = verticalScrollBar.getSize().getHeight() + verticalScrollBar.getValue();
                if(oldValue >= verticalScrollBar.getMaximum() - 7){
                    keepBottom = true;
                }
                HostServerFrame.messages.add(MainFrame.serverMessages.getLast());
                chatOutputTextArea.append(HostServerFrame.messages.getLast() + "\n");
                System.out.println(HostServerFrame.messages.get(HostServerFrame.messages.size() - 1));
                chatOutputTextArea.repaint();
                chatOutputTextArea.revalidate();
                Thread.sleep(10);

                if(keepBottom){
                    verticalScrollBar.setValue(verticalScrollBar.getMaximum());
                }

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


