package services;

import model.Song;
import java.util.List;

public interface ISongService {
    void addSong(Song song);
    void removeSong(Song song);
    List<Song> getAllSongs();
    Song getSongById(int id);
}
