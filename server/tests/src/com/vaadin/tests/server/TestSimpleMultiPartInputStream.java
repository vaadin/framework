package com.vaadin.tests.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import com.vaadin.server.communication.FileUploadHandler.SimpleMultiPartInputStream;

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
                    + resultStream.toString());
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
        checkBoundaryDetection("xyz123" + getFullBoundary("a"), "a", "xyz123");
    }

    public void testSingleByteBoundaryInMiddle() throws Exception {
        checkBoundaryDetection("xyz" + getFullBoundary("a") + "123", "a", "xyz");
    }

    public void testCorrectBoundaryAtEnd() throws Exception {
        checkBoundaryDetection("xyz123" + getFullBoundary("abc"), "abc",
                "xyz123");
    }

    public void testCorrectBoundaryNearEnd() throws Exception {
        checkBoundaryDetection("xyz123" + getFullBoundary("abc") + "de", "abc",
                "xyz123");
    }

    public void testCorrectBoundaryAtBeginning() throws Exception {
        checkBoundaryDetection(getFullBoundary("abc") + "xyz123", "abc", "");
    }

    public void testRepeatingCharacterBoundary() throws Exception {
        checkBoundaryDetection(getFullBoundary("aa") + "xyz123", "aa", "");
        checkBoundaryDetection("axyz" + getFullBoundary("aa") + "123", "aa",
                "axyz");
        checkBoundaryDetection("xyz123" + getFullBoundary("aa"), "aa", "xyz123");
    }

    /**
     * Note, the boundary in this test is invalid. Boundary strings don't
     * contain CR/LF.
     * 
     */
    // public void testRepeatingNewlineBoundary() throws Exception {
    // checkBoundaryDetection("1234567890" + getFullBoundary("\n\n")
    // + "1234567890", "\n\n", "");
    // }

    public void testRepeatingStringBoundary() throws Exception {
        checkBoundaryDetection(getFullBoundary("abab") + "xyz123", "abab", "");
        checkBoundaryDetection("abaxyz" + getFullBoundary("abab") + "123",
                "abab", "abaxyz");
        checkBoundaryDetection("xyz123" + getFullBoundary("abab"), "abab",
                "xyz123");
    }

    public void testOverlappingBoundary() throws Exception {
        checkBoundaryDetection("abc" + getFullBoundary("abcabd") + "xyz123",
                "abcabd", "abc");
        checkBoundaryDetection("xyzabc" + getFullBoundary("abcabd") + "123",
                "abcabd", "xyzabc");
        checkBoundaryDetection("xyz123abc" + getFullBoundary("abcabd"),
                "abcabd", "xyz123abc");
    }

    /*
     * TODO fix these tests, they don't do what their method name says.
     */

    // public void testNoBoundaryInInput() throws Exception {
    // try {
    // checkBoundaryDetection("xyz123", "abc", "xyz123");
    // fail();
    // } catch (IOException e) {
    // }
    // }
    //
    // public void testPartialBoundaryAtInputEnd() throws Exception {
    // try {
    // // This should lead to IOException (stream end), not AIOOBE
    // checkBoundaryDetection("xyz123ab", "abc", "xyz123ab");
    // fail();
    // } catch (IOException e) {
    // }
    // }
    //
    // public void testPartialBoundaryAtInputBeginning() throws Exception {
    // try {
    // checkBoundaryDetection("abxyz123", "abc", "abxyz123");
    // fail();
    // } catch (IOException e) {
    // }
    // }

    public static String getFullBoundary(String str) {
        return "\r\n--" + str + "--";
    }

}
