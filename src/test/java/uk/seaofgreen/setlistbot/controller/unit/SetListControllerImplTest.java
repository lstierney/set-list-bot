package uk.seaofgreen.setlistbot.controller.unit;

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
import uk.seaofgreen.setlistbot.controller.SetListControllerImpl;
import uk.seaofgreen.setlistbot.dto.AudioFileMatcherResults;
import uk.seaofgreen.setlistbot.model.Song;
import uk.seaofgreen.setlistbot.service.AudioFileMatcherService;
import uk.seaofgreen.setlistbot.service.PlayListService;
import uk.seaofgreen.setlistbot.service.SongService;
import uk.seaofgreen.setlistbot.testutils.TestStubs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
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
    void convertSetListToPlayList_successReturnsExpectedPlaylistBytes() throws IOException {
        // Given
        MockMultipartFile file = TestStubs.getSetListWithKeys();
        String playlistName = "MyPlaylist";

        Song song = TestStubs.getSong1();
        List<Song> parsedSongs = List.of(song);

        AudioFileMatcherResults audioFileMatcherResults = new AudioFileMatcherResults();
        audioFileMatcherResults.addMatch(song, Path.of("/tmp/Walking_Blues_G.wav"));

        String playlistXml = "<playlist>FAKE_XML</playlist>";

        given(songService.parseSetList(file)).willReturn(parsedSongs);
        given(audioFileMatcherService.matchSongsToAudioFiles(parsedSongs, 85)).willReturn(audioFileMatcherResults);
        given(playListService.buildPlaylist(audioFileMatcherResults.getMatches(), playlistName)).willReturn(playlistXml);

        // When
        ResponseEntity<byte[]> response = (ResponseEntity<byte[]>) controller.convertSetListToPlayList(file, playlistName, "");

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getHeaders().getContentType().toString(), is("application/xml"));
        assertThat(response.getHeaders().getFirst("Content-Disposition"), is("attachment; filename=\"" + playlistName + ".xspf\""));
        assertThat(new String(response.getBody()), is(playlistXml));
        assertThat(response.getBody().length, is(playlistXml.getBytes().length));

        then(songService).should().parseSetList(file);
        then(audioFileMatcherService).should().matchSongsToAudioFiles(parsedSongs, 85);
        then(playListService).should().buildPlaylist(audioFileMatcherResults.getMatches(), playlistName);
    }

    @Test
    void convertSetListToPlayList_returnsJsonWhenSongsNotMatched() throws IOException {
        // Given
        MockMultipartFile file = TestStubs.getSetListWithKeys();
        String playlistName = "MyPlaylist";

        Song song = TestStubs.getSong1();
        List<Song> parsedSongs = List.of(song);

        AudioFileMatcherResults audioFileMatcherResults = new AudioFileMatcherResults();
        audioFileMatcherResults.addNotMatched(song);

        given(songService.parseSetList(file)).willReturn(parsedSongs);
        given(audioFileMatcherService.matchSongsToAudioFiles(parsedSongs, 85)).willReturn(audioFileMatcherResults);

        // When
        ResponseEntity<Map<String, List<Song>>> response = (ResponseEntity<Map<String, List<Song>>>) controller.convertSetListToPlayList(file, playlistName, "");

        // Then
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getHeaders().getContentType().toString(), is("application/json"));
        assertThat(response.getBody().get("unmatchedSongs").get(0), is(song));

        then(songService).should().parseSetList(file);
        then(audioFileMatcherService).should().matchSongsToAudioFiles(parsedSongs, 85);
        org.mockito.Mockito.verifyNoInteractions(playListService);
    }

    @Test
    void getExtraSongs_happyPath() {
        // Given
        given(songService.getExtraSongs()).willReturn(TestStubs.getSongs());

        // When
        List<Song>extraSongs = songService.getExtraSongs();

        // Then
        assertThat(extraSongs.size(), is(3));
        assertThat(extraSongs.get(0).getTitle(), is("Walking Blues"));
        assertThat(extraSongs.get(1).getTitle(), is("Why I Sing The Blues"));
        assertThat(extraSongs.get(2).getTitle(), is("Suzie Q"));
    }

    @Test
    void getExtraSongs_noSongsFound() {
        // Given
        given(songService.getExtraSongs()).willReturn(new ArrayList<>());

        // When
        List<Song>extraSongs = songService.getExtraSongs();

        // Then
        assertThat(extraSongs.size(), is(0));
    }
}

