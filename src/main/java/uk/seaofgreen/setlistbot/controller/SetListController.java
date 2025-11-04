package uk.seaofgreen.setlistbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface SetListController {
    ResponseEntity<?> convertSetListToPlayList(MultipartFile file, String playlistName);
}
