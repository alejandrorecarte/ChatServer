package models;

import java.io.Serializable;

public class Servidor implements Serializable {

    private String name;
    private String ip;
    private int textPort;
    private int imagePortSender;
    private int imagePortReceiver;
    private String hashedPassword;

    public Servidor(String name, String ip, int textPort, int imagePortSender, int imagePortReceiver, String hashedPassword) {
        this.name = name;
        this.ip = ip;
        this.textPort = textPort;
        this.imagePortSender = imagePortSender;
        this.imagePortReceiver = imagePortReceiver;
        this.hashedPassword = hashedPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getTextPort() {
        return textPort;
    }

    public void setTextPort(int textPort) {
        this.textPort = textPort;
    }

    public int getImagePortSender() {
        return imagePortSender;
    }

    public void setImagePortSender(int imagePortSender) {
        this.imagePortSender = imagePortSender;
    }

    public int getImagePortReceiver() {
        return imagePortReceiver;
    }

    public void setImagePortReceiver(int imagePortReceiver) {
        this.imagePortReceiver = imagePortReceiver;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
