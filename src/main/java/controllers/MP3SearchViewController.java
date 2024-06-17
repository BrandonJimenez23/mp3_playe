package controllers;

import java.io.File;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import model.Song;
import services.MP3SearchService;
import services.MediaPlayerService;
import services.SongService;
import utils.FileUtils;

public class MP3SearchViewController {

    @FXML
    private Label inicialPathLabel;
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button selectPathButton;
    @FXML
    private ListView<File> fileListview;
    @FXML
    private Button stopSearchButton;

    private MediaPlayerService mediaPlayerService = MediaPlayerService.getInstance();

    private SongService songService = SongService.getInstance();

    private String initialPath;

    private MP3SearchService mp3SearchService = new MP3SearchService();
    @FXML
    public void initialize() {

        fileListview.setCellFactory(lv -> new ListCell<File>() {
            @Override
            protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                if (empty || file == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/searchitemview.fxml"));
                        Node view = loader.load();
                        SearchItemViewController controller = loader.getController();
                        setGraphic(view);
                        controller.setFile(file);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        fileListview.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                File selectedFile = fileListview.getSelectionModel().getSelectedItem();
                if(selectedFile != null) {
                    Song song = songService.extractMetadata(selectedFile.toURI().toString());
                    Media media = new Media(song.getPath());
                    mediaPlayerService.load(media, song); 
                }else{
                    System.out.println("File not loaded");
                }
                
            }
        });
        // Vincular la ObservableList mp3Files con el ListView para mostrar los archivos encontrados.
        fileListview.setItems(mp3SearchService.getMp3Files());
        initialPath = FileUtils.getUserHomeDirectory();
        inicialPathLabel.setText(initialPath);
        searchButton.setOnAction(searchEvent -> search());
        selectPathButton.setOnAction(selectPathEvent -> selectPath());
        stopSearchButton.setOnAction(stopSearchEvent -> stopSearch());
        stopSearchButton.setDisable(true);
        // Definir cómo se mostrarán los archivos en la ListView
        
    }
    public void search() {
        // Add the following code
        String query = searchTextField.getText();
        mp3SearchService.setSearchPath(initialPath);
        mp3SearchService.setSearchCriteria(query);
        searchButton.setDisable(true);
        stopSearchButton.setDisable(false);
        mp3SearchService.searchForMP3FilesInBackground();
        
    }
    public void pauseSearch() {
        mp3SearchService.pauseSearch();
    }
    
    public void resumeSearch() {
        mp3SearchService.resumeSearch();
    }
    
    public void stopSearch() {
        mp3SearchService.stopSearch();
        searchButton.setDisable(false);
        stopSearchButton.setDisable(true);
    }
    private void selectPath() {
        // Add the following code
        File selectedDirectory = FileUtils.chooseDirectory();
        if (selectedDirectory != null) {
            initialPath = selectedDirectory.getAbsolutePath();
            inicialPathLabel.setText(initialPath);
        }
    }
}
