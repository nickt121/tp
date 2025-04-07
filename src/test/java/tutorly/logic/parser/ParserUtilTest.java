package tutorly.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tutorly.model.person.Identity.MESSAGE_INVALID_IDENTITY;
import static tutorly.testutil.Assert.assertThrows;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import tutorly.logic.parser.exceptions.ParseException;
import tutorly.model.attendancerecord.Feedback;
import tutorly.model.person.Address;
import tutorly.model.person.Email;
import tutorly.model.person.Identity;
import tutorly.model.person.Name;
import tutorly.model.person.Phone;
import tutorly.model.session.Session;
import tutorly.model.session.Timeslot;
import tutorly.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_ID = "-1";
    private static final String INVALID_NAME = "R!chel";
    private static final String INVALID_PHONE = "++651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = " ";
    private static final String INVALID_FEEDBACK = " Feedback";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";
    private static final String VALID_FEEDBACK = "Great session!";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseSessionIdentity_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseIdentity(null));
    }

    @Test
    public void parseIdentity_invalidValue_throwsParseSessionException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_IDENTITY, () -> ParserUtil.parseIdentity(INVALID_NAME));
        assertThrows(ParseException.class, MESSAGE_INVALID_IDENTITY, () -> ParserUtil.parseIdentity(INVALID_ID));
    }

    @Test
    public void parseSessionIdentity_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(new Identity(new Name(VALID_NAME)), ParserUtil.parseIdentity(VALID_NAME));
        assertEquals(new Identity(1), ParserUtil.parseIdentity("1"));

        // Leading and trailing whitespaces
        assertEquals(new Identity(new Name(VALID_NAME)),
                ParserUtil.parseIdentity(WHITESPACE + VALID_NAME + WHITESPACE));
        assertEquals(new Identity(1), ParserUtil.parseIdentity(WHITESPACE + 1 + WHITESPACE));
        assertEquals(new Identity(new Name("Bob Charlie")),
                ParserUtil.parseIdentity(WHITESPACE + "Bob     Charlie" + WHITESPACE));
    }

    @Test
    public void parseSessionId_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseSessionId(null));
    }

    @Test
    public void parseId_invalidInput_throwsParseSessionException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseSessionId("10 a"));
    }

    @Test
    public void parseId_outOfRangeInput_throwsParseSessionException() {
        assertThrows(ParseException.class, Session.MESSAGE_INVALID_ID, () -> ParserUtil.parseSessionId("0"));
        assertThrows(ParseException.class, Session.MESSAGE_INVALID_ID, () -> ParserUtil.parseSessionId("-1"));
        assertThrows(ParseException.class, Session.MESSAGE_INVALID_ID, () -> ParserUtil.parseSessionId("1.1"));
    }

    @Test
    public void parseSessionId_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(1, ParserUtil.parseSessionId("1"));

        // Leading and trailing whitespaces
        assertEquals(1, ParserUtil.parseSessionId("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName(null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parseName_validValueWithIntermediateWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + "Alice     Bob   Pauline" + WHITESPACE;
        Name expectedName = new Name("Alice Bob Pauline");
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone(null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress(null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseAddress_validValueWithIntermediateWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = WHITESPACE + "123     Main    Street     #0505" + WHITESPACE;
        Address expectedAddress = new Address("123 Main Street #0505");
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail(null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(Collections.emptyList()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(Arrays.asList(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = new HashSet<Tag>(Arrays.asList(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2)));

        assertEquals(expectedTagSet, actualTagSet);
    }

    @Test
    public void parseTimeslot_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTimeslot(null));
    }

    @Test
    public void parseTimeslot_validValueWithoutWhitespace_returnsTimeslot() throws Exception {
        String validTimeslot = "25 Mar 2025 10:00-12:00";
        Timeslot expectedTimeslot = new Timeslot(LocalDateTime.of(2025, 3, 25, 10, 0),
                LocalDateTime.of(2025, 3, 25, 12, 0));
        assertEquals(expectedTimeslot, ParserUtil.parseTimeslot(validTimeslot));
    }

    @Test
    public void parseTimeslot_validValueWithoutWhitespaceWithEndDate_returnsTimeslot() throws Exception {
        String validTimeslot = "9 Mar 2025 9:00-10 Mar 2025 12:00";
        Timeslot expectedTimeslot = new Timeslot(LocalDateTime.of(2025, 3, 9, 9, 0),
                LocalDateTime.of(2025, 3, 10, 12, 0));
        assertEquals(expectedTimeslot, ParserUtil.parseTimeslot(validTimeslot));
    }

    @Test
    public void parseTimeslot_validValueWithWhitespace_returnsTrimmedTimeslot() throws Exception {
        String timeslotWithWhitespace = WHITESPACE + "25  Mar  2025  09:00  -  12:00" + WHITESPACE;
        Timeslot expectedTimeslot = new Timeslot(LocalDateTime.of(2025, 3, 25, 9, 0),
                LocalDateTime.of(2025, 3, 25, 12, 0));
        assertEquals(expectedTimeslot, ParserUtil.parseTimeslot(timeslotWithWhitespace));
    }

    @Test
    public void parseTimeslot_validValueWithWhitespaceWithEndDate_returnsTrimmedTimeslot() throws Exception {
        String timeslotWithWhitespace = WHITESPACE + "25  Mar   2025  10:00   -   26  Mar   2025    12:00" + WHITESPACE;
        Timeslot expectedTimeslot = new Timeslot(LocalDateTime.of(2025, 3, 25, 10, 0),
                LocalDateTime.of(2025, 3, 26, 12, 0));
        assertEquals(expectedTimeslot, ParserUtil.parseTimeslot(timeslotWithWhitespace));
    }

    @Test
    public void parseTimeslot_sameStartAndEndTime_throwsParseException() throws Exception {
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25 Mar 2025 10:00-10:00"));
    }

    @Test
    public void parseTimeslot_sameStartAndEndTimeWithEndDate_throwsParseException() throws Exception {
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25 Mar 2025 10:00-25 Mar 2025 10:00"));
    }

    @Test
    public void parseTimeslot_invalidDateFormat_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25/3/2025 10:00-12:00"));
    }

    @Test
    public void parseTimeslot_invalidEndDateFormat_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25 Mar 2025 10:00-25/3/2025 12:00"));
    }

    @Test
    public void parseTimeslot_invalidTimeFormat_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25 Mar 2025 10:00-12:00-14:00"));
    }

    @Test
    public void parseTimeslot_invalidEndTimeFormat_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25 Mar 2025 10:00-25 Mar 2025 1200"));
    }

    @Test
    public void parseTimeslot_endBeforeStartTime_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25 Mar 2025 12:00-10:00"));
    }

    @Test
    public void parseTimeslot_endBeforeStartTimeWithEndDate_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25 Mar 2025 12:00-25 Mar 2025 10:00"));
    }

    @Test
    public void parseTimeslot_emptyString_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot(""));
    }

    @Test
    public void parseTimeslot_missingHyphen_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25 Mar 2025 10:00 12:00"));
    }

    @Test
    public void parseTimeslot_shortMonthName_throwsParseException() {
        // Invalid month abbreviation
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25 Ma 2025 10:00-12:00"));
    }

    @Test
    public void parseTimeslot_dayOutOfRange_throwsParseException() {
        // Invalid day value
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("32 Mar 2025 10:00-12:00"));
    }

    @Test
    public void parseTimeslot_monthOutOfRange_throwsParseException() {
        // Invalid month name
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25 Mxr 2025 10:00-12:00"));
    }

    @Test
    public void parseTimeslot_timeOutOfRange_throwsParseException() {
        // Invalid month name
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25 Mar 2025 10:00-25:00"));
    }

    @Test
    public void parseTimeslot_partialTime_throwsParseException() {
        // Missing one digit in the time
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25 Mar 2025 5:0-7:00"));
    }

    @Test
    public void parseTimeslot_minimalBoundaryDateTime_returnsTimeslot() throws Exception {
        // Example testing a lower date boundary scenario
        String validTimeslot = "01 Jan 1970 00:00-01 Jan 1970 01:00";
        Timeslot parsedTimeslot = ParserUtil.parseTimeslot(validTimeslot);
        assertEquals(LocalDateTime.of(1970, 1, 1, 0, 0), parsedTimeslot.getStartTime());
        assertEquals(LocalDateTime.of(1970, 1, 1, 1, 0), parsedTimeslot.getEndTime());
    }

    @Test
    public void parseTimeslot_leapYearDay_returnsTimeslot() throws Exception {
        String timeslot = "29 Feb 2024 10:00-29 Feb 2024 12:00";
        Timeslot expected = new Timeslot(
                LocalDateTime.of(2024, 2, 29, 10, 0),
                LocalDateTime.of(2024, 2, 29, 12, 0)
        );
        assertEquals(expected, ParserUtil.parseTimeslot(timeslot));
    }

    @Test
    public void parseTimeslot_crossMidnightSameDay_returnsTimeslot() throws Exception {
        String timeslot = "25 Mar 2025 00:00-01:00";
        Timeslot expected = new Timeslot(
                LocalDateTime.of(2025, 3, 25, 0, 0),
                LocalDateTime.of(2025, 3, 25, 1, 0)
        );
        assertEquals(expected, ParserUtil.parseTimeslot(timeslot));
    }

    @Test
    public void parseTimeslot_crossMidnightDifferentDays_returnsTimeslot() throws Exception {
        String timeslot = "25 Mar 2025 23:00-26 Mar 2025 01:00";
        Timeslot expected = new Timeslot(
                LocalDateTime.of(2025, 3, 25, 23, 0),
                LocalDateTime.of(2025, 3, 26, 1, 0)
        );
        assertEquals(expected, ParserUtil.parseTimeslot(timeslot));
    }

    @Test
    public void parseTimeslot_minimumTime_returnsTimeslot() throws Exception {
        String timeslot = "25 Mar 2025 00:00-00:01";
        Timeslot expected = new Timeslot(
                LocalDateTime.of(2025, 3, 25, 0, 0),
                LocalDateTime.of(2025, 3, 25, 0, 1)
        );
        assertEquals(expected, ParserUtil.parseTimeslot(timeslot));
    }

    @Test
    public void parseTimeslot_noSpaceBetweenDateAndTime_throwsParseException() {
        // Missing space: "25Mar 2025" is invalid
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25Mar 2025 10:00-12:00"));
    }

    @Test
    public void parseTimeslot_nonExistentDateNonLeap_throwsParseException() {
        // 29 Feb 2023 is not valid because 2023 is not a leap year
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("29 Feb 2023 10:00-12:00"));
    }

    @Test
    public void parseTimeslot_crossYearBoundary_returnsTimeslot() throws Exception {
        // Crossing from 31 Dec 2025 to 01 Jan 2026
        String timeslot = "31 Dec 2025 23:00-01 Jan 2026 01:00";
        Timeslot expected = new Timeslot(
                LocalDateTime.of(2025, 12, 31, 23, 0),
                LocalDateTime.of(2026, 1, 1, 1, 0)
        );
        assertEquals(expected, ParserUtil.parseTimeslot(timeslot));
    }

    @Test
    public void parseTimeslot_invalid24Hour_throwsParseException() {
        // 24:00 is not recognized by LocalTime.parse with HH:mm
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25 Mar 2025 24:00-25 Mar 2025 01:00"));
    }

    @Test
    public void parseTimeslot_missingDateComponent_throwsParseException() {
        // Missing the month field
        assertThrows(ParseException.class, () -> ParserUtil.parseTimeslot("25 2025 10:00-12:00"));
    }

    @Test
    public void parseFeedback_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseFeedback(null));
    }

    @Test
    public void parseFeedback_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseFeedback("a" + "a".repeat(Feedback.MAX_LENGTH)));
    }

    @Test
    public void parseFeedback_validValueWithoutWhitespace_returnsFeedback() throws Exception {
        assertEquals(new Feedback(VALID_FEEDBACK), ParserUtil.parseFeedback(VALID_FEEDBACK));
    }

    @Test
    public void parseFeedback_validValueWithWhitespace_returnsTrimmedFeedback() throws Exception {
        String feedbackWithWhitespace = WHITESPACE + VALID_FEEDBACK + WHITESPACE;
        assertEquals(new Feedback(VALID_FEEDBACK), ParserUtil.parseFeedback(feedbackWithWhitespace));
    }
}
