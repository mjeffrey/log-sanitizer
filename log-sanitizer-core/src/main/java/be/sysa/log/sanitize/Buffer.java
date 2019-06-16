package be.sysa.log.sanitize;

import lombok.Getter;

import java.util.regex.Matcher;

import static java.lang.System.arraycopy;

/**
 * Responsible for holding the log text as it is being sanitized. Implements
 * CharSequence so that it can be used easily. It is mutable and not threadsafe.
 */
@Getter
public class Buffer implements CharSequence {
    private char[] transformed;
    private String original;

    /**
     * Create a new Buffer from the original String. The original is kept.
     * @param original the original String before sanitization.
     */
    public Buffer(String original) {
        transformed = original.toCharArray();
        this.original = original;
    }

    @Override
    public int length() {
        return transformed.length;
    }

    @Override
    public char charAt(int index) {
        return transformed[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return String.copyValueOf(transformed, start, end - start);
    }

    public String toString() {
        return new String(transformed);
    }

    /**
     * Mask the non-whitespace character at this position by overwriting it with a '*' character.
     * @param pos The zero-based position in the buffer.
     */
    public void mask(int pos) {
        if (!Character.isWhitespace(transformed[pos])) {
            transformed[pos] = MessageSanitizer.MASK_CHARACTER;
        }
    }

    /**
     * Mask length characters from the start position. If any characters are whitespace they are not masked.
     * @param start zero based start position.
     * @param length Number of charactes to mask.
     *
     */
    public void mask(int start, int length) {
        for (int pos = start; pos < start + length; pos++) {
            mask(pos);
        }
    }

    /**
     * Intended to mask characters in the middle of a string while keeping some ath the start and some at the end.
     * This is how credit card maksing typically works.
     * @param matcher The regex matcher that has matched the string to mask.
     * @param keepCharsStart The number of unmasked characters at the start of the string found by the matcher.
     * @param keepCharsEnd The number of unmasked characters at the endo of the string found by the matcher.
     */
    public void maskCharactersBetween(Matcher matcher, int keepCharsStart, int keepCharsEnd) {
        int pos1 = matcher.start();
        int pos2 = matcher.end() - 1;
        int skipStart = keepCharsStart;
        int skipEnd = keepCharsEnd;

        while (pos2 > pos1) {

            if (!Character.isWhitespace(transformed[pos1])) {
                if (skipStart <= 0) {
                    mask(pos1);
                }
                skipStart--;
            }
            if (!Character.isWhitespace(transformed[pos2])) {
                if (skipEnd <= 0) {
                    mask(pos2);
                }
                skipEnd--;
            }
            pos1++;
            pos2--;
        }
    }

    /**
     * Sometimes a sanitizer produces a string that is bigger or smaller than the original: Json Parsing can often give this.
     * This method writes the new string at the coorect place in the buffer and adjusts the characters.
     * @param matchedGroup The group that this sanitizer matched. JSON for example is matched starting with { and
     *                     ending with }
     * @param newSanitizedString The string that resulted from the regex match that has been sanitized and needs to be
     *                           injected back into teh buffer.
     */
    public void replaceAt(Matcher matchedGroup, String newSanitizedString) {
        int groupLength = matchedGroup.end() - matchedGroup.start();
        int lengthChange = newSanitizedString.length() - groupLength;

        if (lengthChange == 0) {
            arraycopy(newSanitizedString.toCharArray(), 0, transformed, matchedGroup.start(), groupLength);
        } else if (lengthChange > 0) {
            injectNewString(matchedGroup, newSanitizedString, lengthChange);
        } else {
            injectNewString(matchedGroup, newSanitizedString, lengthChange);
        }
    }

    /**
     * Check if a string is all characters.
     * @param matcher
     * @return
     */
    public boolean isAllChars(Matcher matcher) {
        int pos1 = matcher.start();
        int pos2 = matcher.end();
        int upperCase = 0;
        int lowerCase = 0;
        for (int i = pos1; i < pos2; i++) {
            if (!Character.isAlphabetic(transformed[i])) {
                return false;
            } else {
                if (Character.isUpperCase(transformed[i])) {
                    upperCase++;
                } else {
                    lowerCase++;
                }
            }
        }
        return upperCase * 2 < lowerCase || lowerCase < 2;
    }

    private void injectNewString(Matcher matchedGroup, String newJson, int lengthChange) {
        char[] newBuf = new char[transformed.length + lengthChange];
        arraycopy(transformed, 0, newBuf, 0, matchedGroup.start());
        arraycopy(newJson.toCharArray(), 0, newBuf, matchedGroup.start(), newJson.length());
        arraycopy(transformed, matchedGroup.end(), newBuf, matchedGroup.start() + newJson.length(), transformed.length - matchedGroup.end());
        transformed = newBuf;
    }

}
