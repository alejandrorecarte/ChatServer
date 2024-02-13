package controllers;

import java.io.*;
import java.util.ArrayList;

public class Streams {
    public static void exportarPreferences(ArrayList<String>[] preferences) throws IOException {
        FileOutputStream fileWriter = new FileOutputStream("src/resources/preferences");
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(preferences);
        objectWriter.close();
        fileWriter.close();
    }

    public static ArrayList<String>[] importarPreferences() throws IOException, ClassNotFoundException{
        ArrayList<String>[] preferences = new ArrayList[10];
        FileInputStream fileReader = new FileInputStream("src/resources/preferences");
        ObjectInputStream objectReader = new ObjectInputStream(fileReader);
        preferences = (ArrayList<String>[]) objectReader.readObject();
        objectReader.close();
        fileReader.close();
        return preferences;
    }

    public static void exportarFilesDownloadsServerPath(String path) throws IOException {
        FileOutputStream fileWriter = new FileOutputStream("src/resources/filesdownloadsserverpath");
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(path);
        objectWriter.close();
        fileWriter.close();
    }

    public static String importarFilesDownloadsServerPath() throws IOException, ClassNotFoundException{
        String path = new String();
        FileInputStream fileReader = new FileInputStream("src/resources/filesdownloadsserverpath");
        ObjectInputStream objectReader = new ObjectInputStream(fileReader);
        path = (String) objectReader.readObject();
        objectReader.close();
        fileReader.close();
        return path;
    }

    public static void exportarFilesDownloadsClientPath(String path) throws IOException {
        FileOutputStream fileWriter = new FileOutputStream("src/resources/filesdownloadsclientpath");
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(path);
        objectWriter.close();
        fileWriter.close();
    }

    public static String importarFilesDownloadsClientPath() throws IOException, ClassNotFoundException{
        String path = new String();
        FileInputStream fileReader = new FileInputStream("src/resources/filesdownloadsclientpath");
        ObjectInputStream objectReader = new ObjectInputStream(fileReader);
        path = (String) objectReader.readObject();
        objectReader.close();
        fileReader.close();
        return path;
    }
}