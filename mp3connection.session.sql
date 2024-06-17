-- Eliminar la base de datos si existe
DROP DATABASE IF EXISTS mp3_player;

-- Crear la base de datos
CREATE DATABASE mp3_player;
USE mp3_player;

-- Crear la tabla de playlists
CREATE TABLE playlists (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    createDate DATE NOT NULL,
    numberOfSongs INT DEFAULT 0,
    image LONGBLOB NOT NULL
);

-- Crear la tabla de canciones
CREATE TABLE songs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(255) NOT NULL,
    album VARCHAR(255),
    length INT NOT NULL, -- Duración en segundos
    releaseDate DATE,
    image LONGBLOB NOT NULL
);

-- Crear la tabla que relaciona playlists y canciones
CREATE TABLE playlist_songs (
    playlist_id INT,
    song_id INT,
    PRIMARY KEY (playlist_id, song_id),
    FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE
);

-- Insertar una canción en la tabla songs
INSERT INTO songs (title, artist, album, length, releaseDate, image)
VALUES ('Canción de ejemplo', 'Artista de ejemplo', 'Álbum de ejemplo', 180, '2022-01-01', 'null');

-- Insertar la referencia de la canción en la tabla playlist_songs
INSERT INTO playlist_songs (playlist_id, song_id)
VALUES (1, 1);

-- Añadir columna genre a la tabla songs
ALTER TABLE playlists
ADD COLUMN description VARCHAR(255);
-- Modificar la columna length de la tabla songs para que sea de tipo double
ALTER TABLE songs
MODIFY COLUMN length DOUBLE NOT NULL;
-- Eliminar la columna releaseDate de la tabla songs
ALTER TABLE songs
DROP COLUMN releaseDate;

