package services;

import java.util.List;

import dao.PlaylistDAOImpl;
import model.Playlist;
import model.Song;
import utils.FileUtils;
import java.io.IOException;

public class PlaylistService implements IPlaylistService{

    private PlaylistDAOImpl playlistDao;
    private static PlaylistService instance;

    public PlaylistService() {
        playlistDao = new PlaylistDAOImpl();
    }
    public static synchronized PlaylistService getInstance() {
        if (instance == null) {
            instance = new PlaylistService();
        }
        return instance;
    }
    @Override
    public Playlist addPlaylist() {

        String baseName = "Nueva Playlist ";
        int number = 1;
        List<Playlist> existingPlaylists = getAllPlaylists();
        boolean nameExists;

        do {
            nameExists = false;
            String potentialName = baseName + number;
            for (Playlist playlist : existingPlaylists) {
                if (playlist.getName().equals(potentialName)) {
                    nameExists = true;
                    number++;
                    break;
                }
            }
        } while (nameExists);

        Playlist newPlaylist = null;
        try {
            newPlaylist = new Playlist(baseName + number, FileUtils.getDefaultImageBytes());
        } catch (IOException e) {
            // Handle the exception
            e.printStackTrace();
        }
        newPlaylist = playlistDao.save(newPlaylist);

        return newPlaylist;

    }
    @Override
    public void removePlaylist(Playlist playlist){
        // Implementación del método para eliminar una playlist
        playlistDao.delete(playlist);
    }
    @Override
    public List<Playlist> getAllPlaylists() {
        // Implementación del método para obtener todas las playlists
        List<Playlist> playlists = playlistDao.getTable();
        return playlists; // Cambiar por la implementación real
    }
    @Override
    public void addSongToPlaylist(Playlist playlist, Song song) {
        // Implementación del método para agregar una canción a una playlist
        playlistDao.insertSongToPlaylist(playlist, song);
    }
    @Override
    public void removeSongFromPlaylist(Playlist playlist, Song song) {
        // Implementación del método para eliminar una canción de una playlist+
        playlistDao.deleteSongFromPlaylist(playlist, song);
    }
    @Override
    public void updatePlaylist(Playlist playlist) {
        // Implementación del método para actualizar una playlist
        playlistDao.update(playlist);
    }   
    @Override
    public Playlist loadSongPlaylist(Playlist playlist) {
        // Implementación del método para cargar las canciones de una playlist
        return playlistDao.loadPlaylistSongs(playlist);
    }
    public boolean isSongInPlaylist(String songPath, Playlist playlist) {
        Playlist loadedPlaylist = playlistDao.loadPlaylistSongs(playlist);// Carga las canciones de la playlist
        for (Song song : loadedPlaylist.getSongs()) {
            if(song.getPath() == null){
                continue;
            }
            if (song.getPath().equals(songPath)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public Playlist getPlaylistById(int id) {
        // Implementación del método para obtener una playlist por su id
        return playlistDao.find(id);
    }
}
