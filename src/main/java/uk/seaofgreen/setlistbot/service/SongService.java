package uk.seaofgreen.setlistbot.service;

import org.springframework.web.multipart.MultipartFile;
import uk.seaofgreen.setlistbot.model.Song;

import java.util.List;

public interface SongService {
    List<Song> parseSetList(MultipartFile file);
}
