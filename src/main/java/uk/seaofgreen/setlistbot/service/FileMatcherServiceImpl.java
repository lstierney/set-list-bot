package uk.seaofgreen.setlistbot.service;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
public class FileMatcherServiceImpl implements FileMatcherService {
    @Value("${audiofiles.base.path}")
    private String baseFolderPath;

    public Map<Song, Path> matchSongsToFiles(List<Song> songs, int threshold) {
        Path baseFolder = Paths.get(baseFolderPath);
        List<Path> audioFiles;

        try {
            audioFiles = Files.walk(baseFolder)
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

        return matches;
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
