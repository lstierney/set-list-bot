package uk.seaofgreen.setlistbot.controller.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import uk.seaofgreen.setlistbot.controller.SetListControllerImpl;
import uk.seaofgreen.setlistbot.dto.AudioFileMatcherResults;
import uk.seaofgreen.setlistbot.model.Song;
import uk.seaofgreen.setlistbot.service.*;
import uk.seaofgreen.setlistbot.testutils.TestStubs;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SetListControllerImpl.class)
@ActiveProfiles("dev")
public class SetListControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SongService songService;

    @MockitoBean
    private AudioFileMatcherService audioFileMatcherService;

    @MockitoBean
    private PlayListService playListService;


    @Test
    void convertSetListToPlayList_allFilesMatched() throws Exception {
        // Given
        MockMultipartFile multipartFile = TestStubs.getSetListWithKeys();
        String playListName = "Playlist Name";
        List<Song> parsedSongs = TestStubs.getSongs();
        AudioFileMatcherResults audioFileMatcherResults = new AudioFileMatcherResults();

        given(songService.parseSetList(multipartFile)).willReturn(parsedSongs);
        given(audioFileMatcherService.matchSongsToAudioFiles(parsedSongs, 85)).willReturn(audioFileMatcherResults);
        given(playListService.buildPlaylist(anyMap(), any(String.class))).willReturn("This is the playlist");

        // When & Then
        mockMvc.perform(multipart("/upload")
                        .file(multipartFile)
                        .param("playlistName", playListName)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("This is the playlist"));
    }

    @Test
    void convertSetListToPlayList_someUnmatchedFiles() throws Exception {
        // Given
        MockMultipartFile multipartFile = TestStubs.getSetListWithKeys();
        String playListName = "Playlist Name";
        List<Song> parsedSongs = TestStubs.getSongs();
        AudioFileMatcherResults audioFileMatcherResults = new AudioFileMatcherResults();
        audioFileMatcherResults.addNotMatched(parsedSongs.get(0));

        given(songService.parseSetList(multipartFile)).willReturn(parsedSongs);
        given(audioFileMatcherService.matchSongsToAudioFiles(parsedSongs, 85)).willReturn(audioFileMatcherResults);
        given(playListService.buildPlaylist(anyMap(), any(String.class))).willReturn("This is the playlist");

        // When & Then
        mockMvc.perform(multipart("/upload")
                        .file(multipartFile)
                        .param("playlistName", playListName)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"unmatchedSongs\":[{\"title\":\"Walking Blues\",\"key\":\"G\"}]}"));
    }

    @Test
    void getExtraSongs_songsFound() throws Exception {
        // Given
        given(songService.getExtraSongs()).willReturn(TestStubs.getSongs());

        // When & Then
        mockMvc.perform(get("/extraSongs"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(TestStubs.getSongs())));
    }

    @Test
    void getExtraSongs_noSongsFound() throws Exception {
        // Given
        given(songService.getExtraSongs()).willReturn(new ArrayList<>());

        // When & Then
        mockMvc.perform(get("/extraSongs"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
