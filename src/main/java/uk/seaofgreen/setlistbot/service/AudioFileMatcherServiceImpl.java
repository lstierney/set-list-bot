package uk.seaofgreen.setlistbot.service;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.seaofgreen.setlistbot.dto.AudioFileMatcherResults;
import uk.seaofgreen.setlistbot.exception.SetListBotException;
import uk.seaofgreen.setlistbot.model.Song;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AudioFileMatcherServiceImpl implements AudioFileMatcherService {
    private static final Logger logger = LoggerFactory.getLogger(AudioFileMatcherServiceImpl.class);

    @Value("${AUDIO_FILE_SEARCH_PATH}")
    private String audioFileSearchPath;

    public AudioFileMatcherResults matchSongsToAudioFiles(List<Song> songs, int threshold) {
        logger.info("Calling with audioFileSearchPath: '{}'", audioFileSearchPath);

        Path baseFolder = Paths.get(audioFileSearchPath);
        List<Path> audioFiles;

        try {
            // list = top level only, walk = traverse sub-dirs too
            audioFiles = Files.list(baseFolder)
                    .filter(Files::isRegularFile)
                    .toList();
        } catch (IOException e) {
            throw new SetListBotException(e);
        }

        Map<Song, Path> matches = new LinkedHashMap<>();

        for (Song song : songs) {
            String songTitle = normalize(song.title());
            Path bestMatch = null;
            int bestScore = 0;

            for (Path file : audioFiles) {
                String fileName = file.getFileName().toString()
                        .replaceFirst("\\.[^.]+$", "") // strip extension
                        .toLowerCase();

                String normalizedFileName = normalize(fileName);
                int score = FuzzySearch.tokenSetRatio(songTitle, normalizedFileName);

                if (score > bestScore) {
                    bestScore = score;
                    bestMatch = file;
                }
            }

            matches.put(song, bestScore >= threshold ? bestMatch : null);
        }

        return AudioFileMatcherResults.fromSongPathMap(matches);
    }

    private String normalize(String input) {
        return input.toLowerCase()
                .replaceAll("[’']", "'")                    // normalize apostrophes
                .replaceAll("\\(.*?\\)", "")                // remove parentheses
                .replaceAll("[^a-z0-9\\s]", " ")            // strip punctuation/symbols
                .replaceAll("\\s+", " ")                    // collapse spaces
                .trim();
    }
}
