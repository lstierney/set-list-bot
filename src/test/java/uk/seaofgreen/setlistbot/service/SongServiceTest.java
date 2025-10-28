package uk.seaofgreen.setlistbot.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import uk.seaofgreen.setlistbot.model.Song;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SongServiceTest {
    private static final String FILE_NAME = "HowlinWolf.Oct.2025.docx";
    private SongService songService;
    private MultipartFile testMultipartFile;

    @BeforeEach
    void setup() throws Exception {
        songService = new SongServiceImpl();
        testMultipartFile = getTestMultipartFile();
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

    private MultipartFile getTestMultipartFile() throws IOException {
        Path path = Path.of("src/test/resources/setlists/" + FILE_NAME);
        byte[] content = Files.readAllBytes(path);

        return new MockMultipartFile(
                "file",
                FILE_NAME,
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                content
        );
    }
}
