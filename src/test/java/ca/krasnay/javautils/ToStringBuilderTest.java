package ca.krasnay.javautils;

import java.util.Arrays;

import ca.krasnay.javautils.ToStringBuilder;

import junit.framework.TestCase;

public class ToStringBuilderTest extends TestCase {

    public void testAll() {

        String theObject = "Hello";

        String prefix = "java.lang.String@" + theObject.hashCode();

        assertEquals(prefix + "[]", new ToStringBuilder(theObject).toString());
        assertEquals(prefix + "[foo=bar]", new ToStringBuilder(theObject).append("foo", "bar").toString());
        assertEquals(prefix + "[foo=bar, baz=quux]", new ToStringBuilder(theObject).append("foo", "bar").append("baz", "quux").toString());
        assertEquals(prefix + "[a=[1, 2, 3]]", new ToStringBuilder(theObject).append("a", new Integer[] { 1, 2, 3 }).toString());
        assertEquals(prefix + "[a=[1, 2, 3]]", new ToStringBuilder(theObject).append("a", Arrays.asList(1, 2, 3)).toString());
    }
}
