package controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import services.PlaylistService;
import model.Playlist;

public class PlaylistLibraryViewController {

    @FXML
    private ListView<Playlist> playlistListView;
    @FXML
    private VBox playlistItemVBox;
    @FXML
    private Button addPlaylistButton;

    private UIChangeListener selectionListener;

    private PlaylistService playlistService = PlaylistService.getInstance();

    public void initialize() {
        // Cargar las playlists y mostrarlas en playlistListView
        addPlaylistButton.setOnAction(event -> handleAddPlaylist());
        loadPlaylists();
        setupPlaylistContextMenu();
        // Manejar clic izquierdo en un elemento de la lista
        playlistListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                Playlist selectedPlaylist = playlistListView.getSelectionModel().getSelectedItem();
                if (selectedPlaylist != null && selectionListener != null) {
                    selectionListener.onPlaylistSelected(selectedPlaylist);
                }
            }
        });
    }

    private void loadPlaylists() {
        playlistListView.getItems().setAll(playlistService.getAllPlaylists());
    }

    public void handleAddPlaylist() {

        Playlist newPlaylist = playlistService.addPlaylist();
        playlistListView.getItems().add(newPlaylist);

    }

    public void setSelectionListener(UIChangeListener listener) {
        this.selectionListener = listener;
    }

    private void setupPlaylistContextMenu() {
        // Crear el menú contextual
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("Editar");
        editItem.setOnAction(event -> {
            Playlist selectedPlaylist = playlistListView.getSelectionModel().getSelectedItem();
            if (selectedPlaylist != null) {
                // Lógica para editar la playlist seleccionada
                System.out.println("Editar: " + selectedPlaylist.getName());
            }
        });

        MenuItem deleteItem = new MenuItem("Borrar");
        deleteItem.setOnAction(event -> {
            Playlist selectedPlaylist = playlistListView.getSelectionModel().getSelectedItem();
            if (selectedPlaylist != null) {
                // Lógica para borrar la playlist seleccionada
                System.out.println("Borrar: " + selectedPlaylist.getName());
                playlistService.removePlaylist(selectedPlaylist);
                playlistListView.getItems().remove(selectedPlaylist);
            }
        });

        contextMenu.getItems().addAll(editItem, deleteItem);

        // Asignar el menú contextual a los elementos de la lista
        playlistListView.setCellFactory(lv -> new ListCell<Playlist>() {
            @Override
            protected void updateItem(Playlist item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setContextMenu(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/playlistitemview.fxml"));
                        Node view = loader.load();
                        PlaylistItemViewController controller = loader.getController();
                        controller.setPlaylist(item);
                        setGraphic(view);
                        setContextMenu(contextMenu);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    public void refreshPlaylists() {
        loadPlaylists();
    }
}