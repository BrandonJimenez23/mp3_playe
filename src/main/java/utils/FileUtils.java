package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileUtils {

    /**
     * Permite seleccionar un archivo MP3 a través de un FileChooser.
     *
     * @return Path del archivo MP3 seleccionado, o null si no se seleccionó ningún archivo.
     */
    public static Path selectMP3File() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo MP3");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            return selectedFile.toPath();
        }

        return null;
    }

    /**
     * Normaliza una URL de archivo según el sistema operativo.
     *
     * @param url URL a normalizar
     * @return URL normalizada
     */
    public static String normalizeURL(String url) {
        // Elimina la unidad en Windows (ej. "C:")
        if (isWindows()) {
            url = url.replaceFirst("[A-Z]:", "");
        }
        // No es necesario modificar para Linux
        return url;
    }

    /**
     * Verifica si el sistema operativo actual es Windows.
     *
     * @return true si el sistema operativo es Windows, false en caso contrario.
     */
    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static byte[] getDefaultImageBytes() throws IOException {
        Path imagePath = Paths.get("src/main/resources/images/playlistDefaultImage.png");
        return Files.readAllBytes(imagePath);
    }

    public static String getUserHomeDirectory() {
        return System.getProperty("user.home");
    }

    // Método para verificar si un archivo es un MP3 válido
    public static boolean isValidMP3File(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = new byte[3];
            if (fis.read(bytes) == 3) {
                String header = new String(bytes);
                return header.equals("ID3") || (bytes[0] == (byte)0xFF && bytes[1] == (byte)0xFB);
            }
        } catch (IOException e) {
            // Manejar excepción si es necesario
        }
        return false;
    }

    public static File chooseDirectory() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar directorio");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName("Carpeta de música");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Directorios", "*"));
        return fileChooser.showSaveDialog(stage);
    }
}
