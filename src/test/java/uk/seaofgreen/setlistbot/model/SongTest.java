package uk.seaofgreen.setlistbot.model;

import org.junit.jupiter.api.Test;
import uk.seaofgreen.setlistbot.utils.TestUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SongTest {
    @Test
    public void toStringTest() {
        // Given
        Song song = TestUtils.getSong1();

        // When
        String toString = song.toString();

        // Then
        assertThat(toString, is("Song{title='Walking Blues', key='G'}"));
    }
}
