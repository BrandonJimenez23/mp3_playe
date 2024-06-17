package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import model.Song;

public class SongDAOImpl implements GenericDAO<Song> {

    private DataSource dataSource;
    private static final String TABLE_NAME = "songs";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_ARTIST = "artist";
    private static final String COLUMN_ALBUM = "album";
    private static final String COLUMN_DURATION = "length";
    private static final String COLUMN_GENRE = "genre";
    private static final String COLUMN_COVER_IMAGE = "image";
    private static final String COLUMN_PATH = "path";

    public SongDAOImpl() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    //Falta añadir atributos como duration, genre y coverImage
    @Override
    public Song save(Song song) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("INSERT INTO " + TABLE_NAME + " (" + COLUMN_TITLE + ", " + COLUMN_ARTIST + ", " + COLUMN_ALBUM + ", " + COLUMN_DURATION + ", " + COLUMN_COVER_IMAGE + ", " + COLUMN_GENRE + ", " + COLUMN_PATH + ") VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, song.getTitle());
            statement.setString(2, song.getArtist());
            statement.setString(3, song.getAlbum());
            statement.setDouble(4, song.getDuration());
            statement.setBytes(5, song.getCoverImage());
            statement.setString(6, song.getGenre());
            statement.setString(7, song.getPath());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return song;
    }
    @Override
    public void update(Song object) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("UPDATE " + TABLE_NAME + " SET " + COLUMN_TITLE + " = ?, " + COLUMN_ARTIST + " = ? WHERE " + COLUMN_ID + " = ?")) {
            statement.setString(1, object.getTitle());
            statement.setString(2, object.getArtist());
            statement.setInt(3, object.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Song object) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?")) {
            statement.setInt(1, object.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Song find(int id) {
        Song song = null;
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String title = resultSet.getString(COLUMN_TITLE);
                String artist = resultSet.getString(COLUMN_ARTIST);
                String album = resultSet.getString(COLUMN_ALBUM);
                double duration = resultSet.getDouble(COLUMN_DURATION);
                String genre = resultSet.getString(COLUMN_GENRE);
                byte[] coverImage = resultSet.getBytes(COLUMN_COVER_IMAGE);
                String path = resultSet.getString(COLUMN_PATH);
                song = new Song(id, title, artist, album, duration, genre, coverImage, path);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return song;
    }

    @Override
    public List<Song> getTable() {
        List<Song> songs = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(COLUMN_ID);
                String title = resultSet.getString(COLUMN_TITLE);
                String artist = resultSet.getString(COLUMN_ARTIST);
                String album = resultSet.getString(COLUMN_ALBUM);
                double duration = resultSet.getDouble(COLUMN_DURATION);
                String genre = resultSet.getString(COLUMN_GENRE);
                byte[] coverImage = resultSet.getBytes(COLUMN_COVER_IMAGE);
                String path = resultSet.getString(COLUMN_PATH);
                Song song = new Song(id, title, artist, album, duration, genre, coverImage, path);
                songs.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }
    public Song findSongByPath(String path) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PATH + " = ?")) {
            statement.setString(1, path);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt(COLUMN_ID);
                String title = resultSet.getString(COLUMN_TITLE);
                String artist = resultSet.getString(COLUMN_ARTIST);
                String album = resultSet.getString(COLUMN_ALBUM);
                double duration = resultSet.getDouble(COLUMN_DURATION);
                String genre = resultSet.getString(COLUMN_GENRE);
                byte[] coverImage = resultSet.getBytes(COLUMN_COVER_IMAGE);
                Song song = new Song(id, title, artist, album, duration, genre, coverImage, path);
                return song;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean isSongInDatabase(String path){
        boolean exists = false;
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PATH + " = ?")) {
            statement.setString(1, path);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }
}
