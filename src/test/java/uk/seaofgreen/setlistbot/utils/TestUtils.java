package uk.seaofgreen.setlistbot.utils;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import uk.seaofgreen.setlistbot.model.Song;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {
    private TestUtils() {}

    public static final String TEST_SETLIST_FILENAME = "HowlinWolf.Oct.2025.docx";

    public static Song getSong1() {
        return new Song("Walking Blues", "G");
    }

    public static Song getSong2() {
        return new Song("Why I Sing the Blues", "C");
    }

    public static Song getSong3() {
        return new Song("Suzie Q", "E");
    }

    public static List<Song>getSongs() {
        List<Song> songs = new ArrayList<>();
        songs.add(getSong1());
        songs.add(getSong2());
        songs.add(getSong3());
        return songs;
    }

    public static MockMultipartFile getTestMultipartFile() throws IOException {
        Path path = Path.of("src/test/resources/setlists/" + TEST_SETLIST_FILENAME);
        byte[] content = Files.readAllBytes(path);

        return new MockMultipartFile(
                "file",
                TEST_SETLIST_FILENAME,
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                content
        );
    }
}
