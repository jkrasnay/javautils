package ca.krasnay.javautils;

import java.util.Arrays;
import java.util.List;

import ca.krasnay.javautils.StringUtils;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {

    private static void assertJoin(String expected, String separator, Object... items) {
        assertEquals(expected, StringUtils.join(separator, items));
        assertEquals(expected, StringUtils.join(separator, Arrays.asList(items)));
    }

    public void testJoin() {

        List<String> nullList = null;
        Object[] nullArray = null;

        try {
            StringUtils.join(null);
            fail("Expected exception");
        } catch (AssertionError e) {
        }

        try {
            StringUtils.join(", ", nullList);
            fail("Expected exception");
        } catch (AssertionError e) {
        }

        try {
            StringUtils.join(", ", nullArray);
            fail("Expected exception");
        } catch (AssertionError e) {
        }

        assertEquals("", StringUtils.join(", "));
        assertJoin("", ", ");
        assertJoin("a", ", ", "a");
        assertJoin("a, b", ", ", "a", "b");
        assertJoin("a, b, c", ", ", "a", "b", "c");
        assertJoin("abc|123", "|", "abc", 123);

    }
}
