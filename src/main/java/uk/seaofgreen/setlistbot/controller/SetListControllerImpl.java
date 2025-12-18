package uk.seaofgreen.setlistbot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.seaofgreen.setlistbot.dto.AudioFileMatcherResults;
import uk.seaofgreen.setlistbot.model.Song;
import uk.seaofgreen.setlistbot.service.AudioFileMatcherService;
import uk.seaofgreen.setlistbot.service.PlayListService;
import uk.seaofgreen.setlistbot.service.SongService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
public class SetListControllerImpl implements SetListController {
    private static final Logger logger = LoggerFactory.getLogger(SetListControllerImpl.class);

    private final SongService songService;
    private final AudioFileMatcherService audioFileMatcherService;
    private final PlayListService playListService;

    public SetListControllerImpl(SongService songService, AudioFileMatcherService audioFileMatcherService, PlayListService playListService) {
        this.songService = songService;
        this.audioFileMatcherService = audioFileMatcherService;
        this.playListService = playListService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<?> convertSetListToPlayList(
            @RequestParam("file") MultipartFile file,
            @RequestParam("playlistName") String playlistName,
            @RequestParam(name = "extraSongs", defaultValue = "") String extraSongs) {
        logger.info("originalFilename: '{}', Playlist name: '{}'", file.getOriginalFilename(), playlistName);

        List<Song> parsedSongs = songService.parseSetList(file);

        for (String s : extraSongs.split("\\r?\\n")) {
            if (!s.isBlank()) {
                parsedSongs.add(new Song(s.trim(), null));
            }
        }

        AudioFileMatcherResults audioFileMatcherResults = audioFileMatcherService.matchSongsToAudioFiles(parsedSongs, 85);

        if (audioFileMatcherResults.getNotMatched().isEmpty()) { // All songs matched = return playlist
            String playlist = playListService.buildPlaylist(audioFileMatcherResults.getMatches(), playlistName);
            byte[] playlistBytes = playlist.getBytes(StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + playlistName + ".xspf\"")
                    .contentType(MediaType.APPLICATION_XML)
                    .contentLength(playlistBytes.length)
                    .body(playlistBytes);
        } else { // Some songs unmatched = return JSON with unmatched song details
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("unmatchedSongs", audioFileMatcherResults.getNotMatched()));
        }
    }

}
