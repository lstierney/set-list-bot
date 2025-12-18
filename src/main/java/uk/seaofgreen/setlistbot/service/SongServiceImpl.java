package uk.seaofgreen.setlistbot.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.seaofgreen.setlistbot.exception.SetListBotException;
import uk.seaofgreen.setlistbot.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SongServiceImpl implements SongService {

    @Override
    public List<Song> parseSetList(MultipartFile file) {
        List<Song> results = new ArrayList<>();

        try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
            for (XWPFParagraph para : doc.getParagraphs()) {
                String line = para.getText().trim();
                if (line.isEmpty()) {
                    continue;
                }

                List<String> parts = List.of(line.split("\\s+"));
                parts = removeLeadingNumbers(parts);

                String key = getSongKey(parts);

                int titleEnd = key == null ? parts.size() : parts.size() - 1;
                String title = String.join(" ", parts.subList(0, titleEnd));

                results.add(new Song(title.toLowerCase(), key));
            }
        } catch (IOException e) {
            throw new SetListBotException(e);
        }

        return results;
    }

    private String getSongKey(List<String> parts) {
        String candidate = parts.get(parts.size() - 1);
        return candidate.matches("[a-zA-Z]") ? candidate : null;
    }

    private List<String> removeLeadingNumbers(List<String> parts) {
        if (parts.get(0).matches("^\\d+\\.?")) {
            return parts.subList(1, parts.size());
        }
        return parts;
    }
}
