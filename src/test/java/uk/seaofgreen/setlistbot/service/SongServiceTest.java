package uk.seaofgreen.setlistbot.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import uk.seaofgreen.setlistbot.model.Song;
import uk.seaofgreen.setlistbot.utils.TestUtils;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SongServiceTest {
    private SongService songService;
    private MultipartFile testMultipartFile;

    @BeforeEach
    void setup() throws Exception {
        songService = new SongServiceImpl();
        testMultipartFile = TestUtils.getTestMultipartFile();
    }

    @Test
    public void parseSetListReturnsCorrectNumberOfSongs() {
        // When
        List<Song> songs = songService.parseSetList(testMultipartFile);

        // Then
        assertThat(songs.size(), is(28));
    }

    @Test
    public void parseSetListReturnsCorrectSongs_firstSong() throws IOException {
        // When
        List<Song> songs = songService.parseSetList(testMultipartFile);
        Song song = songs.get(0);

        // Then
        assertThat(song.title(), is("why i sing the blues"));
        assertThat(song.key(), is("C"));
    }

    @Test
    public void parseSetListReturnsCorrectSongs_lastSong() throws IOException {
        // When
        List<Song> songs = songService.parseSetList(testMultipartFile);
        Song song = songs.get(songs.size() - 1);

        // Then
        assertThat(song.title(), is("let the good times roll"));
        assertThat(song.key(), is("G"));
    }

    @Test
    public void parseSetListReturnsCorrectSongs_middleSong() throws IOException {
        // When
        List<Song> songs = songService.parseSetList(testMultipartFile);
        Song song = songs.get(11);

        // Then
        assertThat(song.title(), is("talk to me baby"));
        assertThat(song.key(), is("C"));
    }


}
