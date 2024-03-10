package controllers;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;

public class Misc {

    public static void removeClientImages(){
        try {
            if (Streams.importarAutodestroyImagesClient()) {
                Path directorioPath = Paths.get(Streams.importarFilesDownloadsClientPath());

                if (!Files.exists(directorioPath)) {
                    System.out.println("El directorio especificado no existe.");
                    return;
                }
                Files.walkFileTree(directorioPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (Files.isRegularFile(file) && file.toString().toLowerCase().endsWith(".jpg")) {
                            Files.delete(file);
                            System.out.println("Archivo eliminado: " + file);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void removeServerImages(){
        try {

            if(Streams.importarAutodestroyImagesServer()) {
                Path directorioPath = Paths.get(Streams.importarFilesDownloadsServerPath());

                if (!Files.exists(directorioPath)) {
                    System.out.println("El directorio especificado no existe.");
                    return;
                }
                Files.walkFileTree(directorioPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (Files.isRegularFile(file) && file.toString().toLowerCase().endsWith(".jpg")) {
                            Files.delete(file);
                            System.out.println("Archivo eliminado: " + file);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getDate(){
        Calendar calendar = Calendar.getInstance();
        String dateStr = "";
        dateStr += calendar.get(Calendar.DAY_OF_MONTH) + "/";
        dateStr += calendar.get(Calendar.MONTH) + "/";
        dateStr += calendar.get(Calendar.YEAR) + " ";

        if(calendar.get(Calendar.HOUR_OF_DAY) < 10){
            dateStr += "0" + calendar.get(Calendar.HOUR_OF_DAY) + ":";
        }else dateStr += calendar.get(Calendar.HOUR_OF_DAY) + ":";
        if(calendar.get(Calendar.MINUTE) < 10){
            dateStr += "0" + calendar.get(Calendar.MINUTE);
        }else dateStr += calendar.get(Calendar.MINUTE);

        return dateStr;
    }
}
