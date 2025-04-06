package tutorly.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tutorly.storage.JsonAdaptedSession.MISSING_FIELD_MESSAGE_FORMAT;
import static tutorly.testutil.Assert.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import tutorly.commons.exceptions.IllegalValueException;
import tutorly.logic.parser.ParserUtil;
import tutorly.model.session.Session;
import tutorly.model.session.Subject;
import tutorly.model.session.Timeslot;

public class JsonAdaptedSessionTest {
    private static final int VALID_SESSION_ID = 1;
    private static final String VALID_START_TIME = "2020-01-01T10:00:00";
    private static final String VALID_END_TIME = "2020-01-01T12:00:00";
    private static final String VALID_SUBJECT = "Mathematics";

    private static final int INVALID_SESSION_ID = 0;
    private static final String INVALID_START_TIME = "2020-01-01T14:00:00"; // start time after end time
    private static final String INVALID_DATETIME = "21-03-2025"; // Incorrect format
    private static final String INVALID_SUBJECT = ""; // Empty subject

    @Test
    public void toModelType_validSessionDetails_returnsSession() throws Exception {
        Timeslot timeslot = new Timeslot(LocalDateTime.parse(VALID_START_TIME), LocalDateTime.parse(VALID_END_TIME));
        Session session = new Session(timeslot, new Subject(VALID_SUBJECT));
        session.setId(VALID_SESSION_ID);
        JsonAdaptedSession adaptedSession = new JsonAdaptedSession(session);
        assertEquals(session, adaptedSession.toModelType());
    }

    @Test
    public void toModelType_nullStartAndEndTime_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_SESSION_ID, null, null, VALID_SUBJECT);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "startTime");
        assertThrows(IllegalValueException.class, expectedMessage, session::toModelType);
    }

    @Test
    public void toModelType_nullStartTime_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_SESSION_ID, null, VALID_END_TIME, VALID_SUBJECT);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "startTime");
        assertThrows(IllegalValueException.class, expectedMessage, session::toModelType);
    }

    @Test
    public void toModelType_nullEndTime_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_SESSION_ID, VALID_START_TIME, null, VALID_SUBJECT);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "endTime");
        assertThrows(IllegalValueException.class, expectedMessage, session::toModelType);
    }

    @Test
    public void toModelType_invalidStartTime_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(
                VALID_SESSION_ID, INVALID_DATETIME, VALID_END_TIME, VALID_SUBJECT);
        assertThrows(IllegalValueException.class, ParserUtil.MESSAGE_INVALID_DATETIME, session::toModelType);
    }

    @Test
    public void toModelType_invalidEndTime_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(
                VALID_SESSION_ID, VALID_START_TIME, INVALID_DATETIME, VALID_SUBJECT);
        assertThrows(IllegalValueException.class, ParserUtil.MESSAGE_INVALID_DATETIME, session::toModelType);
    }

    @Test
    public void toModelType_nullSubject_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(VALID_SESSION_ID, VALID_START_TIME, VALID_END_TIME, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "subject");
        assertThrows(IllegalValueException.class, expectedMessage, session::toModelType);
    }

    @Test
    public void toModelType_startAfterEndTime_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(
                VALID_SESSION_ID, INVALID_START_TIME, VALID_END_TIME, VALID_SUBJECT);
        assertThrows(IllegalValueException.class, Timeslot.MESSAGE_END_BEFORE_START_DATETIME, session::toModelType);
    }

    @Test
    public void toModelType_invalidSubject_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(
                VALID_SESSION_ID, VALID_START_TIME, VALID_END_TIME, INVALID_SUBJECT);
        assertThrows(IllegalValueException.class, Subject.MESSAGE_CONSTRAINTS, session::toModelType);
    }

    @Test
    public void toModelType_invalidSessionId_throwsIllegalValueException() {
        JsonAdaptedSession session = new JsonAdaptedSession(
                INVALID_SESSION_ID, VALID_START_TIME, VALID_END_TIME, VALID_SUBJECT);
        assertThrows(IllegalValueException.class, Session.MESSAGE_INVALID_ID, session::toModelType);
    }
}
