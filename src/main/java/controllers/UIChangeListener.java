package controllers;

import model.Playlist;
import model.Song;

public interface UIChangeListener {
    void onPlaylistSelected(Playlist playlist);
    void onNavigationButtonClicked(String viewName);
    void onSongMetadataChanged(Song newSongMetadata);
    void onPlaylistChanged();
}
