package controllers.frameControllers;

import controllers.Streams;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static controllers.Encoding.encrypt;

public class SettingsFrame {
    private static JFrame frame;
    private JPanel mainPanel;
    private JLabel filesDownloadsClientPathLabel;
    private JTextField filesDownloadsClientPathField;
    private JLabel clientSettingsLabel;
    private JTextField filesDownloadsServerPathField;
    private JLabel filesDownloadsServerPathLabel;
    private JLabel wideRoomSettingsLabel;
    private JLabel serverSettingsLabel;
    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;
    public static String filesDownloadsServerPath;
    public static String filesDownloadsClientPath;

    public SettingsFrame() {
        try{
            filesDownloadsServerPathField.setText(Streams.importarFilesDownloadsServerPath());
            filesDownloadsClientPathField.setText(Streams.importarFilesDownloadsClientPath());
        }catch(Exception e){
            filesDownloadsServerPathField.setText("src/files/server/");
            filesDownloadsClientPathField.setText("src/files/client/");
        }

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(frame, "Do you want to exit this chat server?", "Exit confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Streams.exportarFilesDownloadsServerPath(filesDownloadsServerPathField.getText());
                        Streams.exportarFilesDownloadsClientPath(filesDownloadsClientPathField.getText());
                    }catch(Exception ex){
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error", "Error exporting settings infromation", JOptionPane.ERROR_MESSAGE);
                        frame.dispose();
                    }
                } else {
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    public static void startUI() {
        frame = new JFrame("WideRoom Settings");
        frame.setContentPane(new SettingsFrame().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setBounds(0,0, WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}