package uk.seaofgreen.setlistbot.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SongTest {
    private static final String TITLE = "Song Title";
    private static final String KEY = "G";

    private Song song;

    @BeforeEach
    void beforeEach() {
        song = new Song(TITLE, KEY);
    }

    @Test
    void testGetSetTitle(){
        assertThat(song.getTitle(), is(TITLE));

        String newTitle = "NEW TITLE";
        song.setTitle(newTitle);
        assertThat(song.getTitle(), is(newTitle));
    }

    @Test
    void testGetSetKey(){
        assertThat(song.getKey(), is(KEY));

        String newKey = "A";
        song.setKey(newKey);
        assertThat(song.getKey(), is(newKey));
    }

    @Test
    public void toStringTest() {
        // When
        String toString = song.toString();

        // Then
        assertThat(toString, is("Song{title='Song Title', key='G'}"));
    }
}
