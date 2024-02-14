package views;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MessagePanel extends JPanel{
    private JLabel usernameLabel;
    private JLabel messageLabel;

    public MessagePanel(String username, String message, Color color) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(color);
        usernameLabel = new JLabel(username);
        messageLabel = new JLabel(message);

        usernameLabel.setForeground(Color.WHITE);
        messageLabel.setForeground(Color.WHITE);
        add(usernameLabel);
        add(messageLabel);
    }

    public void setColor(Color color){
        setBackground(color);
    }

    public JLabel getUsernameLabel() {
        return usernameLabel;
    }

    public void setUsernameLabel(JLabel usernameLabel) {
        this.usernameLabel = usernameLabel;
    }

    public JLabel getMessageLabel() {
        return messageLabel;
    }

    public void setMessageLabel(JLabel messageLabel) {
        this.messageLabel = messageLabel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar borde redondeado
        int width = getWidth();
        int height = getHeight();
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(getBackground());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int arc = 20;
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(0, 0, width - 1, height - 1, arc, arc);
        g2d.draw(round);
        g2d.dispose();
    }
}
