package controllers;

import java.io.*;
import java.util.ArrayList;

public class Streams {
    public static void exportarPreferences(ArrayList<String> preferences) throws IOException {
        FileOutputStream fileWriter = new FileOutputStream("src/resources/preferences");
        ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
        objectWriter.writeObject(preferences);
        objectWriter.close();
        fileWriter.close();
    }

    public static ArrayList<String> importarPreferences() throws IOException, ClassNotFoundException{
        ArrayList<String> preferences = new ArrayList<String>();
        FileInputStream fileReader = new FileInputStream("src/resources/preferences");
        ObjectInputStream objectReader = new ObjectInputStream(fileReader);
        preferences = (ArrayList<String>) objectReader.readObject();
        objectReader.close();
        fileReader.close();
        return preferences;
    }
}