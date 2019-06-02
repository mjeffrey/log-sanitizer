package be.sysa.log.sanitize;

import lombok.Getter;

import java.util.regex.Matcher;

import static java.lang.System.arraycopy;

@Getter
public class Buffer implements CharSequence {
    private char[] transformed;
    private String original;

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

    public void mask(int pos) {
        if (!Character.isWhitespace(transformed[pos])) {
            transformed[pos] = MessageSanitizer.MASK_CHARACTER;
        }
    }

    public void mask(int start, int length) {
        for (int pos = start; pos < start + length; pos++) {
            mask(pos);
        }
    }

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

    public void replaceAt(Matcher matchedGroup, String newJson) {
        int groupLength = matchedGroup.end() - matchedGroup.start();

        int lengthChange = newJson.length() - groupLength;

        if (lengthChange == 0) {
            arraycopy(newJson.toCharArray(), 0, transformed, matchedGroup.start(), groupLength);
        } else if (lengthChange > 0) {
            injectNewJson(matchedGroup, newJson, lengthChange);
        } else {
            injectNewJson(matchedGroup, newJson, lengthChange);
        }
    }

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

    private void injectNewJson(Matcher matchedGroup, String newJson, int lengthChange) {
        char[] newBuf = new char[transformed.length + lengthChange];
        arraycopy(transformed, 0, newBuf, 0, matchedGroup.start());
        arraycopy(newJson.toCharArray(), 0, newBuf, matchedGroup.start(), newJson.length());
        arraycopy(transformed, matchedGroup.end(), newBuf, matchedGroup.start() + newJson.length(), transformed.length - matchedGroup.end());
        transformed = newBuf;
    }

}
