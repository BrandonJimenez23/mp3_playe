package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.Playlist;
import model.Song;
import services.MediaPlayerService;
import services.PlaylistService;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class MainController implements UIChangeListener {

    private MediaPlayerService mediaPlayerService = MediaPlayerService.getInstance();

    private PlaylistService playlistService = PlaylistService.getInstance();

    private SidebarNavigationViewController sidebarNavigationViewController;

    private PlaylistLibraryViewController playlistLibraryViewController;

    private SongListViewController songListViewController;

    private Playlist playlistActual;
    @FXML
    private VBox leftVBox;

    @FXML
    private VBox centerVBox;

    @FXML
    private Button playButton;

    @FXML
    private Button stopButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button previousButton;

    @FXML
    private Button addButton;

    @FXML
    private Label titleLabel;

    @FXML
    private Label artistLabel;

    @FXML
    private Label albumLabel;

    @FXML
    private Label genreLabel;

    @FXML
    private ImageView imageImageView;

    @FXML
    public void initialize() {
        // Add your code here
        loadSidebarNavigation();
        loadPlaylistLibrary();
        loadSongList();
        playlistLibraryViewController.setSelectionListener(this);
        sidebarNavigationViewController.setUIChangeListener(this);
        mediaPlayerService.setMetadataListener(this);
        playButton.setOnAction(e -> play());
        stopButton.setOnAction(e -> stop());
        previousButton.setOnAction(e -> previous());
        nextButton.setOnAction(e -> next());
        addButton.setOnAction(e -> addSong());
    }

    public void loadMediaMetadata(Song song) {
        titleLabel.setText(song.getTitle());
        artistLabel.setText(song.getArtist());
        albumLabel.setText(song.getAlbum());
        genreLabel.setText(song.getGenre());
        // Verifica si el arreglo de bytes de la imagen no está vacío
        if (song.getCoverImage() != null && song.getCoverImage().length > 0) {
            try {
                // Intenta cargar la imagen desde el arreglo de bytes
                Image image = new Image(new ByteArrayInputStream(song.getCoverImage()));
                imageImageView.setImage(image);
            } catch (Exception e) {
                // Imprime el error si no se puede cargar la imagen
                System.out.println("Error al cargar la imagen: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Opcional: establece una imagen predeterminada si no hay datos de imagen
            System.out.println("No hay datos de imagen disponibles.");
        }
    }

    private void loadSidebarNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/sidebarnavigationview.fxml"));
            AnchorPane sidebarNavigation = loader.load();
            sidebarNavigationViewController = loader.getController();
            leftVBox.getChildren().add(sidebarNavigation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPlaylistLibrary() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/playlistlibraryview.fxml"));
            AnchorPane playlistLibrary = loader.load();
            playlistLibraryViewController = loader.getController();
            leftVBox.getChildren().add(playlistLibrary);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSongList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/songlistview.fxml"));
            AnchorPane songList = loader.load();
            songListViewController = loader.getController();
            centerVBox.getChildren().add(songList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void play() {
        mediaPlayerService.play();
    }

    private void stop() {
        mediaPlayerService.stop();
    }

    private void previous() {
        mediaPlayerService.playPrevious();
    }

    private void next() {
        mediaPlayerService.playNext();
    }

    private void addSong() {

    }

    @Override
    public void onPlaylistSelected(Playlist playlist) {
        playlistActual = playlist;
        playlist = playlistService.loadSongPlaylist(playlist);
        playlist.getSongs().forEach(song -> System.out.println(song.getTitle()));
        onNavigationButtonClicked("songlistview");
        songListViewController.setUIChangeListener(this);
        
    }

    @Override
    public void onNavigationButtonClicked(String viewName) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + viewName + ".fxml"));
        try {
            Node view = loader.load();
            centerVBox.getChildren().clear();
            centerVBox.getChildren().add(view);
            // Asegúrate de actualizar songListViewController si estás cargando la vista de
            // la lista de canciones
            if ("songlistview".equals(viewName)) {
                songListViewController = loader.getController();
                songListViewController.setPlaylist(playlistActual); // Asume que tienes una referencia a la playlist
                                                                    // actual
                songListViewController.setUIChangeListener(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlaylistChanged() {
        // Add your code here
        playlistLibraryViewController.refreshPlaylists();
    }

    @Override
    public void onSongMetadataChanged(Song newSongMetadata) {
        loadMediaMetadata(newSongMetadata);
    }
}
