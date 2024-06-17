package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Playlist;

import java.io.ByteArrayInputStream; // Add this import statement

public class PlaylistItemViewController {
    @FXML
    private ImageView imageView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label songCountLabel;

    public void setPlaylist(Playlist playlist) {
        nameLabel.setText(playlist.getName());
        songCountLabel.setText("Canciones: " + playlist.getNumberOfSongs());
        if (playlist.getImageBytes() != null) {
            Image image = new Image(new ByteArrayInputStream(playlist.getImageBytes()));
            imageView.setImage(image);
        } else {
            imageView.setImage(null);
        }
    }
}
