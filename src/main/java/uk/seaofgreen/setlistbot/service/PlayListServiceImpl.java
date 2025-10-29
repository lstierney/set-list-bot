package uk.seaofgreen.setlistbot.service;

import uk.seaofgreen.setlistbot.model.Song;
import org.springframework.stereotype.Service;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Service
public class PlayListServiceImpl implements PlayListService {

    @Override
    public String buildPlaylist(Map<Song, Path> songPathMap, String playlistTitle) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // <playlist> root
            Element playlist = doc.createElement("playlist");
            playlist.setAttribute("version", "1");
            playlist.setAttribute("xmlns", "http://xspf.org/ns/0/");
            doc.appendChild(playlist);

            // <title>
            Element title = doc.createElement("title");
            title.setTextContent(playlistTitle);
            playlist.appendChild(title);

            // <trackList>
            Element trackList = doc.createElement("trackList");
            playlist.appendChild(trackList);

            for (Map.Entry<Song, Path> entry : songPathMap.entrySet()) {
                Path path = entry.getValue();
                if (path == null) continue; // skip unmatched

                Song song = entry.getKey();
                Element track = doc.createElement("track");

                Element location = doc.createElement("location");
                location.setTextContent(path.toUri().toString());
                track.appendChild(location);

                Element trackTitle = doc.createElement("title");
                trackTitle.setTextContent(capitalizeWords(song.title()) + " (" + song.key() + ")");
                track.appendChild(trackTitle);

                trackList.appendChild(track);
            }

            // Transform to string
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();

        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException("Error building XSPF playlist", e);
        }
    }

    private String capitalizeWords(String input) {
        String[] words = input.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
            }
        }
        return String.join(" ", words);
    }
}

