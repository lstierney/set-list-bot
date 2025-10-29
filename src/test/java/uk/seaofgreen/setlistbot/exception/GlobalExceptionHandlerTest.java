package uk.seaofgreen.setlistbot.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GlobalExceptionHandlerTest {
    private static final String SOMETHING_BAD_HAPPENED = "Something bad happened";
    private final SetListBotException setListBotException = new SetListBotException(new RuntimeException(SOMETHING_BAD_HAPPENED));

    @Test
    public void handleAllExceptions() {
        // Given
        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

        // When
        ResponseEntity<Map<String, Object>> responseEntity = globalExceptionHandler.handleAllExceptions(setListBotException);

        // Then
        Map<String, Object> body = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(body.get("status"), is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        assertThat(body.get("error"), is(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
        assertThat(body.get("message"), is("java.lang.RuntimeException: " + SOMETHING_BAD_HAPPENED));
    }
}
