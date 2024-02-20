package controllers.frameControllers;

import controllers.Encoding;
import controllers.Streams;
import models.Servidor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModifyServerFrame {
    private static JFrame frame;
    private JPanel mainPanel;
    private JLabel addANewServerLabel;
    private JLabel IPLabel;
    private JLabel textPortLabel;
    private JLabel imagePortSenderLabel;
    private JLabel imagePortReceiverLabel;
    private JTextField ipField;
    private JTextField textPortField;
    private JTextField imagePortSenderField;
    private JTextField imagePortReceiverField;
    private JPasswordField passwordField;
    private JLabel passwordLabel;
    private JButton modifyButton;
    private JTextField serverNameField;
    private JLabel serverNameLabel;

    public static void startUI(Servidor servidor) {
        frame = new JFrame("Modify Server");
        frame.setContentPane(new ModifyServerFrame(servidor).mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setBounds(MainFrame.mainFrame.getX(), MainFrame.mainFrame.getY(), 400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public ModifyServerFrame(Servidor servidor) {
        serverNameField.setText(servidor.getName());
        ipField.setText(servidor.getIp());
        textPortField.setText(String.valueOf(servidor.getTextPort()));
        imagePortSenderField.setText(String.valueOf(servidor.getImagePortSender()));
        imagePortReceiverField.setText(String.valueOf(servidor.getImagePortReceiver()));


        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.servidores.remove(servidor);
                Servidor servidor = new Servidor(serverNameField.getText(), ipField.getText(), Integer.valueOf(textPortField.getText()),
                        Integer.valueOf(imagePortSenderField.getText()), Integer.valueOf(imagePortReceiverField.getText()),
                        Encoding.hashPassword(passwordField.getText()));
                MainFrame.servidores.add(servidor);
                try{
                    Streams.exportarServidores(MainFrame.servidores);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                frame.dispose();
            }
        });
    }
}
