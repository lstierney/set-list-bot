package uk.seaofgreen.setlistbot.model;

public record Song(String title, String key) {
    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
