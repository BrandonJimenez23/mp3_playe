package model;

public class Song {

    private int id;
    private String title;
    private String artist;
    private String album;
    private double duration;
    private String genre;
    private byte[] coverImage;
    private String path;
    
    public Song() {
    }
    public Song(int id, String title, String artist, String album, double duration, String genre, byte[] coverImage, String path) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.genre = genre;
        this.coverImage = coverImage;
        this.path = path;
    }
    // Getters and setters for the properties
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getArtist() {
        return artist;
    }
    
    public void setArtist(String artist) {
        this.artist = artist;
    }
    
    public String getAlbum() {
        return album;
    }
    
    public void setAlbum(String album) {
        this.album = album;
    }
    
    public double getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public byte[] getCoverImage() {
        return coverImage;
    }
    public void setCoverImage(byte[] coverImage) {
        this.coverImage = coverImage;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
}
