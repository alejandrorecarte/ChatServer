package views;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ImageChooserComponent extends JPanel {

    private ImageIcon icon;
    private String path;
    private JButton chooseButton;

    public ImageChooserComponent() {
        setLayout(new BorderLayout());

        chooseButton = new JButton("Seleccionar imagen");
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crea un selector de archivos
                JFileChooser fileChooser = new JFileChooser();

                // Establece un filtro para mostrar solo archivos de imagen
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de imagen", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(filter);

                // Muestra el diálogo de selección de archivo
                int result = fileChooser.showOpenDialog(ImageChooserComponent.this);

                // Si el usuario elige un archivo y hace clic en "Abrir"
                if (result == JFileChooser.APPROVE_OPTION) {
                    // Obtiene el archivo seleccionado
                    File selectedFile = fileChooser.getSelectedFile();

                    // Crea un icono de imagen a partir del archivo seleccionado
                    icon = new ImageIcon(selectedFile.getAbsolutePath());

                    path = fileChooser.getSelectedFile().getAbsolutePath();
                    chooseButton.setText("Imagen seleccionada");
                }
            }
        });
        add(chooseButton, BorderLayout.NORTH);
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public void setText(String text){
        chooseButton.setText(text);
    }
}
