package liam.dea.persistence;

import liam.dea.dataobjects.Playlist;
import liam.dea.dataobjects.PlaylistsOverview;

import java.util.List;

public interface PlaylistDAO {
    List<Playlist> getAllPlaylists(String token);

    Playlist getPlaylistByID(int id, String token);

    Playlist deletePlaylist(int id, String token);

    Playlist addPlaylist(Playlist playlist, String token);

    Playlist editPlaylist(Playlist playlist, String token);

    PlaylistsOverview getPlaylistsOverview(String token);
}
