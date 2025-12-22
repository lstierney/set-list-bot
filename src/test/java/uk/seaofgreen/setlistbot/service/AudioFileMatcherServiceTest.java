package uk.seaofgreen.setlistbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.seaofgreen.setlistbot.dto.AudioFileMatcherResults;
import uk.seaofgreen.setlistbot.model.Song;
import uk.seaofgreen.setlistbot.testutils.TestStubs;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AudioFileMatcherServiceTest {
    private static final String AUDIO_FILES_BASE_PATH = "src/test/resources/audio/";
    private AudioFileMatcherService audioFileMatcherService;

    @BeforeEach
    void setup() {
        audioFileMatcherService = new AudioFileMatcherServiceImpl();
        ReflectionTestUtils.setField(audioFileMatcherService, "audioFileSearchPath", AUDIO_FILES_BASE_PATH);
    }

    @Test
    void matchSongsToAudioFiles_happyPath() {
        // Given
        List<Song>songs = TestStubs.getSongs();
        int threshold = 85;

        // When
        AudioFileMatcherResults audioFileMatcherResults = audioFileMatcherService.matchSongsToAudioFiles(songs, threshold);
        Map<Song, Path> matches = audioFileMatcherResults.getMatches();

        // Then
        assertThat(matches.size(), is(songs.size()));
        assertThat(matches.get(songs.get(0)), is(Path.of("src", "test", "resources", "audio", "Walking_Blues_G.wav")));
        assertThat(matches.get(songs.get(1)), is(Path.of("src", "test", "resources", "audio", "Why I Sing the Blues_C_Orig.mp3")));
        assertThat(matches.get(songs.get(2)), is(Path.of("src", "test", "resources", "audio", "Suzie Q_E.wav")));
    }

    @Test
    void matchSongsToAudioFiles_shouldNotSearchSubDirs() {
        // Given
        int threshold = 85;
        List<Song>songs = TestStubs.getSongs();
        Song archiveSong = TestStubs.getArchiveSong();
        songs.add(archiveSong); // this should never match as it only exists in nested folder

        // When
        AudioFileMatcherResults audioFileMatcherResults = audioFileMatcherService.matchSongsToAudioFiles(songs, threshold);

        // Then
        assertThat(audioFileMatcherResults.getMatches().size(), is(3));
        assertThat(audioFileMatcherResults.getNotMatched().size(), is(1));
        assertTrue(audioFileMatcherResults.getNotMatched().contains(archiveSong));
        assertFalse(audioFileMatcherResults.getMatches().containsKey(archiveSong));
    }
}
