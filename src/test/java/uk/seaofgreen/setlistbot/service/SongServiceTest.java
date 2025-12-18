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
import static org.hamcrest.Matchers.nullValue;

public class SongServiceTest {
    private SongService songService;
    private MultipartFile setListWithKeys;
    private MultipartFile setListNumberedNoKeys;

    @BeforeEach
    void setup() throws Exception {
        songService = new SongServiceImpl();
        setListWithKeys = TestUtils.getSetListWithKeys();
        setListNumberedNoKeys = TestUtils.getSetListNumberedNoKeys();
    }

    @Test
    public void parseSetListWithKeysReturnsCorrectNumberOfSongs() {
        // When
        List<Song> songs = songService.parseSetList(setListWithKeys);

        // Then
        assertThat(songs.size(), is(28));
    }

    @Test
    public void parseSetListWithKeysReturnsCorrectSongs_firstSong() throws IOException {
        // When
        List<Song> songs = songService.parseSetList(setListWithKeys);
        Song song = songs.get(0);

        // Then
        assertThat(song.getTitle(), is("why i sing the blues"));
        assertThat(song.getKey(), is("C"));
    }

    @Test
    public void parseSetListWithKeysReturnsCorrectSongs_lastSong() throws IOException {
        // When
        List<Song> songs = songService.parseSetList(setListWithKeys);
        Song song = songs.get(songs.size() - 1);

        // Then
        assertThat(song.getTitle(), is("let the good times roll"));
        assertThat(song.getKey(), is("G"));
    }

    @Test
    public void parseSetListWithKeysReturnsCorrectSongs_middleSong() throws IOException {
        // When
        List<Song> songs = songService.parseSetList(setListWithKeys);
        Song song = songs.get(11);

        // Then
        assertThat(song.getTitle(), is("talk to me baby"));
        assertThat(song.getKey(), is("C"));
    }

    @Test
    public void parseSetListNumberedNoKeysReturnsCorrectNumberOfSongs() {
        // When
        List<Song> songs = songService.parseSetList(setListNumberedNoKeys);

        // Then
        assertThat(songs.size(), is(10));
    }

    @Test
    public void parseSetListNumberedNoKeysReturnsCorrectSongs_firstSong() {
        // When
        List<Song> songs = songService.parseSetList(setListNumberedNoKeys);
        Song song = songs.get(0);

        // Then
        assertThat(song.getTitle(), is("why i sing the blues"));
        assertThat(song.getKey(), is(nullValue()));
    }

    @Test
    public void parseSetListNumberedNoKeysReturnsCorrectSongs_lastSong() {
        // When
        List<Song> songs = songService.parseSetList(setListNumberedNoKeys);
        Song song = songs.get(9);

        // Then
        assertThat(song.getTitle(), is("talk to your daughter"));
        assertThat(song.getKey(), is(nullValue()));
    }

    @Test
    public void parseSetListNumberedNoKeysReturnsCorrectSongs_middleSong() {
        // When
        List<Song> songs = songService.parseSetList(setListNumberedNoKeys);
        Song song = songs.get(5);

        // Then
        assertThat(song.getTitle(), is("it hurts me too"));
        assertThat(song.getKey(), is(nullValue()));
    }
}
