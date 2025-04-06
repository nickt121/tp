package tutorly.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import tutorly.commons.util.StringUtil;
import tutorly.logic.parser.exceptions.ParseException;
import tutorly.model.attendancerecord.Feedback;
import tutorly.model.person.Address;
import tutorly.model.person.Email;
import tutorly.model.person.Identity;
import tutorly.model.person.Memo;
import tutorly.model.person.Name;
import tutorly.model.person.Phone;
import tutorly.model.session.Session;
import tutorly.model.session.Subject;
import tutorly.model.session.Timeslot;
import tutorly.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_DATETIME = "Invalid datetime or incorrect datetime format. "
            + "Please ensure it follows the format 'yyyy-MM-ddTHH:mm' (e.g. '2025-12-25T10:00') and is a valid "
            + "datetime.";
    public static final String MESSAGE_INVALID_DATE_FORMAT = "Invalid date or incorrect date format. "
            + "Please ensure it follows the format 'dd MMM yyyy' (e.g. '25 Dec 2025') and is a valid date.";
    public static final String MESSAGE_INVALID_TIMESLOT_FORMAT = "Invalid timeslot or incorrect timeslot format. "
            + "Please ensure it follows the format 'dd MMM yyyy HH:mm-HH:mm' or 'dd MMM yyyy HH:mm-dd MMM yyyy HH:mm' "
            + "(e.g. '25 Dec 2025 10:00-25 Dec 2025 12:00'), and the date and time provided is valid.";
    public static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMM uuuu")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses {@code String identity} into an {@code Identity} and returns it.
     * Leading and trailing whitespaces will be trimmed.
     * Multiple intermediate spaces will be collapsed into one space.
     *
     * @throws ParseException if the specified identity is invalid (not non-zero unsigned integer or valid name).
     */
    public static Identity parseIdentity(String identity) throws ParseException {
        requireNonNull(identity);
        String trimmedIdentity = identity.trim().replaceAll("\\s+", " ");
        if (StringUtil.isNonZeroUnsignedInteger(trimmedIdentity)) {
            return new Identity(Integer.parseInt(trimmedIdentity));
        }
        if (Name.isValidName(trimmedIdentity)) {
            return new Identity(new Name(trimmedIdentity));
        }
        throw new ParseException(Identity.MESSAGE_INVALID_IDENTITY);
    }

    /**
     * Parses Session {@code String id} into an {@code int} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     *
     * @throws ParseException if the specified id is invalid (not non-zero unsigned integer).
     */
    public static int parseSessionId(String id) throws ParseException {
        requireNonNull(id);
        String trimmedId = id.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedId)) {
            throw new ParseException(Session.MESSAGE_INVALID_ID);
        }
        return Integer.parseInt(trimmedId);
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     * Multiple intermediate spaces will be collapsed into one space.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim().replaceAll("\\s+", " ");
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     * Multiple intermediate spaces will be collapsed into one space.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim().replaceAll("\\s+", " ");
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a {@code String memo} into an {@code Memo}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code memo} is invalid.
     */
    public static Memo parseMemo(String memo) throws ParseException {
        requireNonNull(memo);
        String trimmedMemo = memo.trim();
        if (!Memo.isValidMemo(trimmedMemo)) {
            throw new ParseException(Memo.MESSAGE_CONSTRAINTS);
        }
        return new Memo(trimmedMemo);
    }

    /**
     * Parses a {@code String date} into a {@code LocalDate}.
     * The date format must be d MMM yyyy.
     *
     * @param dateStr The date string to parse.
     * @return The parsed LocalDate.
     * @throws ParseException if the date format is invalid.
     */
    public static LocalDate parseDate(String dateStr) throws ParseException {
        requireNonNull(dateStr);
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_DATE_FORMAT);
        }
    }

    /**
     * Parses a {@code String timeslot} into a {@code Timeslot}.
     * The timeslot format must be d MMM yyyy H:mm-H:mm or d MMM yyyy H:mm-d MMM yyyy H:mm.
     *
     * @param timeslot The timeslot to parse.
     * @return The parsed Timeslot.
     * @throws ParseException if the date format is invalid.
     */
    public static Timeslot parseTimeslot(String timeslot) throws ParseException {
        requireNonNull(timeslot);

        // Split the timeslot into start and end times based on the first hyphen
        String[] tokens = timeslot.trim().split("-");
        if (tokens.length != 2) {
            throw new ParseException(MESSAGE_INVALID_TIMESLOT_FORMAT);
        }
        String startDateTimeStr = tokens[0].trim();
        String endDateTimeStr = tokens[1].trim();

        // Process start datetime
        String[] startTokens = startDateTimeStr.split("\\s+");
        if (startTokens.length != 4) {
            throw new ParseException(MESSAGE_INVALID_TIMESLOT_FORMAT);
        }
        String startDateStr = startTokens[0] + " " + startTokens[1] + " " + startTokens[2];
        String startTimeStr = startTokens[3];

        // Process end datetime
        String[] endTokens = endDateTimeStr.split("\\s+");
        String endDateStr;
        String endTimeStr;
        if (endTokens.length == 1) {
            endDateStr = startDateStr;
            endTimeStr = endTokens[0];
        } else if (endTokens.length == 4) {
            endDateStr = endTokens[0] + " " + endTokens[1] + " " + endTokens[2];
            endTimeStr = endTokens[3];
        } else {
            throw new ParseException(MESSAGE_INVALID_TIMESLOT_FORMAT);
        }

        LocalDate startDate;
        LocalDate endDate;
        LocalTime startTime;
        LocalTime endTime;
        try {
            startDate = LocalDate.parse(startDateStr, DATE_FORMATTER);
            endDate = LocalDate.parse(endDateStr, DATE_FORMATTER);
            startTime = LocalTime.parse(startTimeStr, TIME_FORMATTER);
            endTime = LocalTime.parse(endTimeStr, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_TIMESLOT_FORMAT);
        }

        // Combine date and time into LocalDateTime objects
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        if (!endDateTime.isAfter(startDateTime)) {
            throw new ParseException(Timeslot.MESSAGE_END_BEFORE_START_DATETIME);
        }

        return new Timeslot(startDateTime, endDateTime);
    }

    /**
     * Parses a {@code String subject} into a {@code Subject}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code subject} is invalid.
     */
    public static Subject parseSubject(String subject) throws ParseException {
        requireNonNull(subject);
        String trimmedSubject = subject.trim();
        if (!Subject.isValidSubject(trimmedSubject)) {
            throw new ParseException(Subject.MESSAGE_CONSTRAINTS);
        }
        return new Subject(trimmedSubject);
    }

    /**
     * Parses a {@code String feedback} into a {@code Feedback}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code feedback} is invalid.
     */
    public static Feedback parseFeedback(String feedback) throws ParseException {
        requireNonNull(feedback);
        String trimmedFeedback = feedback.trim();
        if (!Feedback.isValidFeedback(trimmedFeedback)) {
            throw new ParseException(Feedback.MESSAGE_CONSTRAINTS);
        }
        return new Feedback(trimmedFeedback);
    }
}
