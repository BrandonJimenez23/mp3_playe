package services;

import java.util.List;

import model.Playlist;
import model.Song;

public interface IPlaylistService {
    Playlist addPlaylist();
    void removePlaylist(Playlist playlist);
    void addSongToPlaylist(Playlist playlist, Song song);
    void removeSongFromPlaylist(Playlist playlist, Song song);
    void updatePlaylist(Playlist playlist);
    List<Playlist> getAllPlaylists();
    Playlist getPlaylistById(int id);
    Playlist loadSongPlaylist(Playlist playlist);
}
