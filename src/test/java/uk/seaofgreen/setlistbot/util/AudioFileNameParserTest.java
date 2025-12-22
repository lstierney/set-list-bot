package uk.seaofgreen.setlistbot.util;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AudioFileNameParserTest {
    @Test
    void getKeyFromPath_keyInParenthesis() {
        verifyKey("/audio/files/Song_Title_(G).mp3", "G");

    }

    private void verifyKey(String pathAsString, String expectedKey) {
        // Given
        Path path = Path.of(pathAsString);

        // When
        String key = AudioFileNameParser.getKeyFromPath(path);

        // Then
        assertThat(key, is(expectedKey));
    }

    @Test
    void getKeyFromPath_keyUnderScoreAtEnd() {
        verifyKey("/audio/files/Song_Title_G.mp3", "G");
    }

    @Test
    void getKeyFromPath_keySingleTrailingLetter() {
        verifyKey("/audio/files/Song Title G.mp3", "G");
    }

    @Test
    void getKeyFromPath_noKeyFoundReturnsNull() {
        verifyKey("/audio/files/Song Title No Key.mp3", null);
    }
}
