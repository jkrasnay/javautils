package ca.krasnay.javautils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import ca.krasnay.javautils.ReflectionUtils;

import junit.framework.TestCase;


@SuppressWarnings("unused")
public class ReflectionUtilsTest extends TestCase {

    private static class Bar extends Foo {

        private String bar;
    }

    private static class Baz {

    }

    private static class Child extends Parent{
        private String field1;
        private String field2;
    }

    public static class FirstLevelClass{

        private SecondLevelClass secondLevelSimpleField;
    }

    private static class Foo {

        private String foo;
    }

    private static class Parent{
        private String field1;
    }

    public static class SecondLevelClass{
        private ThirdLevelClass thirdLevelSimpleField;

    }

    public static class ThirdLevelClass{
        private int value;
    }

    public void fByte(byte x) {
    }

    public void fChar(char x) {
    }

    public void fDouble(double x) {
    }

    public void fFloat(float x) {
    }

    public void fInt(int x) {
    }

    public void fLong(long x) {
    }

    public void fShort(short x) {
    }

    public void myMethod(String s, int i) {
    }


    private void privateMethod(String s, int i) {
    }

    public void testAssertMethodArgs() throws Exception {

        Method m = ReflectionUtilsTest.class.getDeclaredMethod("myMethod", String.class, int.class);

        // Happy path
        ReflectionUtils.assertMethodArgs(m, "foo", 0);
        ReflectionUtils.assertMethodArgs(m, "null", 0);

        // Wrong arg count
        try {
            ReflectionUtils.assertMethodArgs(m);
            fail("Expected exception");
        } catch (Exception e) {
            assertTrue("Actual message: " + e.getMessage(),
                    e.getMessage().contains("expected 2 args, received 0"));
        }

        // Class mismatch
        try {
            ReflectionUtils.assertMethodArgs(m, 0, 0);
            fail("Expected exception");
        } catch (Exception e) {
            assertTrue("Actual message: " + e.getMessage(),
                    e.getMessage().contains("arg 0 expected type class java.lang.String, got type class java.lang.Integer"));
        }

        // Null primitive
        try {
            ReflectionUtils.assertMethodArgs(m, "foo", null);
        } catch (Exception e) {
            assertTrue("Actual message: " + e.getMessage(),
                    e.getMessage().contains("arg 1 is a primitive, must not be null"));
        }

    }

    public void testAssignability() {

        byte byteVal = 0;
        char charVal = 0;
        short shortVal = 0;
        int intVal = 0;
        long longVal = 0;
        float floatVal = 0;
        double doubleVal = 0;

        String[] methodNames = { "fByte", "fChar", "fShort", "fInt", "fLong", "fFloat", "fDouble" };
        Object[] values = { byteVal, charVal, shortVal, intVal, longVal, floatVal, doubleVal };

        for (Object value : values) {

            for (String methodName : methodNames) {

                Method m = null;

                for (Method m2 : ReflectionUtilsTest.class.getMethods()) {
                    if (m2.getName().equals(methodName)) {
                        m = m2;
                        break;
                    }
                }

                assert m != null : "Can't find method " + methodName;

                boolean b;
                try {
                    m.invoke(this, value);
                    b = true;
                } catch (IllegalArgumentException e) {
                    b = false;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

                assertEquals(b, ReflectionUtils.isPromotableFrom(m.getParameterTypes()[0], value.getClass()));
            }

        }
    }
    public void testFieldAccess(){
        FirstLevelClass flc = new FirstLevelClass();
        SecondLevelClass slc = new SecondLevelClass();
        ThirdLevelClass tlc = new ThirdLevelClass();
        int value=12;
        ReflectionUtils.setFieldValue(flc, "secondLevelSimpleField", slc);
        ReflectionUtils.setDeepFieldValue(flc, "secondLevelSimpleField.thirdLevelSimpleField", tlc);
        ReflectionUtils.setDeepFieldValue(flc, "secondLevelSimpleField.thirdLevelSimpleField.value", value);
        assertSame(slc, ReflectionUtils.getFieldValue(flc, "secondLevelSimpleField"));
        assertSame(tlc, ReflectionUtils.getDeepFieldValue(flc, "secondLevelSimpleField.thirdLevelSimpleField"));
        assertEquals(value, ReflectionUtils.getDeepFieldValue(flc, "secondLevelSimpleField.thirdLevelSimpleField.value"));

//        BigDecimal bd = new BigDecimal(value+1);
//        ReflectionUtils.setDeepFieldValue(flc, "secondLevelSimpleField.thirdLevelSimpleField.value", bd);
//        assertEquals(value+1, ReflectionUtils.getDeepFieldValue(flc, "secondLevelSimpleField.thirdLevelSimpleField.value"));
    }

    public void testFieldAccessIllegalFieldNames(){

        FirstLevelClass flc = new FirstLevelClass();
        SecondLevelClass slc = new SecondLevelClass();
        ThirdLevelClass tlc = new ThirdLevelClass();
        int value=12;
        ReflectionUtils.setFieldValue(flc, "secondLevelSimpleField", slc);
        ReflectionUtils.setDeepFieldValue(flc, "secondLevelSimpleField.thirdLevelSimpleField", tlc);
        ReflectionUtils.setDeepFieldValue(flc, "secondLevelSimpleField.thirdLevelSimpleField.value", value);

        try {
            ReflectionUtils.setFieldValue(flc, "secondLevelSimpleField.", slc);
            assertFalse("Exception expected", true);
        } catch (RuntimeException rex) {
        }

        try {
            ReflectionUtils.getFieldValue(flc, "secondLevelSimpleField.");
            assertFalse("Exception expected", true);
        } catch (RuntimeException rex) {
        }

        try {
            ReflectionUtils.setFieldValue(flc, "secondLevelSimpleField..thirdLevelSimpleField.value", value);
            assertFalse("Exception expected", true);
        } catch (RuntimeException rex) {
        }

        try {
            ReflectionUtils.getFieldValue(flc, "secondLevelSimpleField..");
            assertFalse("Exception expected", true);
        } catch (RuntimeException rex) {
        }

        try {
            ReflectionUtils.setFieldValue(flc, ".", slc);
            assertFalse("Exception expected", true);
        } catch (RuntimeException rex) {
        }

        //non existent field
        try {
            ReflectionUtils.setFieldValue(flc, "does not exist", slc);
            assertFalse("Exception expected", true);
        } catch (RuntimeException rex) {
        }

        try {
            ReflectionUtils.getFieldValue(flc, "does not exist");
            assertFalse("Exception expected", true);
        } catch (RuntimeException rex) {
        }


        try {
            ReflectionUtils.setFieldValue(new FirstLevelClass(), "secondLevelSimpleField.thirdLevelSimpleField", new ThirdLevelClass());
            assertFalse("Exception expected", true);
        } catch (RuntimeException rex) {
        }


    }

    public void testGetDeclaredFieldInHierarchy() {

        Field field = ReflectionUtils.getDeclaredFieldInHierarchy(Child.class, "field1");

        assertEquals(Child.class, field.getDeclaringClass());
    }

    public void testGetDeclaredFieldsInHierarchy() {

        Field[] fields = ReflectionUtils.getDeclaredFieldsInHierarchy(Baz.class);

        assertEquals(0, fields.length);

        fields = ReflectionUtils.getDeclaredFieldsInHierarchy(Foo.class);

        assertEquals(1, fields.length);
        assertEquals("foo", fields[0].getName());

        fields = ReflectionUtils.getDeclaredFieldsInHierarchy(Bar.class);

        assertEquals(2, fields.length);
        assertEquals("bar", fields[0].getName());
        assertEquals("foo", fields[1].getName());

    }

    public void testInvokeByName() throws Exception {

        // Happy path
        ReflectionUtils.invokeMethodByName(this, "myMethod", "foo", 42);

        // Bad args
        try {
            ReflectionUtils.invokeMethodByName(this, "myMethod", "foo");
            fail("Expected exception");
        } catch (Exception e) {
            assertTrue("Actual message: " + e.getMessage(),
                    e.getMessage().contains("expected 2 args, received 1"));
        }

        // Method not found
        try {
            ReflectionUtils.invokeMethodByName(this, "myPoorBrain", "foo", 42);
            fail("Expected exception");
        } catch (Exception e) {
            assertTrue("Actual message: " + e.getMessage(),
                    e.getMessage().contains("Method myPoorBrain not found in class class ca.krasnay.javautils.ReflectionUtilsTest"));
        }

        // Private Method
        try {
            ReflectionUtils.invokeMethodByName(this, "privateMethod", "foo", 42);
            fail("Expected exception");
        } catch (Exception e) {
            assertTrue("Actual message: " + e.getMessage(),
                    e.getMessage().contains("Method privateMethod not found in class class ca.krasnay.javautils.ReflectionUtilsTest"));
        }

    }
    public void testIsPromotableFrom() {

        assertTrue(ReflectionUtils.isPromotableFrom(Object.class, Object.class));
        assertTrue(ReflectionUtils.isPromotableFrom(Foo.class, Bar.class));
        assertFalse(ReflectionUtils.isPromotableFrom(Bar.class, Foo.class));

        assertTrue(ReflectionUtils.isPromotableFrom(boolean.class, Boolean.class));
        assertTrue(ReflectionUtils.isPromotableFrom(Boolean.class, boolean.class));

        assertTrue(ReflectionUtils.isPromotableFrom(short.class, Short.class));
        assertTrue(ReflectionUtils.isPromotableFrom(Short.class, short.class));

        assertTrue(ReflectionUtils.isPromotableFrom(char.class, Character.class));
        assertTrue(ReflectionUtils.isPromotableFrom(Character.class, char.class));

        assertTrue(ReflectionUtils.isPromotableFrom(int.class, Integer.class));
        assertTrue(ReflectionUtils.isPromotableFrom(Integer.class, int.class));

        assertTrue(ReflectionUtils.isPromotableFrom(long.class, Long.class));
        assertTrue(ReflectionUtils.isPromotableFrom(Long.class, long.class));

        assertTrue(ReflectionUtils.isPromotableFrom(float.class, Float.class));
        assertTrue(ReflectionUtils.isPromotableFrom(Float.class, float.class));

        assertTrue(ReflectionUtils.isPromotableFrom(double.class, Double.class));
        assertTrue(ReflectionUtils.isPromotableFrom(Double.class, double.class));

    }
}
