package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SidebarNavigationViewController {
    // Manejar eventos de navegación aquí

    private UIChangeListener uiChangeListener;
    @FXML
    private Button navSearchButton;

    @FXML
    public void initialize() {
        // Cargar las playlists y mostrarlas en playlistListView
        navSearchButton.setOnAction(event -> handleSearchNavigation());
    }

    @FXML
    public void handleSearchNavigation() {
        // Implementar la navegación a la vista de la biblioteca
        if (uiChangeListener != null) {
            uiChangeListener.onNavigationButtonClicked("mp3searchview");
        }
    }

    public void setUIChangeListener(UIChangeListener listener) {
        this.uiChangeListener = listener;
    }
}