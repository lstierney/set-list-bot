package uk.seaofgreen.setlistbot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class AudioFileNameParser {
    private static final Logger logger = LoggerFactory.getLogger(AudioFileNameParser.class);
    public static String getKeyFromPath(Path path) {
        String audioFileName = path.getFileName().toString();
        logger.info("audioFileName: '{}'", audioFileName);

        // Strip extension
        String baseName = audioFileName.replaceFirst("\\.[^.]+$", "");

        // 1. Check for parenthetical key: (C), (G#), etc.
        var parenMatch = java.util.regex.Pattern.compile("\\(([A-Ga-g][#b]?)\\)")
                .matcher(baseName);
        if (parenMatch.find()) {
            return parenMatch.group(1).toUpperCase();
        }

        // 2. Check for underscore-separated key at end: _C, _G#, _D_Orig, etc.
        var underscoreMatch = java.util.regex.Pattern.compile("_([A-Ga-g][#b]?)(_|$)")
                .matcher(baseName);
        if (underscoreMatch.find()) {
            return underscoreMatch.group(1).toUpperCase();
        }

        // 3. Check for trailing single letter after space: "Who's Been Talking A"
        var trailingLetterMatch = java.util.regex.Pattern.compile("\\s([A-Ga-g])$")
                .matcher(baseName);
        if (trailingLetterMatch.find()) {
            return trailingLetterMatch.group(1).toUpperCase();
        }

        // 4. No valid key found
        return null;
    }
}
