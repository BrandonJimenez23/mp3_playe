package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import model.Playlist;
import model.Song;

public class PlaylistDAOImpl implements GenericDAO<Playlist> {

    private DataSource dataSource;

    // Constant variables for column names
    private static final String TABLE_PLAYLISTS = "playlists";
    private static final String TABLE_PLAYLIST_SONGS = "playlist_songs";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_CREATE_DATE = "createDate";
    private static final String COLUMN_NUMBER_OF_SONGS = "numberOfSongs";
    private static final String COLUMN_PLAYLIST_ID = "playlist_id";
    private static final String COLUMN_SONG_ID = "song_id";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_DESCRIPTION = "description";

    public PlaylistDAOImpl() {
        this.dataSource = DatabaseConfig.getDataSource();
    }

    @Override
    public List<Playlist> getTable() {
        List<Playlist> playlists = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM " + TABLE_PLAYLISTS;
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(COLUMN_ID);
                String name = rs.getString(COLUMN_NAME);
                Date createDate = rs.getDate(COLUMN_CREATE_DATE);
                int numberOfSongs = rs.getInt(COLUMN_NUMBER_OF_SONGS);
                byte[] imageBytes = rs.getBytes(COLUMN_IMAGE);
                String description = rs.getString(COLUMN_DESCRIPTION);
                Playlist playlist = new Playlist(id, name, createDate, numberOfSongs, imageBytes, description);
                playlists.add(playlist);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    @Override
    public Playlist save(Playlist playlist) {
        // Suponiendo que usamos JDBC para la conexión a la base de datos
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO " + TABLE_PLAYLISTS + " (" + COLUMN_NAME + ", " + COLUMN_CREATE_DATE + ", "
                                + COLUMN_NUMBER_OF_SONGS + ", " + COLUMN_IMAGE + ") VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, playlist.getName());
            preparedStatement.setDate(2, new java.sql.Date(playlist.getCreateDate().getTime()));
            preparedStatement.setInt(3, playlist.getNumberOfSongs());
            preparedStatement.setBytes(4, playlist.getImageBytes());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La creación de la playlist falló, no se afectaron filas.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Suponiendo que la ID es de tipo int
                    playlist.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("La creación de la playlist falló, no se obtuvo la ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlist;
    }

    @Override
    public void update(Playlist playlist) {
        try (Connection conn = dataSource.getConnection()) {
            String query = "UPDATE " + TABLE_PLAYLISTS
                    + " SET name = ?, createDate = ?, numberOfSongs = ?, description = ?, image = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, playlist.getName());
            stmt.setDate(2, new java.sql.Date(playlist.getCreateDate().getTime()));
            stmt.setInt(3, playlist.getNumberOfSongs());
            stmt.setString(4, playlist.getDescription());
            stmt.setBytes(5, playlist.getImageBytes());
            stmt.setInt(6, playlist.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Playlist playlist) {
        try (Connection conn = dataSource.getConnection()) {
            String query = "DELETE FROM " + TABLE_PLAYLISTS + " WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, playlist.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertSongToPlaylist(Playlist playlist, Song song) {
        try (Connection conn = dataSource.getConnection()) {
            String query = "INSERT INTO " + TABLE_PLAYLIST_SONGS + " (" + COLUMN_PLAYLIST_ID + ", " + COLUMN_SONG_ID
                    + ") VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, playlist.getId());
            stmt.setInt(2, song.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Playlist find(int id) {
        Playlist playlist = null; // Cambiado a null para manejar el caso de no encontrar la playlist
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM " + TABLE_PLAYLISTS + " WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id); // Usar el ID proporcionado directamente
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString(COLUMN_NAME);
                Date createDate = rs.getDate(COLUMN_CREATE_DATE);
                int numberOfSongs = rs.getInt(COLUMN_NUMBER_OF_SONGS);
                byte[] imageBytes = rs.getBytes(COLUMN_IMAGE);
                String description = rs.getString(COLUMN_DESCRIPTION);
                playlist = new Playlist(id, name, createDate, numberOfSongs, imageBytes, description);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlist; // Devuelve null si la playlist no se encuentra
    }

    public void deleteSongFromPlaylist(Playlist playlist, Song song) {
        try (Connection conn = dataSource.getConnection()) {
            String query = "DELETE FROM " + TABLE_PLAYLIST_SONGS + " WHERE " + COLUMN_PLAYLIST_ID + " = ? AND "
                    + COLUMN_SONG_ID + " = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, playlist.getId());
            stmt.setInt(2, song.getId());
            stmt.executeUpdate();

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Playlist loadPlaylistSongs(Playlist playlist) {
        if (playlist == null) {
            return null;
        }
        List<Song> songs = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT s.* FROM " + TABLE_PLAYLIST_SONGS + " ps INNER JOIN songs s ON ps." + COLUMN_SONG_ID
                    + " = s.id WHERE ps." + COLUMN_PLAYLIST_ID + " = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, playlist.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String artist = rs.getString("artist");
                String album = rs.getString("album");
                int duration = rs.getInt("length");
                byte[] coverImage = rs.getBytes("image");
                String genre = rs.getString("genre");
                String path = rs.getString("path");
                Song song = new Song(id, title, artist, album, duration, genre, coverImage, path);
                songs.add(song);
            }
            playlist.setSongs(songs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlist;
    }
}