package uk.seaofgreen.setlistbot.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.seaofgreen.setlistbot.model.Song;
import uk.seaofgreen.setlistbot.testutils.TestStubs;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AudioFileMatcherResultsTest {
    private AudioFileMatcherResults audioFileMatcherResults;

    @BeforeEach
    void setup() {
        audioFileMatcherResults = new AudioFileMatcherResults();
    }

    @Test
    public void testMatches() {
        Song song = TestStubs.getSong1();
        Path path = Path.of("/some/path");

        audioFileMatcherResults.addMatch(song, path);

        Map<Song, Path> matches = audioFileMatcherResults.getMatches();
        List<Song> notMatched = audioFileMatcherResults.getNotMatched();

        assertThat(matches.size(), is(1));
        assertThat(notMatched.size(), is(0));
        assertTrue(matches.containsKey(song));
        assertTrue(matches.containsValue(path));

    }

    @Test
    public void testNotMatched() {
        Song song = TestStubs.getSong1();

        audioFileMatcherResults.addNotMatched(song);

        Map<Song, Path> matches = audioFileMatcherResults.getMatches();
        List<Song> notMatched = audioFileMatcherResults.getNotMatched();

        assertThat(matches.size(), is(0));
        assertThat(notMatched.size(), is(1));
        assertTrue(notMatched.contains(song));
    }
}
