package liam.dea.persistence;

import liam.dea.dataobjects.Track;
import liam.dea.dataobjects.TracksOverview;

import java.util.List;

public interface TrackDAO {
    Track getTrackByID(int id);

    List<Track> getPlaylistTracks(int playlistID, String token);

    List<Track> getAvailableTracks(int playlistID, String token);

    TracksOverview addTrack(int playlistId, Track track, String token);

    TracksOverview removeTrack(int playlistID, int trackID, String token);

    TracksOverview createTracksOverview(List<Track> tracks);
}
