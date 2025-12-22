package uk.seaofgreen.setlistbot.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import uk.seaofgreen.setlistbot.model.Song;
import uk.seaofgreen.setlistbot.testutils.TestStubs;

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
        setListWithKeys = TestStubs.getSetListWithKeys();
        setListNumberedNoKeys = TestStubs.getSetListNumberedNoKeys();
    }

    @Test
    void parseSetListWithKeysReturnsCorrectNumberOfSongs() {
        // When
        List<Song> songs = songService.parseSetList(setListWithKeys);

        // Then
        assertThat(songs.size(), is(28));
    }

    @Test
    void parseSetListWithKeysReturnsCorrectSongs_firstSong() throws IOException {
        // When
        List<Song> songs = songService.parseSetList(setListWithKeys);
        Song song = songs.get(0);

        // Then
        assertThat(song.getTitle(), is("why i sing the blues"));
        assertThat(song.getKey(), is("C"));
    }

    @Test
    void parseSetListWithKeysReturnsCorrectSongs_lastSong() throws IOException {
        // When
        List<Song> songs = songService.parseSetList(setListWithKeys);
        Song song = songs.get(songs.size() - 1);

        // Then
        assertThat(song.getTitle(), is("let the good times roll"));
        assertThat(song.getKey(), is("G"));
    }

    @Test
    void parseSetListWithKeysReturnsCorrectSongs_middleSong() throws IOException {
        // When
        List<Song> songs = songService.parseSetList(setListWithKeys);
        Song song = songs.get(11);

        // Then
        assertThat(song.getTitle(), is("talk to me baby"));
        assertThat(song.getKey(), is("C"));
    }

    @Test
    void parseSetListNumberedNoKeysReturnsCorrectNumberOfSongs() {
        // When
        List<Song> songs = songService.parseSetList(setListNumberedNoKeys);

        // Then
        assertThat(songs.size(), is(10));
    }

    @Test
    void parseSetListNumberedNoKeysReturnsCorrectSongs_firstSong() {
        // When
        List<Song> songs = songService.parseSetList(setListNumberedNoKeys);
        Song song = songs.get(0);

        // Then
        assertThat(song.getTitle(), is("why i sing the blues"));
        assertThat(song.getKey(), is(nullValue()));
    }

    @Test
    void parseSetListNumberedNoKeysReturnsCorrectSongs_lastSong() {
        // When
        List<Song> songs = songService.parseSetList(setListNumberedNoKeys);
        Song song = songs.get(9);

        // Then
        assertThat(song.getTitle(), is("talk to your daughter"));
        assertThat(song.getKey(), is(nullValue()));
    }

    @Test
    void parseSetListNumberedNoKeysReturnsCorrectSongs_middleSong() {
        // When
        List<Song> songs = songService.parseSetList(setListNumberedNoKeys);
        Song song = songs.get(5);

        // Then
        assertThat(song.getTitle(), is("it hurts me too"));
        assertThat(song.getKey(), is(nullValue()));
    }

    /**
     * @Override
     *     public List<Song> getExtraSongs() {
     *         List<String>titles = List.of(defaultExtraSongs.split(","));
     *         List<Song>extraSongs = new ArrayList<>();
     *
     *         for(String title : titles) {
     *             extraSongs.add(new Song(title, null));
     *         }
     *
     *         return extraSongs;
     *     }
     */

    @Test
    void getExtraSongs_happyPath() {
        // Given
        ReflectionTestUtils.setField(songService, "defaultExtraSongs", TestStubs.DEFAULT_EXTRA_SONGS);

        // When
        List<Song> extraSongs = songService.getExtraSongs();

        // Then
        assertThat(extraSongs.size(), is(2));

        assertThat(extraSongs.get(0).getTitle(), is("Leaving Trunk"));
        assertThat(extraSongs.get(0).getKey(), is(nullValue()));

        assertThat(extraSongs.get(1).getTitle(), is("Let the Good Times Roll"));
        assertThat(extraSongs.get(1).getKey(), is(nullValue()));
    }

    @Test
    void getExtraSongs_extraSongsIsEmpty() {
        // Given
        ReflectionTestUtils.setField(songService, "defaultExtraSongs", "");

        // When
        List<Song> extraSongs = songService.getExtraSongs();

        // Then
        assertThat(extraSongs.size(), is(0));
    }

    @Test
    void getExtraSongs_extraSongsIsNull() {
        // Given
        ReflectionTestUtils.setField(songService, "defaultExtraSongs", null);

        // When
        List<Song> extraSongs = songService.getExtraSongs();

        // Then
        assertThat(extraSongs.size(), is(0));
    }
}
