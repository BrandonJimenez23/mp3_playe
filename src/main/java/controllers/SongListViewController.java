/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import model.Playlist;
import model.Song;
import services.MediaPlayerService;
import services.PlaylistService;
import services.SongService;
import javafx.scene.control.ButtonBar;

/**
 *
 * @author brandon
 */

public class SongListViewController {

    @FXML
    private TableView<Song> songsTableView = new TableView<>();
    @FXML
    private TableColumn<Song, String> titleColumn;
    @FXML
    private TableColumn<Song, String> idColumn;
    @FXML
    private TableColumn<Song, String> albumColumn;
    @FXML
    private TableColumn<Song, Integer> durationColumn;
    @FXML
    private Label playlistNameLabel;
    @FXML
    private TableColumn<Song, Void> addToPlaylistColumn;
    @FXML
    private Label createDateLabel;
    @FXML
    private Button playPlaylistButton;
    @FXML
    private Button deletePlaylistButton;
    @FXML
    private Button editPlaylistButton;

    private Playlist currentPlaylist;

    private UIChangeListener uic;

    private MediaPlayerService mediaPlayerService = MediaPlayerService.getInstance();

    private PlaylistService playlistService = PlaylistService.getInstance();

    private SongService songService = SongService.getInstance();

    @FXML
    public void initialize() {
        // Configura la columna del título para que muestre el título de la canción
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        // Configura la columna del artista (si tienes una)
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Configura la columna del álbum para que muestre el álbum de la canción
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));

        // Configura la columna de duración para que muestre la duración de la canción
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        addToPlaylistColumn.setCellValueFactory(new PropertyValueFactory<>(""));

        playPlaylistButton.setOnAction(event -> playPlaylist());
        deletePlaylistButton.setOnAction(event -> deletePlaylist());
        editPlaylistButton.setOnAction(event -> editPlaylist());

        songsTableView.setRowFactory(tv -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Song selectedSong = row.getItem();
                    Media media = new Media(selectedSong.getPath());
                    mediaPlayerService.load(media, selectedSong);
                }
            });
            return row;
        });

    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setPlaylist(Playlist playlist) {
        System.out.println("Setting playlist");
        currentPlaylist = playlist;
        playlistNameLabel.setText(currentPlaylist.getName());
        createDateLabel.setText(currentPlaylist.getCreateDate().toString());
        ObservableList<Song> songList = FXCollections.observableArrayList(currentPlaylist.getSongs());
        songsTableView.setItems(songList);
        addToPlaylistColumn.setCellFactory(column -> {
            TableCell<Song, Void> cell = new TableCell<Song, Void>() {
                private final Button addButton = new Button("Opciones");

                {
                    addButton.setOnAction(event -> {
                        Song currentSong = getTableView().getItems().get(getIndex());
                        showPlaylistContextMenu(currentSong, 0, 0); // Pasa las coordenadas al método
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(addButton);
                    }
                }
            };
            return cell;
        });
    }

    public void showPlaylistContextMenu(Song song, double x, double y) {
        ContextMenu contextMenu = new ContextMenu();

        // Opción para añadir a otra playlist
        MenuItem addMenuItem = new MenuItem("Añadir a otra playlist");
        addMenuItem.setOnAction(event -> showAddToPlaylistOptions(song));
        contextMenu.getItems().add(addMenuItem);

        // Opción para eliminar de esta playlist
        MenuItem deleteFromPlaylistMenuItem = new MenuItem("Eliminar de esta playlist");
        deleteFromPlaylistMenuItem.setOnAction(event -> confirmAndRemoveFromPlaylist(song));
        contextMenu.getItems().add(deleteFromPlaylistMenuItem);

        // Opción para borrar canción
        MenuItem deleteSongMenuItem = new MenuItem("Borrar canción");
        deleteSongMenuItem.setOnAction(event -> confirmAndDeleteSong(song));
        contextMenu.getItems().add(deleteSongMenuItem);

        // Muestra el menú contextual
        contextMenu.show(addToPlaylistColumn.getTableView(), Side.RIGHT, x, y);
    }

    private void showAddToPlaylistOptions(Song song) {
        List<Playlist> playlists = playlistService.getAllPlaylists();
        List<Playlist> availablePlaylists = new ArrayList<>();
        // Filtrar playlists donde la canción aún no ha sido agregada
        for (Playlist playlist : playlists) {
            if (!playlistService.isSongInPlaylist(song.getPath(), playlist)) {
                availablePlaylists.add(playlist);
            }
        }

        if (availablePlaylists.isEmpty()) {
            // Mostrar mensaje si la canción ya está en todas las playlists
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Canción ya agregada a todas las playlists.");
            alert.showAndWait();
        } else {
            // Crear y mostrar el menú con las playlists disponibles
            ContextMenu addToPlaylistMenu = new ContextMenu();
            for (Playlist playlist : availablePlaylists) {
                MenuItem playlistMenuItem = new MenuItem(playlist.getName());
                playlistMenuItem.setOnAction(event -> playlistService.addSongToPlaylist(playlist, song));
                addToPlaylistMenu.getItems().add(playlistMenuItem);
            }
            // Suponiendo que x y y son las coordenadas donde se debe mostrar el menú
            addToPlaylistMenu.show(addToPlaylistColumn.getTableView(), Side.TOP, 0, 0);
        }
    }

    private void confirmAndRemoveFromPlaylist(Song song) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Estás seguro de que quieres eliminar esta canción de la playlist?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // Aquí va la lógica para eliminar la canción de la playlist actual
                playlistService.removeSongFromPlaylist(currentPlaylist, song);
                refreshtable();
            }
        });
    }

    private void confirmAndDeleteSong(Song song) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de que quieres borrar esta canción?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // Aquí va la lógica para borrar la canción completamente
                songService.removeSong(song);
                refreshtable();
            }
        });
    }
    public void setUIChangeListener(UIChangeListener uic){
        this.uic = uic;
    }
    private void playPlaylist() {
        // Implementa la lógica para reproducir la playlist completa
        mediaPlayerService.loadPlaylist(currentPlaylist);
    }

    private void deletePlaylist() {
        // Implementa la lógica para eliminar la playlist
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de que quieres eliminar esta playlist?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                playlistService.removePlaylist(currentPlaylist);
                refreshtable();
                uic.onPlaylistChanged();
            }
        });
    }

    private void editPlaylist() {
        // Crear una nueva ventana de diálogo para editar la playlist
        Dialog<Playlist> dialog = new Dialog<>();
        dialog.setTitle("Editar Playlist");
        dialog.setHeaderText("Editar información de la playlist");

        // Configurar los botones de guardar y cancelar
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Crear los campos de texto para editar la información de la playlist
        TextField nameTextField = new TextField(currentPlaylist.getName());
        TextArea descriptionTextArea = new TextArea(currentPlaylist.getDescription());
        descriptionTextArea.setWrapText(true);

        // Crear un VBox para contener los campos de texto
        VBox content = new VBox();
        content.getChildren().addAll(
                new Label("Nombre:"),
                nameTextField,
                new Label("Descripción:"),
                descriptionTextArea);

        // Agregar el VBox al diálogo
        dialog.getDialogPane().setContent(content);

        // Validar los campos de texto antes de guardar
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });
        // Convertir el resultado del diálogo a una Playlist modificada
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Playlist editedPlaylist = new Playlist();
                editedPlaylist.setName(nameTextField.getText());
                editedPlaylist.setDescription(descriptionTextArea.getText());
                return editedPlaylist;
            }
            return null;
        });

        // Mostrar el diálogo y obtener el resultado
        Optional<Playlist> result = dialog.showAndWait();
        result.ifPresent(editedPlaylist -> {
            // Actualizar la información de la playlist
            currentPlaylist.setName(editedPlaylist.getName());
            currentPlaylist.setDescription(editedPlaylist.getDescription());
            // Guardar los cambios en la playlist
            playlistService.updatePlaylist(currentPlaylist);
            // Mostrar una alerta de éxito
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Playlist actualizada exitosamente.");
            successAlert.showAndWait();
            refreshtable();
            uic.onPlaylistChanged();
        });
    }
    private void refreshtable(){
        currentPlaylist = playlistService.loadSongPlaylist(playlistService.getPlaylistById(currentPlaylist.getId()));   
        setPlaylist(currentPlaylist);
    }
}
