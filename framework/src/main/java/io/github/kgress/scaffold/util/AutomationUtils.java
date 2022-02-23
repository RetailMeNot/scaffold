package io.github.kgress.scaffold.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

/**
 * A class for shared Automation Utilities.
 */
@Slf4j
public class AutomationUtils {

    /**
     * Returns a string representation of the exception.
     *
     * @param t the {@link Throwable} to get the stack trace from.
     * @return the stack trace as {@link String}.
     */
    public static String getStackTrace(Throwable t) {
        if (t == null) {
            return null;
        }
        var sw = new StringWriter();
        var pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

    /**
     * Returns the text from HTML source code between the given parameters. Returns an empty string if the desired
     * anchors were not found.
     *
     * @param pageSource the page source to pull text from.
     * @param startsWith the beginning anchor for pulling the text from the page source.
     * @param endsWith the end anchor for pulling the text from the page source.
     * @return the text as {@link String}.
     */
    public static String getTextFromHTML(String pageSource, String startsWith, String endsWith) {
        if (!pageSource.contains(startsWith) || !pageSource.contains(endsWith)) {
            log.debug(String.format("Could not locate text from html - [%s~%s]", startsWith, endsWith));
            return "";
        }
        var lengthOfStartsWithWord = startsWith.length();
        var startingIndex = pageSource.indexOf(startsWith) + lengthOfStartsWithWord;
        var endingIndex = pageSource.indexOf(endsWith, startingIndex);

        return pageSource.substring(startingIndex, endingIndex);
    }

    /**
     * Gets the URL and encodes it as a {@link String}.
     *
     * @param url the url to encode.
     * @return the encoded URL as {@link String}.
     */
    public static String getURLEncodedString(final String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

    /**
     * Returns a UUID without any dashes. Alphanumeric only.
     *
     * @return A full GUID with 32 characters and no punctuation.
     */
    public static String getUniqueString() {
        return getUniqueString(-1);
    }

    /**
     * Returns a randomly generated string of the length specified - the upper limit is the upper limit of int.
     *
     * @param maxLength The maximum length of string you want, e.g. 10 would give you something like 8A7AF4C324.
     *                  Passing a negative number will just return a UUID of standard length.
     * @return The generated string of the given length.
     */
    public static String getUniqueString(final int maxLength) {
        String finalString = null;
        var uniqueString = UUID.randomUUID().toString().replace("-", "");
        var uniqueLen = uniqueString.length();
        if (maxLength > 0) {
            var remainingLength = maxLength;
            //If the size of the desired string is larger than the UUID, just keep repeating it over and over until we
            // have the length we want
            while (remainingLength > 0) {
                var nextString = uniqueString;
                if (remainingLength < uniqueLen) {
                    nextString = uniqueString.substring(0, remainingLength);
                }
                finalString = (finalString == null) ? nextString : finalString + nextString;
                remainingLength -= uniqueLen;
            }
        } else {
            finalString = uniqueString;
        }
        return finalString;
    }

    /**
     * Performs a sleep operation on the current thread, in milliseconds.
     *
     * @param millis the amount of time to sleep in milliseconds.
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error(String.format("Thread.sleep() encountered an error: %s", e.getMessage()));
        }
    }

    public static String getUnderlyingLocatorByString(By by) {
        var locatorAsString = by.toString();
        var index = locatorAsString.indexOf(" ");
        return locatorAsString.substring(index + 1);
    }
}
