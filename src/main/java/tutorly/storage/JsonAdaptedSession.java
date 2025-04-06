package tutorly.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import tutorly.commons.exceptions.IllegalValueException;
import tutorly.logic.parser.ParserUtil;
import tutorly.model.session.Session;
import tutorly.model.session.Subject;
import tutorly.model.session.Timeslot;

/**
 * Jackson-friendly version of {@link Session}.
 */
class JsonAdaptedSession {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Session's %s field is missing!";

    private final int id;
    private final String startTime;
    private final String endTime;
    private final String subject;

    /**
     * Constructs a {@code JsonAdaptedSession} with the given session details.
     */
    @JsonCreator
    public JsonAdaptedSession(@JsonProperty("id") int id,
                              @JsonProperty("startTime") String startTime,
                              @JsonProperty("endTime") String endTime,
                              @JsonProperty("subject") String subject) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
    }

    /**
     * Converts a given {@code Session} into this class for Jackson use.
     */
    public JsonAdaptedSession(Session source) {
        this.id = source.getId();
        this.subject = source.getSubject().subjectName;

        Timeslot timeslot = source.getTimeslot();
        this.startTime = timeslot.getStartTime().toString();
        this.endTime = timeslot.getEndTime().toString();
    }

    /**
     * Converts this Jackson-friendly adapted session object into the model's {@code Session} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted session.
     */
    public Session toModelType() throws IllegalValueException {
        if (id <= 0) {
            throw new IllegalValueException(Session.MESSAGE_INVALID_ID);
        }

        if (startTime == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "startTime"));
        }

        if (endTime == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "endTime"));
        }

        final Timeslot modelTimeslot;
        try {
            modelTimeslot = new Timeslot(LocalDateTime.parse(startTime), LocalDateTime.parse(endTime));
        } catch (DateTimeParseException e) {
            throw new IllegalValueException(ParserUtil.MESSAGE_INVALID_DATETIME);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(e.getMessage());
        }

        if (subject == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "subject"));
        }
        if (!Subject.isValidSubject(subject)) {
            throw new IllegalValueException(Subject.MESSAGE_CONSTRAINTS);
        }
        final Subject modelSubject = new Subject(subject);


        Session session = new Session(modelTimeslot, modelSubject);
        session.setId(id);

        return session;
    }
}
