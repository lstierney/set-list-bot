package uk.seaofgreen.setlistbot.dto;

import uk.seaofgreen.setlistbot.model.Song;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AudioFileMatcherResults {
    private final List<Song> notMatched = new ArrayList<>();
    private final Map<Song, Path> matches = new LinkedHashMap<>();

    public void addMatch(Song song, Path path) {
        matches.put(song, path);
    }

    public void addNotMatched(Song song) {
        notMatched.add(song);
    }

    public List<Song> getNotMatched() {
        return notMatched;
    }

    public Map<Song, Path> getMatches() {
        return matches;
    }

    public static AudioFileMatcherResults fromSongPathMap(Map<Song, Path> data) {
        AudioFileMatcherResults results = new AudioFileMatcherResults();
        for (Song song : data.keySet()) {
            Path path = data.get(song);
            if (path != null) {
                results.addMatch(song, path);
            } else {
                results.addNotMatched(song);
            }
        }
        return results;
    }
}
