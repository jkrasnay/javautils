package ca.krasnay.javautils;

import ca.krasnay.javautils.ObjectUtils;
import junit.framework.TestCase;

public class ObjectUtilsTest extends TestCase {

    public void testCloneSerializable() {
        assertNull(ObjectUtils.cloneSerializable(null));

        String p = "foo";
        String q = ObjectUtils.cloneSerializable(p);
        assertEquals(p, q);
        assertNotSame(p, q);
    }

    public void testEquals() {

        assertTrue(ObjectUtils.equals(null, null));
        assertFalse(ObjectUtils.equals("foo", null));
        assertFalse(ObjectUtils.equals(null, "foo"));
        assertFalse(ObjectUtils.equals("foo", "bar"));
        assertTrue(ObjectUtils.equals("foo", "foo"));

        int[] ia1 = new int[] { 1, 2, 3 };
        int[] ia2 = new int[] { 1, 2, 3 };
        int[] ia3 = new int[] { 1, 2 };
        long[] la1 = new long[] { 1, 2, 3 };

        Object[] oa1 = new Object[] { ia1 };
        Object[] oa2 = new Object[] { ia2 };
        Object[] oa3 = new Object[] { ia3 };

        assertTrue(ObjectUtils.equals(ia1, ia2));
        assertFalse(ObjectUtils.equals(ia1, ia3));
        assertFalse(ObjectUtils.equals(ia1, la1));

        assertTrue(ObjectUtils.equals(oa1, oa2));
        assertFalse(ObjectUtils.equals(oa1, oa3));

    }
}
