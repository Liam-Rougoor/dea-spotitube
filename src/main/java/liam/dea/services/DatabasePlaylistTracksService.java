package liam.dea.services;

import liam.dea.dataobjects.Track;
import liam.dea.dataobjects.TracksOverview;
import liam.dea.persistence.DefaultTrackDAO;
import liam.dea.persistence.TrackDAO;

import javax.enterprise.inject.Default;

@Default
public class DatabasePlaylistTracksService implements PlaylistTracksService {

    private TrackDAO trackDAO = new DefaultTrackDAO();

    @Override
    public TracksOverview getPlaylistTracksOverview(int playlistID, String token) {
        return new TracksOverview(trackDAO.getPlaylistTracks(playlistID, token));
    }

    @Override
    public TracksOverview getAvailableTracksOverview(int playlistID, String token) {
        return new TracksOverview(trackDAO.getAvailableTracks(playlistID, token));
    }

    @Override
    public TracksOverview addTrack(int playlistId, Track track, String token) {
        return trackDAO.addTrack(playlistId, track, token);
    }

    @Override
    public TracksOverview removeTrack(int playlistID, int trackID, String token) {
        return trackDAO.removeTrack(playlistID, trackID, token);
    }
}
