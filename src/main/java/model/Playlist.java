
package model;

import java.util.Date;
import java.util.List;

public class Playlist {
    private List<Song> songs;
    private String name;
    private byte[] imageBytes;
    private Date createDate;
    private int numberOfSongs;
    private String description;
    private int id;

    public Playlist() {
    }
    public Playlist(String name, byte[] imageBytes) {
        this.name = name;
        this.imageBytes = imageBytes;
        this.createDate = new Date();
        this.numberOfSongs = 0;
    }
    public Playlist(int id, String name, Date createDate, int numberOfSongs, byte[] imageBytes, String description) {
        this.name = name;
        this.createDate = new Date();
        this.numberOfSongs = numberOfSongs;
        this.id = id;
        this.imageBytes = imageBytes;
        this.description = description;
    }

    // Add getters and setters for name, createDate, numberOfSongs, and id
    public Playlist(List<Song> songs) {
        this.songs = songs;
    }

    // Add getters and setters for songs
    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public byte[] getImageBytes() {
        return imageBytes;
    }
    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
