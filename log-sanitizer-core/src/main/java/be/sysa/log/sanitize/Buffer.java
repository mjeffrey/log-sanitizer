package be.sysa.log.sanitize;

import lombok.Getter;

import java.util.BitSet;

import static java.lang.System.arraycopy;

/**
 * Responsible for holding the log text as it is being sanitized. Implements
 * CharSequence so that it can be used easily. It is mutable and not threadsafe.
 */
public class Buffer implements CharSequence {
    @Getter
    private final String original;
    private char[] transformed;
    private final BitSet protectedRegions;

    /**
     * Create a new Buffer from the original String. The original is kept.
     *
     * @param original the original String before sanitization.
     */
    public Buffer(String original) {
        transformed = original.toCharArray();
        this.original = original;
        this.protectedRegions = new BitSet(original.length());
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
     *
     * @param pos The zero-based position in the buffer.
     */
    public void mask(int pos) {
        if (!Character.isWhitespace(transformed[pos])) {
            transformed[pos] = MessageSanitizer.MASK_CHARACTER;
        }
    }

    /**
     * Mask length characters from the start position. If any characters are whitespace they are not masked.
     * If any characters are in protected regions (protected by a higher priority sanitizer) then they are not masked
     *
     * @param start  zero based start position.
     * @param length Number of characters to mask.
     */
    public void mask(int start, int length) {
        for (int pos = start; pos < start + length; pos++) {
            if (!protectedRegions.get(pos)) {
                mask(pos);
            }
        }
    }

    /**
     * Mask length characters from the start position. If any characters are whitespace they are not masked.
     * If any characters are in protected regions (protected by a higher priority sanitizer) then they are not masked
     *
     * @param bounds The region to mask;
     */
    public void maskString(String value, Bounds bounds) {
        int maxWrite = getMaximumWritablePosition(bounds.start(), bounds.end());

        int pos = bounds.start();
        for (int i=0; i < value.length() && pos <= maxWrite; pos++, i++) {
            transformed[pos] = value.charAt(i);
        }
        for (; pos <= maxWrite; pos++) {
            transformed[pos] = ' ';
        }


    }

    private int getMaximumWritablePosition(int start, int end) {
        final int protectedStartRegion = protectedRegions.nextSetBit(start);
        return protectedStartRegion < 0 ? end: protectedStartRegion;
    }

    /**
     * Protect these characters from other sanitizers.
     *
     * @param bounds The start and the end region to mask (matched region).
     */
    public void protect(Bounds bounds) {
        int pos1 = bounds.start();
        int pos2 = bounds.end() - 1;
        for (int pos = pos1; pos < pos1 + pos2; pos++) {
            protectedRegions.set(pos);
        }
    }

    /**
     * Intended to mask characters in the middle of a string while keeping some ath the start and some at the end.
     * This is how credit card masking typically works.
     *
     * @param bounds        The regex bounds that has matched the string to mask.
     * @param keepCharsStart The number of unmasked characters at the start of the string found by the bounds.
     * @param keepCharsEnd   The number of unmasked characters at the end of the string found by the bounds.
     */
    public void maskCharactersBetween(Bounds bounds, int keepCharsStart, int keepCharsEnd) {
        int pos1 = bounds.start() +keepCharsStart;
        int pos2 = bounds.end() - keepCharsEnd;

        for (int i = pos1; i < pos2; i++) {
            if (!Character.isWhitespace(transformed[i])) {
                mask(i);
            }
        }
    }

    /**
     * Sometimes a sanitizer produces a string that is bigger or smaller than the original: Json Parsing can often give this.
     * This method writes the new string at the correct place in the buffer and adjusts the characters.
     *
     * @param bounds          The group that this sanitizer matched. JSON for example is matched starting with { and
     *                           ending with }
     * @param newSanitizedString The string that resulted from the regex match that has been sanitized and needs to be
     *                           injected back into the buffer.
     */
    public void replaceAt(Bounds bounds, String newSanitizedString) {
        int groupLength = bounds.end() - bounds.start();
        int lengthChange = newSanitizedString.length() - groupLength;

        if (lengthChange == 0) {
            arraycopy(newSanitizedString.toCharArray(), 0, transformed, bounds.start(), groupLength);
        } else if (lengthChange > 0) {
            injectNewString(bounds, newSanitizedString, lengthChange);
        } else {
            injectNewString(bounds, newSanitizedString, lengthChange);
        }
    }

    private void injectNewString(Bounds bounds, String newString, int lengthChange) {
        char[] newBuf = new char[transformed.length + lengthChange];
        arraycopy(transformed, 0, newBuf, 0, bounds.start());
        arraycopy(newString.toCharArray(), 0, newBuf, bounds.start(), newString.length());
        arraycopy(transformed, bounds.end(), newBuf, bounds.start() + newString.length(), transformed.length - bounds.end());
        transformed = newBuf;
    }

    /**
     * Check if a string is all characters.
     *
     * @param bounds The bounds of the matcher that found the chars
     * @return true if all are alpha characters
     */
    public boolean isAllChars(Bounds bounds) { //TODO review naming
        int pos1 = bounds.start();
        int pos2 = bounds.end();
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

}
