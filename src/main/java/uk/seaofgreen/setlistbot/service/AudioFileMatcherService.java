package uk.seaofgreen.setlistbot.service;

import uk.seaofgreen.setlistbot.dto.AudioFileMatcherResults;
import uk.seaofgreen.setlistbot.model.Song;

import java.util.List;

public interface AudioFileMatcherService {
    AudioFileMatcherResults matchSongsToAudioFiles(List<Song> songs, int threshold);
}
