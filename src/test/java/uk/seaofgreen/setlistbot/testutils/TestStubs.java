package uk.seaofgreen.setlistbot.testutils;

import org.springframework.mock.web.MockMultipartFile;
import uk.seaofgreen.setlistbot.model.Song;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestStubs {
    private TestStubs() {}

    public static final String SETLIST_WITH_KEYS = "HowlinWolf.Oct.2025.docx";
    public static final String SETLIST_NUMBERED_NO_KEYS = "Rehearsal withPete.docx";
    public static final String DEFAULT_EXTRA_SONGS = "Leaving Trunk, Let the Good Times Roll";

    public static Song getSong1() {
        return new Song("Walking Blues", "G");
    }

    public static Song getSong2() {
        return new Song("Why I Sing The Blues", "C");
    }

    public static Song getSong3() {
        return new Song("Suzie Q", "E");
    }

    public static Song getArchiveSong() {
        return new Song("Archived Song", "G");
    }

    public static List<Song>getSongs() {
        List<Song> songs = new ArrayList<>();
        songs.add(getSong1());
        songs.add(getSong2());
        songs.add(getSong3());
        return songs;
    }

    public static MockMultipartFile getSetListWithKeys() throws IOException {
        return getSetList(SETLIST_WITH_KEYS);
    }

    public static MockMultipartFile getSetListNumberedNoKeys() throws IOException {
        return getSetList(SETLIST_NUMBERED_NO_KEYS);
    }

    private static MockMultipartFile getSetList(String setListFileName) throws IOException {
        Path path = Path.of("src/test/resources/setlists/" + setListFileName);
        byte[] content = Files.readAllBytes(path);

        return new MockMultipartFile(
                "file",
                setListFileName,
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                content
        );
    }
}
