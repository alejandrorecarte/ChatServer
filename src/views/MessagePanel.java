package views;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class MessagePanel extends JPanel {
    private JLabel usernameLabel;
    private JPanel messagesPanel; // Panel para contener los mensajes

    private ArrayList<JLabel> messageLabels = new ArrayList<>();

    public MessagePanel(String username, String message, Color color) {
        setLayout(new BorderLayout());
        setBackground(color);
        setOpaque(false); // Establecer opacidad a falso para que se pueda ver el fondo redondeado

        // Panel para el nombre de usuario
        usernameLabel = new JLabel(username);
        usernameLabel.setForeground(Color.WHITE);
        add(usernameLabel, BorderLayout.NORTH);

        // Panel para los mensajes
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setOpaque(false); // Establecer opacidad a falso para que se pueda ver el fondo redondeado
        add(messagesPanel, BorderLayout.CENTER);

        addMessage(message);
    }

    public String getUsername(){
        return usernameLabel.getText();
    }

    public void addMessage(String message){
        if(!message.contains("File saved into ")) {
            while (message.length() > 50) {
                addMessage(message.substring(0, 50));
                message = message.substring(50, message.length());
            }
        }
        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(Color.WHITE);
        messagesPanel.add(messageLabel);
        messageLabels.add(messageLabel);
        revalidate(); // Revalidar el componente para actualizar la disposición
        repaint(); // Volver a pintar el componente
    }

    public ArrayList<JLabel> getMessageLabels() {
        return messageLabels;
    }

    public void setMessageLabels(ArrayList<JLabel> messageLabels) {
        this.messageLabels = messageLabels;
    }

    public void setColor(Color color) {
        setBackground(color);
    }

    public JLabel getUsernameLabel() {
        return usernameLabel;
    }

    public void setUsernameLabel(JLabel usernameLabel) {
        this.usernameLabel = usernameLabel;
    }

    public JPanel getMessagesPanel() {
        return messagesPanel;
    }

    public void setMessagesPanel(JPanel messagesPanel) {
        this.messagesPanel = messagesPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(getBackground());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = 20;
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(0, 0, width - 1, height - 1, arc, arc);
        g2d.fill(round); // Rellenar el fondo con el color redondeado
        g2d.dispose();
        super.paintComponent(g); // Llamar a la implementación original de paintComponent para pintar el resto de los componentes
    }
}
