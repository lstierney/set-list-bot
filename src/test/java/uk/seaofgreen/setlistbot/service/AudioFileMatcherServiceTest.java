package uk.seaofgreen.setlistbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.seaofgreen.setlistbot.model.Song;
import uk.seaofgreen.setlistbot.utils.TestUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AudioFileMatcherServiceTest {
    private static final String AUDIO_FILES_BASE_PATH = "src/test/resources/audio/";
    private AudioFileMatcherService audioFileMatcherService;

    @BeforeEach
    void setup() {
        audioFileMatcherService = new AudioFileMatcherServiceImpl();
        ReflectionTestUtils.setField(audioFileMatcherService, "audioFileSearchPath", AUDIO_FILES_BASE_PATH);
    }

    @Test
    public void matchSongsToAudioFiles_happyPath() {
        // Given
        List<Song>songs = TestUtils.getSongs();
        int threshold = 85;

        // When
        Map<Song, Path> songPaths = audioFileMatcherService.matchSongsToAudioFiles(songs, threshold);

        // Then
        assertThat(songPaths.size(), is(songs.size()));
        assertThat(songPaths.get(songs.get(0)), is(Path.of("src", "test", "resources", "audio", "Walking_Blues_G.wav")));
        assertThat(songPaths.get(songs.get(1)), is(Path.of("src", "test", "resources", "audio", "Why I Sing the Blues_C_Orig.mp3")));
        assertThat(songPaths.get(songs.get(2)), is(Path.of("src", "test", "resources", "audio", "Suzie Q_E.wav")));

    }
}
