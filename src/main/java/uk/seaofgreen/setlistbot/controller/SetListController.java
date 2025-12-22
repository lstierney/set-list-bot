package uk.seaofgreen.setlistbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uk.seaofgreen.setlistbot.model.Song;

import java.util.List;

public interface SetListController {
    ResponseEntity<?> convertSetListToPlayList(
            MultipartFile file,
            String playlistName,
            String extraSongs
    );

    List<Song> getExtraSongs();
}
