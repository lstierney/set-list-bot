package uk.seaofgreen.setlistbot.service;

import uk.seaofgreen.setlistbot.model.Song;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface AudioFileMatcherService {
    Map<Song, Path> matchSongsToAudioFiles(List<Song> songs, int threshold);
}
