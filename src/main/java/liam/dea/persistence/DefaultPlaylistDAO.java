package liam.dea.persistence;

import liam.dea.exceptions.InvalidTokenException;
import liam.dea.exceptions.DatabaseItemNotFoundException;
import liam.dea.dataobjects.Playlist;
import liam.dea.dataobjects.PlaylistsOverview;
import liam.dea.services.PlaylistTracksService;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Default
public class DefaultPlaylistDAO implements PlaylistDAO {

    private TokenDAO tokenDAO = new TokenDAO();
    private TrackDAO trackDAO;

    public DefaultPlaylistDAO() {
    }

    @Inject
    public DefaultPlaylistDAO(TokenDAO tokenDAO, TrackDAO trackDAO) {
        this.tokenDAO = tokenDAO;
        this.trackDAO = trackDAO;
    }

    @Override
    public List<Playlist> getAllPlaylists(String token) {
        try (
                Connection connection = new DatabaseConnectionFactory().createConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM playlist");
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Playlist> playlists = new ArrayList<>();
            while (resultSet.next()) {
                Playlist playlist = getPlaylistByID(resultSet.getInt("id"), token);
                playlist.setOwner(tokenDAO.getUserWithToken(token).equals(playlist.getUser()));
                playlists.add(playlist);
            }
            return playlists;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Playlist getPlaylistByID(int id, String token) {
        try (
                Connection connection = new DatabaseConnectionFactory().createConnection();
                PreparedStatement playlistStatement = connection.prepareStatement("SELECT * FROM playlist WHERE id = ?");
                PreparedStatement playlistTrackStatement = connection.prepareStatement("SELECT * FROM playlist_track WHERE playlist = ?");
        ) {
            if (!tokenDAO.tokenIsValid(token)) {
                throw new InvalidTokenException();
            }

            playlistStatement.setInt(1, id);
            ResultSet playlistSet = playlistStatement.executeQuery();

            if (playlistSet.next()) {
                Playlist playlist = new Playlist();
                playlist.setId(id);
                playlist.setName(playlistSet.getString("name"));
                playlist.setUser(playlistSet.getString("owner"));
                playlist.setTracks(trackDAO.getPlaylistTracks(id,token));
                return playlist;
            }
            throw new DatabaseItemNotFoundException("Playlist " + id + " not found");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Playlist deletePlaylist(int id, String token) {
        try (
                Connection connection = new DatabaseConnectionFactory().createConnection();
                PreparedStatement deletePlaylistStatement = connection.prepareStatement("DELETE FROM playlist WHERE id = ?");
        ) {
            Playlist playlist = getPlaylistByID(id, token);
            deletePlaylistStatement.setInt(1, id);
            deletePlaylistStatement.execute();
            return playlist;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Playlist addPlaylist(Playlist playlist, String token) {
        try (
                Connection connection = new DatabaseConnectionFactory().createConnection();
                PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO playlist(name, owner) VALUES(?, ?)");
                PreparedStatement lastInsertedIDStatement = connection.prepareStatement("SELECT LAST_INSERT_ID() AS id")
        ) {
            if (!tokenDAO.tokenIsValid(token)) {
                throw new InvalidTokenException();
            }
            String user = tokenDAO.getUserWithToken(token);
            insertStatement.setString(1, playlist.getName());
            insertStatement.setString(2, user);
            insertStatement.execute();
            ResultSet playlistIDRow = lastInsertedIDStatement.executeQuery();
            playlistIDRow.next();
            Playlist addedPlaylist = new Playlist();
            addedPlaylist.setName(playlist.getName());
            addedPlaylist.setId(playlistIDRow.getInt("id"));
            addedPlaylist.setUser(user);
            return addedPlaylist;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Playlist editPlaylist(Playlist playlist, String token) {
        try (
                Connection connection = new DatabaseConnectionFactory().createConnection();
                PreparedStatement updateStatement = connection.prepareStatement("UPDATE playlist SET name = ? WHERE id = ?");
        ) {
            if (!tokenDAO.tokenIsValid(token)) {
                throw new InvalidTokenException();
            }
            updateStatement.setString(1, playlist.getName());
            updateStatement.setInt(2, playlist.getId());
            updateStatement.execute();
            Playlist editedPlaylist = new Playlist();
            editedPlaylist.setId(playlist.getId());
            editedPlaylist.setName(playlist.getName());
            editedPlaylist.setUser(playlist.getUser());
            return editedPlaylist;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PlaylistsOverview getPlaylistsOverview(String token) {
        return new PlaylistsOverview(getAllPlaylists(token));
    }

}
