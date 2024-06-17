package services;

import dao.SongDAOImpl;
import javafx.scene.media.Media;
import model.Song;
import utils.FileUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SongService implements ISongService {

    private SongDAOImpl songDao;

    private static SongService instance;

    public SongService() {
        songDao = new SongDAOImpl();
    }
    public static synchronized SongService getInstance() {
        if (instance == null) {
            instance = new SongService();
        }
        return instance;
    }
    @Override
    public void addSong(Song song) {
        songDao.save(song);
    }

    @Override
    public void removeSong(Song song) {
        songDao.delete(song);
    }

    @Override
    public List<Song> getAllSongs() {
        return songDao.getTable();
    }

    @Override
    public Song getSongById(int id) {
        return songDao.find(id);
    }
    public Song getSongByPath(String path) {
        Song song = null;
        try {
            song =  songDao.findSongByPath(path);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return song;
    }
    public boolean isSongInDatabase(String path) {
        boolean isSongInDatabase = false;
        isSongInDatabase = songDao.isSongInDatabase(path);
        return isSongInDatabase;
    }   
    public Song extractMetadata(String path) {
        Media media = new Media(path);
        double duration = media.getDuration().toSeconds();
        if (Double.isNaN(duration)) {
            duration = 0.0; // Asignar un valor predeterminado si es NaN
        }
        String title = (String) media.getMetadata().getOrDefault("title", path.substring(path.lastIndexOf("/") + 1));
        String artist = (String) media.getMetadata().getOrDefault("artist", "Unknown Artist");
        String album = (String) media.getMetadata().getOrDefault("album", "Unknown Album");
        String genre = (String) media.getMetadata().getOrDefault("genre", "Unknown Genre");
        byte[] image = null;
        try {
            image = (byte[]) media.getMetadata().getOrDefault("image", FileUtils.getDefaultImageBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } // Esto puede requerir conversión dependiendo del tipo de dato
        return new Song(0, title, artist, album, duration, genre, image, path);
    }
}