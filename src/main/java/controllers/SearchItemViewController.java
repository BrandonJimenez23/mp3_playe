package controllers;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Playlist;
import model.Song;
import services.PlaylistService;
import services.SongService;
import utils.FileUtils;

public class SearchItemViewController {


    @FXML
    private Label titleLabel;

    @FXML
    private Label filePathLabel;

    @FXML 
    private Button addSongToPlaylistButton;

    @FXML
    private ImageView imageView;

    private PlaylistService playlistService = PlaylistService.getInstance();

    private SongService songService = SongService.getInstance();

    @FXML
    public void initialize() {
        // Implementar la lógica de inicialización aquí

    }

    public void setFile(File file) {
        titleLabel.setText(file.getName());
        filePathLabel.setText(file.toURI().toString());
        addSongToPlaylistButton.setOnAction(event -> onAddSongToPlaylistButtonClick());
        try {
            Image image = new Image(new ByteArrayInputStream(FileUtils.getDefaultImageBytes()));
            if(image != null){
                imageView.setImage(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onAddSongToPlaylistButtonClick() {
        showPlaylistContextMenu();
    }

    private void showPlaylistContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        List<Playlist> playlists = playlistService.getAllPlaylists(); // Asume que tienes una instancia de PlaylistService
        for (Playlist playlist : playlists) {
            MenuItem menuItem = new MenuItem(playlist.getName());
            menuItem.setOnAction(event -> addSongToPlaylist(playlist, filePathLabel.getText()));
            contextMenu.getItems().add(menuItem);
        }
        contextMenu.show(addSongToPlaylistButton, Side.BOTTOM, 0, 0); // Ajusta la posición según necesites
    }
    private void addSongToPlaylist(Playlist playlist, String songPath) {
        // Implementa la lógica para agregar la canción a la playlist
        System.out.println("Agregando canción a la playlist");
        Song song = null;
        if(!songService.isSongInDatabase(songPath)){
            System.out.println("La canción no está en la base de datos");
            song = songService.extractMetadata(songPath);
            songService.addSong(song);
            song = songService.getSongByPath(songPath); // Retrieve the song again after adding it to the database
        } else {
            System.out.println("La canción está en la base de datos");
            song = songService.getSongByPath(songPath);
            System.out.println("Canción: " + song.getId() + song.getTitle() + song.getDuration());
        }
        if(playlistService.isSongInPlaylist(songPath, playlist)){
            System.out.println("La canción ya está en la playlist");
            return;
        }else{
            System.out.println("Agregando canción a la playlist");
            playlistService.addSongToPlaylist(playlist, song);
        }
        
    }
    
}
