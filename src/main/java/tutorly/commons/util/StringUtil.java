package tutorly.commons.util;

import static java.util.Objects.requireNonNull;
import static tutorly.commons.util.AppUtil.checkArgument;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * Helper functions for handling strings.
 */
public class StringUtil {

    /**
     * Returns true if {@code keyword} is a substring of any word in {@code sentence}.
     *   Ignores case and a partial match within a word is allowed.
     *   <br>examples:<pre>
     *       containsWordIgnoreCase("ABc def", "abc") == true
     *       containsWordIgnoreCase("ABc def", "DE") == true // partial match allowed
     *       containsWordIgnoreCase("ABc def", "ABcd") == false
     *       </pre>
     * @param sentence cannot be null
     * @param keyword cannot be null, cannot be empty, must be a single word
     */
    public static boolean containsWordIgnoreCase(String sentence, String keyword) {
        requireNonNull(sentence);
        requireNonNull(keyword);

        String preppedKeyword = keyword.trim();
        checkArgument(!preppedKeyword.isEmpty(), "Keyword parameter cannot be empty");
        checkArgument(
                preppedKeyword.split("\\s+").length == 1,
                "Keyword parameter should be a single word");

        String[] wordsInSentence = sentence.split("\\s+");

        return Arrays.stream(wordsInSentence)
                .anyMatch(w -> w.toLowerCase().contains(preppedKeyword.toLowerCase()));
    }

    /**
     * Returns a detailed message of the t, including the stack trace.
     */
    public static String getDetails(Throwable t) {
        requireNonNull(t);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return t.getMessage() + "\n" + sw.toString();
    }

    /**
     * Returns true if {@code s} represents a non-zero unsigned integer, that can be parsed into an integer.
     * e.g. 1, 2, 3, ..., {@code Integer.MAX_VALUE} <br>
     * Will return false for any other non-null string input
     * e.g. empty string, "-1", "0", "+1", and " 2 " (untrimmed), "3 0" (contains whitespace), "1 a" (contains letters)
     * @throws NullPointerException if {@code s} is null.
     */
    public static boolean isParsableNonZeroUnsignedInteger(String s) {
        requireNonNull(s);

        try {
            int value = Integer.parseInt(s);
            return value > 0 && !s.startsWith("+"); // "+1" is successfully parsed by Integer#parseInt(String)
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Returns true if {@code s} represents a non-zero unsigned integer.
     */
    public static boolean isNonZeroUnsignedInteger(String s) {
        requireNonNull(s);
        return s.matches("[1-9]\\d*");
    }

}
