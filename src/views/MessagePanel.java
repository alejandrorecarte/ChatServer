package views;

import javax.swing.*;

public class MessagePanel extends JPanel{
    private JLabel usernameLabel;
    private JLabel messageLabel;

    public MessagePanel(String username, String message) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        usernameLabel = new JLabel(username);
        messageLabel = new JLabel(message);

        add(usernameLabel);
        add(messageLabel);
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
}
