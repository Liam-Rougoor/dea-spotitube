package liam.dea.resources;

import liam.dea.dataobjects.*;
import liam.dea.services.PlaylistService;
import liam.dea.services.PlaylistTracksService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/playlists")
public class PlaylistResource {

    private PlaylistService playlistService;
    private PlaylistTracksService playlistTracksService;

    public PlaylistResource() {
    }

    @Inject
    public PlaylistResource(PlaylistService playlistService, PlaylistTracksService playlistTracksService) {
        this.playlistService = playlistService;
        this.playlistTracksService = playlistTracksService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPlaylists(@QueryParam("token") String token) {
        //return Response.ok(playlistService.getPlaylistsOverview(token)).build();
        return Response.ok("nice").build();
    }

    @Path("{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePlaylist(@QueryParam("token") String token, @PathParam("id") int id) {
        playlistService.deletePlaylist(id, token);
        return getAllPlaylists(token);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPlaylist(@QueryParam("token") String token, Playlist playlist) {
        playlistService.createPlaylist(playlist, token);
        return Response.status(Response.Status.CREATED).entity(playlistService.getPlaylistsOverview(token)).build();
    }

    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editPlaylist(@QueryParam("token") String token, Playlist playlist) {
        playlistService.editPlaylist(playlist, token);
        return getAllPlaylists(token);
    }

    @Path("{id}/tracks")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTracksFromPlaylist(@QueryParam("token") String token, @PathParam("id") int id) {
        TracksOverview tracksOverview = playlistTracksService.getPlaylistTracksOverview(id, token);
        return Response.ok(tracksOverview).build();
    }

    @Path("{id}/tracks")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTrack(@QueryParam("token") String token, @PathParam("id") int playlistId, Track track) {
        TracksOverview tracksOverview = playlistTracksService.addTrack(playlistId, track, token);
        return Response.status(Response.Status.CREATED).entity(tracksOverview).build();
    }

    @Path("{playlistID}/tracks/{trackID}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeTrack(@QueryParam("token") String token, @PathParam("playlistID") int playlistID, @PathParam("trackID") int trackID) {
        TracksOverview tracksOverview = playlistTracksService.removeTrack(playlistID, trackID, token);
        return Response.ok(tracksOverview).build();
    }
}
