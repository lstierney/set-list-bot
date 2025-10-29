package uk.seaofgreen.setlistbot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.seaofgreen.setlistbot.model.Song;
import uk.seaofgreen.setlistbot.service.AudioFileMatcherService;
import uk.seaofgreen.setlistbot.service.PlayListService;
import uk.seaofgreen.setlistbot.service.SongService;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
    public ResponseEntity<byte[]> convertSetListToPlayList(
            @RequestParam("file") MultipartFile file,
            @RequestParam("playlistName") String playlistName) {

        logger.info("Received file: {}", file.getOriginalFilename());
        logger.info("Playlist name: {}", playlistName);

        List<Song> results = songService.parseSetList(file);
        Map<Song, Path> songPathMap = audioFileMatcherService.matchSongsToAudioFiles(results, 85);
        String playlist = playListService.buildPlaylist(songPathMap, playlistName);

        byte[] playlistBytes = playlist.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + playlistName + ".xspf" + "\"")
                .contentType(MediaType.APPLICATION_XML)
                .contentLength(playlistBytes.length)
                .body(playlistBytes);
    }
}
