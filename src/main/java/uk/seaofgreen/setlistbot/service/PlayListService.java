package uk.seaofgreen.setlistbot.service;

import uk.seaofgreen.setlistbot.model.Song;

import java.nio.file.Path;
import java.util.Map;

public interface PlayListService {
    String buildPlaylist(Map<Song, Path> songPathMap, String playlistTitle);
}
