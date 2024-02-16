package controllers.frameControllers;

import controllers.Streams;
import controllers.handlers.HandlerHostServer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static controllers.Encoding.encrypt;
import static controllers.frameControllers.MainFrame.mainFrame;

public class HostServerFrame {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    public static JFrame serverFrame;
    private JLabel wideRoomServerLabel;
    private JTextArea chatOutputTextArea;
    public JPanel mainPanel;
    private JButton stopButton;
    private JScrollPane chatOutputScrollPane;
    public static LinkedList<String> messages;
    private static final Set<PrintWriter> writers = new HashSet<>();

    public static void startUI(){
        serverFrame = new JFrame("WideRoom Server");
        serverFrame.setContentPane(new HostServerFrame().mainPanel);
        serverFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        serverFrame.pack();
        serverFrame.setVisible(true);
        serverFrame.setBounds(mainFrame.getX(), mainFrame.getY()+ MainFrame.HEIGHT, WIDTH, HEIGHT);
    }

    public HostServerFrame() {

        chatOutputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatOutputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        chatOutputScrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        chatOutputScrollPane.getVerticalScrollBar().setBackground(Color.decode("#4F4F4F"));
        messages = new LinkedList<String>();
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Handler().start();
            }
        });
        timer.start();
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    try {
                        if(Streams.importarAutodestroyImagesServer()) {
                            Path directorioPath = Paths.get(Streams.importarFilesDownloadsServerPath());

                            if (!Files.exists(directorioPath)) {
                                System.out.println("El directorio especificado no existe.");
                                return;
                            }
                            Files.walkFileTree(directorioPath, new SimpleFileVisitor<Path>() {
                                @Override
                                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                    if (Files.isRegularFile(file) && file.toString().toLowerCase().endsWith(".jpg")) {
                                        Files.delete(file);
                                        System.out.println("Archivo eliminado: " + file);
                                    }
                                    return FileVisitResult.CONTINUE;
                                }
                            });
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    HandlerHostServer.broadcastServerMessage("Server:Server closed");
                    timer.stop();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                serverFrame.dispose();

            }
        });

        serverFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(serverFrame, "Do you want to exit this chat server?", "Exit confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        if(Streams.importarAutodestroyImagesServer()) {
                            Path directorioPath = Paths.get(Streams.importarFilesDownloadsServerPath());

                            if (!Files.exists(directorioPath)) {
                                System.out.println("El directorio especificado no existe.");
                                return;
                            }
                            Files.walkFileTree(directorioPath, new SimpleFileVisitor<Path>() {
                                @Override
                                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                    if (Files.isRegularFile(file) && file.toString().toLowerCase().endsWith(".jpg")) {
                                        Files.delete(file);
                                        System.out.println("Archivo eliminado: " + file);
                                    }
                                    return FileVisitResult.CONTINUE;
                                }
                            });
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    serverFrame.dispose();
                } else {
                    serverFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
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

    static class CustomScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = Color.decode("#000000");
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            Dimension zeroDim = new Dimension(0, 0);
            button.setPreferredSize(zeroDim);
            button.setMinimumSize(zeroDim);
            button.setMaximumSize(zeroDim);
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Rellenar un rectángulo redondeado con el color del thumb
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);

            g2.dispose();
        }
    }
}


