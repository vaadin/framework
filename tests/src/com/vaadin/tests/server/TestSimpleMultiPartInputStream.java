package com.vaadin.tests.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import com.vaadin.terminal.gwt.server.AbstractCommunicationManager.SimpleMultiPartInputStream;

public class TestSimpleMultiPartInputStream extends TestCase {

    /**
     * Check that the output for a given stream until boundary is as expected.
     * 
     * @param input
     * @param boundary
     * @param expected
     * @throws Exception
     */
    protected void checkBoundaryDetection(byte[] input, String boundary,
            byte[] expected) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(input);
        SimpleMultiPartInputStream smpis = new SimpleMultiPartInputStream(bais,
                boundary);
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        int outbyte;
        try {
            while ((outbyte = smpis.read()) != -1) {
                resultStream.write(outbyte);
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage() + "; expected "
                    + new String(expected) + " but got "
                    + resultStream.toString(), e);
        }
        if (!Arrays.equals(expected, resultStream.toByteArray())) {
            throw new Exception("Mismatch: expected " + new String(expected)
                    + " but got " + resultStream.toString());
        }
    }

    protected void checkBoundaryDetection(String input, String boundary,
            String expected) throws Exception {
        checkBoundaryDetection(input.getBytes(), boundary, expected.getBytes());
    }

    public void testSingleByteBoundaryAtEnd() throws Exception {
        checkBoundaryDetection("xyz123a", "a", "xyz123");
    }

    public void testSingleByteBoundaryInMiddle() throws Exception {
        checkBoundaryDetection("xyza123", "a", "xyz");
    }

    public void testCorrectBoundaryAtEnd() throws Exception {
        checkBoundaryDetection("xyz123abc", "abc", "xyz123");
    }

    public void testCorrectBoundaryNearEnd() throws Exception {
        checkBoundaryDetection("xyz123abcde", "abc", "xyz123");
    }

    public void testCorrectBoundaryAtBeginning() throws Exception {
        checkBoundaryDetection("abcxyz123", "abc", "");
    }

    public void testRepeatingCharacterBoundary() throws Exception {
        checkBoundaryDetection("aaxyz123", "aa", "");
        checkBoundaryDetection("axyzaa123", "aa", "axyz");
        checkBoundaryDetection("xyz123aa", "aa", "xyz123");
    }

    public void testRepeatingStringBoundary() throws Exception {
        checkBoundaryDetection("ababxyz123", "abab", "");
        checkBoundaryDetection("abaxyzabab123", "abab", "abaxyz");
        checkBoundaryDetection("xyz123abab", "abab", "xyz123");
    }

    public void testOverlappingBoundary() throws Exception {
        checkBoundaryDetection("abcabcabdxyz123", "abcabd", "abc");
        checkBoundaryDetection("xyzabcabcabd123", "abcabd", "xyzabc");
        checkBoundaryDetection("xyz123abcabcabd", "abcabd", "xyz123");
    }

    public void testNoBoundaryInInput() throws Exception {
        try {
            checkBoundaryDetection("xyz123", "abc", "xyz123");
            fail();
        } catch (IOException e) {
        }
    }

    public void testPartialBoundaryAtInputEnd() throws Exception {
        try {
            // This should lead to IOException (stream end), not AIOOBE
            checkBoundaryDetection("xyz123ab", "abc", "xyz123ab");
            fail();
        } catch (IOException e) {
        }
    }

    public void testPartialBoundaryAtInputBeginning() throws Exception {
        try {
            checkBoundaryDetection("abxyz123", "abc", "abxyz123");
            fail();
        } catch (IOException e) {
        }
    }

}
