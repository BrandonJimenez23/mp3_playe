package services;

import utils.FileUtils;
import java.io.File;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class MP3SearchService {

    private String searchPath;
    private String searchCriteria; // Atributo para el criterio de búsqueda

    private volatile boolean paused = false;
    private volatile boolean stopped = false;
    private ObservableList<File> mp3Files = FXCollections.observableArrayList();

    public MP3SearchService() {
        this.searchPath = FileUtils.getUserHomeDirectory(); // Path por defecto es el home del usuario
        this.searchCriteria = ".*"; // Criterio por defecto que coincide con cualquier archivo
    }

    // Métodos para pausar, reanudar y detener la búsqueda
    public void pauseSearch() {
        paused = true;
    }

    public void resumeSearch() {
        synchronized (this) {
            paused = false;
            notifyAll();
        }
    }
    public ObservableList<File> getMp3Files() {
        return mp3Files;
    }
    public void stopSearch() {
        stopped = true;
    }
    // Método para cambiar el directorio de búsqueda
    public void setSearchPath(String searchPath) {
        this.searchPath = searchPath;
    }

    // Método para establecer el criterio de búsqueda
    public void setSearchCriteria(String searchCriteria) {
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            this.searchCriteria = ".*"; // Criterio por defecto para todos los archivos
        } else {
            this.searchCriteria = searchCriteria.replace("?", ".?").replace("*", ".*");
        }
    }
    public void searchForMP3FilesInBackground() {
        System.out.println("Searching for MP3 files in: " + searchPath);
        Task<Void> searchTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                search(new File(searchPath));
                return null;
            }
    
            private void search(File directory) {
                if (stopped) return; // Salir si la búsqueda ha sido detenida
    
                if (directory.isDirectory()) {
                    File[] files = directory.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            // Comprobar si la búsqueda está pausada
                            synchronized (this) {
                                while (paused && !stopped) {
                                    try {
                                        wait();
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                }
                            }
                            if (stopped) return; // Comprobar nuevamente si la búsqueda ha sido detenida
                            if (file.isDirectory()) {
                                search(file); // Llamada recursiva para directorios
                            } else if (file.getName().endsWith(".mp3") && file.getName().matches(searchCriteria)) {
                                // Agregar si es un archivo MP3 válido y cumple con el criterio
                                Platform.runLater(() -> {
                                    synchronized (mp3Files) {
                                        mp3Files.add(file);
                                    }
                                });
                                System.out.println("MP3 file found: " + file.getAbsolutePath());
                                // Aquí puedes notificar a la UI sobre el nuevo archivo encontrado
                            }
                        }
                    }
                }
            }
        };
        // Ejecutar la tarea en un hilo de fondo
        Executors.newSingleThreadExecutor().execute(searchTask);
    }
}