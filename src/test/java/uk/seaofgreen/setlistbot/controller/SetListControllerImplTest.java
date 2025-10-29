package uk.seaofgreen.setlistbot.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import uk.seaofgreen.setlistbot.model.Song;
import uk.seaofgreen.setlistbot.service.AudioFileMatcherService;
import uk.seaofgreen.setlistbot.service.PlayListService;
import uk.seaofgreen.setlistbot.service.SongService;
import uk.seaofgreen.setlistbot.utils.TestUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@ExtendWith(MockitoExtension.class)
public class SetListControllerImplTest {

    @Mock
    private SongService songService;

    @Mock
    private AudioFileMatcherService audioFileMatcherService;

    @Mock
    private PlayListService playListService;

    @InjectMocks
    private SetListControllerImpl controller;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void after() {
        verifyNoMoreInteractions(songService, audioFileMatcherService, playListService);
    }

    @Test
    void convertSetListToPlayList_returnsExpectedPlaylistBytes() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "fake content".getBytes()
        );
        String playlistName = "MyPlaylist";

        Song song = TestUtils.getSong1();
        List<Song> parsedSongs = List.of(song);

        Map<Song, Path> songPathMap = Map.of(song, Path.of("/tmp/Walking_Blues_G.wav"));
        String playlistXml = "<playlist>FAKE_XML</playlist>";

        given(songService.parseSetList(file)).willReturn(parsedSongs);
        given(audioFileMatcherService.matchSongsToAudioFiles(parsedSongs, 85)).willReturn(songPathMap);
        given(playListService.buildPlaylist(songPathMap, playlistName)).willReturn(playlistXml);

        // When
        ResponseEntity<byte[]> response = controller.convertSetListToPlayList(file, playlistName);

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getHeaders().getContentType().toString(), is("application/xml"));
        assertThat(response.getHeaders().getFirst("Content-Disposition"), is("attachment; filename=\"" + playlistName + ".xspf\""));
        assertThat(new String(response.getBody()), is(playlistXml));
        assertThat(response.getBody().length, is(playlistXml.getBytes().length));

        then(songService).should().parseSetList(file);
        then(audioFileMatcherService).should().matchSongsToAudioFiles(parsedSongs, 85);
        then(playListService).should().buildPlaylist(songPathMap, playlistName);
    }
}

