package uk.seaofgreen.setlistbot.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.seaofgreen.setlistbot.service.PlayListServiceImpl;
import uk.seaofgreen.setlistbot.utils.TestUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class SetListControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${PLAYLIST_AUDIO_BASE_PATH}")
    private String playListAudioBasePath;

    @Test
    void uploadSetlist_withRealFile_generatesExactPlaylist() throws Exception {
        MockMultipartFile file = TestUtils.getSetListWithKeys();
        String playlistName = "Binkies_20_10_25";

        Map<String, String> expectedTracks = Map.of(
                "Walking_Blues_G.wav", "Walking Blues (G)",
                "Why I Sing the Blues_C_Orig.mp3", "Why I Sing The Blues (C)",
                "Suzie Q_E.wav", "Suzie Q (E)"
        );

        String content = mockMvc.perform(MockMvcRequestBuilders.multipart("/upload")
                        .file(file)
                        .param("playlistName", playlistName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"" + playlistName + ".xspf\""))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Assert tracks
        expectedTracks.forEach((fileName, title) -> {
            Path path = Paths.get(playListAudioBasePath, fileName);
            String encodedPath = PlayListServiceImpl.toFileUri(playListAudioBasePath + path.getFileName().toString());
            assert content.contains("<location>" + encodedPath + "</location>");
            assert content.contains("<title>" + title + "</title>");
        });
    }
}

