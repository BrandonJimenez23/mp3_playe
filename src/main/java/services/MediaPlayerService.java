package services;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import model.Playlist;
import model.Song;
import utils.FileUtils;


import controllers.UIChangeListener;

public class MediaPlayerService {
    private static MediaPlayerService instance;
    private MediaPlayer mediaPlayer;
    private Song currentMediaMetadata;
    private Playlist currentPlaylist;
    private int currentTrackIndex = 0;
    private UIChangeListener mediaChangeListener;

    private MediaPlayerService() {
    }

    public void setMetadataListener(UIChangeListener listener) {
        this.mediaChangeListener = listener;
    }

    public static MediaPlayerService getInstance() {
        if (instance == null) {
            synchronized (MediaPlayerService.class) {
                if (instance == null) {
                    instance = new MediaPlayerService();
                }
            }
        }
        return instance;
    }

    public void load(Media media, Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> mediaPlayer.play());
        currentMediaMetadata = song;
        if (mediaChangeListener != null) {
            mediaChangeListener.onSongMetadataChanged(currentMediaMetadata);
        }
    }

    public void loadPlaylist(Playlist playlist) {
        currentTrackIndex = 0;
        this.currentPlaylist = playlist;
        loadMedia(currentTrackIndex);
    }

    private void loadMedia(int index) {
        if (currentPlaylist != null) {
            String path = currentPlaylist.getSongs().get(index).getPath();
            Song song = currentPlaylist.getSongs().get(index);
            if(mediaPlayer != null) {
                mediaPlayer.stop();
            }
            Media media = new Media(FileUtils.normalizeURL(path));
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnReady(() -> mediaPlayer.play());
            currentMediaMetadata = song;
            if (mediaChangeListener != null) {
                mediaChangeListener.onSongMetadataChanged(currentMediaMetadata);
            }
        }
    }

    public void playNext() {
        if (currentTrackIndex + 1 < currentPlaylist.getSongs().size()) {
            currentTrackIndex++;
            loadMedia(currentTrackIndex);
        }
    }

    public void playPrevious() {
        if (currentTrackIndex - 1 >= 0) {
            currentTrackIndex--;
            loadMedia(currentTrackIndex);
        }
    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public Song getCurrentMediaMetadata() {
        return currentMediaMetadata;
    }
}
