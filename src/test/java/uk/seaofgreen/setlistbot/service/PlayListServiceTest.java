package uk.seaofgreen.setlistbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.seaofgreen.setlistbot.model.Song;
import uk.seaofgreen.setlistbot.utils.TestUtils;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PlayListServiceTest {
    private static final String PLAYLIST_TITLE = "Binkies_22_10_25";
    private static final String PLAYLIST_AUDIO_BASE_PATH = "src/test/resources/audio/";

    private PlayListService playListService;

    @BeforeEach
    void setup() {
        playListService = new PlayListServiceImpl();
        ReflectionTestUtils.setField(playListService, "playListAudioBasePath", PLAYLIST_AUDIO_BASE_PATH);
    }

    @Test
    public void buildPlaylist_happyPath() throws Exception {
        // Given
        List<Song> songs = TestUtils.getSongs();
        Map<Song, Path> songPathMap = new LinkedHashMap<>();
        songPathMap.put(songs.get(0), Path.of("src/test/resources/audio/Walking_Blues_G.wav"));
        songPathMap.put(songs.get(1), Path.of("src/test/resources/audio/Why I Sing The Blues_C_Orig.mp3"));
        songPathMap.put(songs.get(2), Path.of("src/test/resources/audio/Suzie Q_E.wav"));

        // When
        String playlist = playListService.buildPlaylist(songPathMap, PLAYLIST_TITLE);

        // Build expected XML dynamically from the same Paths
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
        sb.append("<playlist xmlns=\"http://xspf.org/ns/0/\" version=\"1\">\n");
        sb.append("  <title>").append(PLAYLIST_TITLE).append("</title>\n");
        sb.append("  <trackList>\n");
        for (Map.Entry<Song, Path> entry : songPathMap.entrySet()) {
            sb.append("    <track>\n");
            sb.append("      <location>").append(PlayListServiceImpl.toFileUri(PLAYLIST_AUDIO_BASE_PATH + entry.getValue().getFileName().toString())).append("</location>\n");
            sb.append("      <title>").append(entry.getKey().title()).append(" (").append(entry.getKey().key()).append(")</title>\n");
            sb.append("    </track>\n");
        }
        sb.append("  </trackList>\n");
        sb.append("</playlist>\n");

        String expectedPlaylist = sb.toString();

        // Normalize line endings for cross-platform comparison
        assertThat(playlist.replace("\r\n", "\n"), is(expectedPlaylist.replace("\r\n", "\n")));
    }
}
