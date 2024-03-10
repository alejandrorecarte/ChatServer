package views;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class MessagePanel extends JPanel {
    private JLabel usernameLabel;
    private JPanel messagesPanel; // Panel para contener los mensajes

    private ArrayList<JLabel> messageLabels = new ArrayList<>();

    private Color foreground;

    public MessagePanel(String username, String message, String date, Color color, Color foreground) {
        this.foreground = foreground;

        setLayout(new BorderLayout());
        setBackground(color);
        setOpaque(false); // Establecer opacidad a falso para que se pueda ver el fondo redondeado

        // Panel para el nombre de usuario
        usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("arial", Font.BOLD, 18));
        usernameLabel.setForeground(foreground);
        add(usernameLabel, BorderLayout.NORTH);

        // Panel para los mensajes
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setOpaque(false); // Establecer opacidad a falso para que se pueda ver el fondo redondeado
        add(messagesPanel, BorderLayout.CENTER);

        addMessage(message);
        addDate(date);
    }

    public String getUsername(){
        return usernameLabel.getText();
    }

    public void addDate(String date){
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("arial", Font.ITALIC, 10));
        dateLabel.setForeground(foreground);
        messagesPanel.add(dateLabel);
        messageLabels.add(dateLabel);
    }

    public void addMessage(String message){
        if(!message.contains("File saved into ")) {
            while (message.length() > 50) {
                addMessage(message.substring(0, 50));
                message = message.substring(50, message.length());
            }
        }
        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(foreground);
        messageLabel.setFont(new Font("arial", Font.PLAIN, 16));
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

    public void setColor(Color background, Color foreground) {
        this.foreground = foreground;
        setBackground(background);

        usernameLabel.setForeground(foreground);
        for(int i = 0; i < messageLabels.size(); i++){
            messageLabels.get(i).setForeground(foreground);
        }
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
