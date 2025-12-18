package uk.seaofgreen.setlistbot.model;

public class Song {
    private String title;
    private String key;

    public Song(String title, String key) {
        this.title = title;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}

