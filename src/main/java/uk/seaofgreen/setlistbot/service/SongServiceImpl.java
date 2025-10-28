package uk.seaofgreen.setlistbot.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.seaofgreen.setlistbot.exception.SetListBotException;
import uk.seaofgreen.setlistbot.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SongServiceImpl implements SongService {
    @Override
    public List<Song> parseSetList(MultipartFile file) {
        List<Song>results = new ArrayList<>();

        try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
            for (XWPFParagraph para : doc.getParagraphs()) {
                String line = para.getText().trim();

                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");

                if (parts.length < 2) continue;

                String key = parts[parts.length - 1];
                String title = String.join(" ", Arrays.copyOf(parts, parts.length - 1));

                results.add(new Song(title.toLowerCase(), key));
            }
        } catch (IOException e) {
            throw new SetListBotException(e);
        }
        return results;
    }
}
