package controllers.frameControllers;

import controllers.Encoding;
import controllers.Streams;
import models.Servidor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddServerFrame {
    private static JFrame frame;
    private JLabel IPLabel;
    private JLabel textPortLabel;
    private JLabel addANewServerLabel;
    private JTextField ipField;
    private JTextField textPortField;
    private JTextField imagePortSenderField;
    private JTextField imagePortReceiverField;
    private JPasswordField passwordField;
    private JButton addButton;
    private JLabel imagePortSenderLabel;
    private JLabel imagePortReceiverLabel;
    private JLabel passwordLabel;
    private JPanel mainPanel;
    private JTextField serverNameField;
    private JLabel serverNameLabel;

    public static void startUI() {
        frame = new JFrame("Add Server");
        frame.setContentPane(new AddServerFrame().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setBounds(MainFrame.mainFrame.getX(), MainFrame.mainFrame.getY(), 400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public AddServerFrame() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
