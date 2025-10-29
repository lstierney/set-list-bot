package uk.seaofgreen.setlistbot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    private static final String EXPECTED_PLAYLIST = """
<?xml version="1.0" encoding="UTF-8" standalone="no"?>\r
<playlist xmlns="http://xspf.org/ns/0/" version="1">\r
  <title>Binkies_22_10_25</title>\r
  <trackList>\r
    <track>\r
      <location>file:///C:/Users/ltierney/Desktop/git/setlistbot/src/test/resources/audio/Walking_Blues_G.wav</location>\r
      <title>Walking Blues (G)</title>\r
    </track>\r
    <track>\r
      <location>file:///C:/Users/ltierney/Desktop/git/setlistbot/src/test/resources/audio/Why%20I%20Sing%20the%20Blues_C_Orig.mp3</location>\r
      <title>Why I Sing The Blues (C)</title>\r
    </track>\r
    <track>\r
      <location>file:///C:/Users/ltierney/Desktop/git/setlistbot/src/test/resources/audio/Suzie%20Q_E.wav</location>\r
      <title>Suzie Q (E)</title>\r
    </track>\r
  </trackList>\r
</playlist>\r
""";

    private PlayListService playListService;

    @BeforeEach
    void setup() {
        playListService = new PlayListServiceImpl();
    }

    @Test
    public void buildPlaylist_happyPath() {
        // Given
        List<Song>songs = TestUtils.getSongs();
        Map<Song, Path> songPathMap = new LinkedHashMap<>();
        songPathMap.put(songs.get(0), Path.of("src\\test\\resources\\audio\\Walking_Blues_G.wav"));
        songPathMap.put(songs.get(1), Path.of("src\\test\\resources\\audio\\Why I Sing the Blues_C_Orig.mp3"));
        songPathMap.put(songs.get(2), Path.of("src\\test\\resources\\audio\\Suzie Q_E.wav"));

        // When
        String playlist = playListService.buildPlaylist(songPathMap, PLAYLIST_TITLE);

        // Then
        assertThat(playlist, is(EXPECTED_PLAYLIST));
    }
}
