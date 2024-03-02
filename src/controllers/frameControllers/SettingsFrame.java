package controllers.frameControllers;

import controllers.Streams;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
    private JLabel textPortServerLabel;
    private JTextField textPortServerField;
    private JTextField imagePortSenderServerField;
    private JTextField imagePortReceiverServerField;
    private JLabel imagePortSenderServerLabel;
    private JLabel imagePortReceiverServerLabel;
    private JTextField textPortClientField;
    private JTextField imagePortSenderClientField;
    private JTextField imagePortReceiverClientField;
    private JCheckBox autodestroyImagesServerCheckBox;
    private JCheckBox autodestroyImagesClientCheckBox;
    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;

    public SettingsFrame() {
        try{
            filesDownloadsServerPathField.setText(Streams.importarFilesDownloadsServerPath());
            filesDownloadsClientPathField.setText(Streams.importarFilesDownloadsClientPath());
            textPortServerField.setText(String.valueOf(Streams.importarTextPortServer()));
            imagePortSenderServerField.setText(String.valueOf(Streams.importarImagePortSenderServer()));
            imagePortReceiverServerField.setText(String.valueOf(Streams.importarImagePortReceiverServer()));
            if(Streams.importarAutodestroyImagesClient()) {
                autodestroyImagesClientCheckBox.setSelected(true);
            }
            if(Streams.importarAutodestroyImagesServer()) {
                autodestroyImagesServerCheckBox.setSelected(true);
            }
        }catch(Exception e){
            filesDownloadsServerPathField.setText("src/files/server/");
            filesDownloadsClientPathField.setText("src/files/client/");
            textPortServerField.setText("5555");
            imagePortSenderServerField.setText("2021");
            imagePortReceiverServerField.setText("2020");
            autodestroyImagesClientCheckBox.setSelected(true);
            autodestroyImagesServerCheckBox.setSelected(true);
        }

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(frame, "Do you want to exit this chat server?", "Exit confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Streams.exportarFilesDownloadsServerPath(filesDownloadsServerPathField.getText());
                        Streams.exportarFilesDownloadsClientPath(filesDownloadsClientPathField.getText());
                        Streams.exportarTextPortServer(Integer.parseInt(textPortServerField.getText()));
                        Streams.exportarImagePortSenderServer(Integer.parseInt(imagePortSenderServerField.getText()));
                        Streams.exportarImagePortReceiverServer(Integer.parseInt(imagePortReceiverServerField.getText()));
                        Streams.exportarAutodestroyImagesClient(autodestroyImagesClientCheckBox.isSelected());
                        Streams.exportarAutodestroyImagesServer(autodestroyImagesServerCheckBox.isSelected());
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