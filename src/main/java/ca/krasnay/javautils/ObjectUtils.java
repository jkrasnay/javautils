package ca.krasnay.javautils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Utility methods applicable to Objects.
 *
 * @author John Krasnay <john@krasnay.ca>
 */
public final class ObjectUtils {

    /**
     * Clones a Serializable object via serialization. The class being cloned
     * need not implement the Cloneable interface.
     *
     * Please not that this is a *very* slow way to clone objects. If you need
     * to clone things in your production code you should write your own clone
     * method instead.
     *
     * This method was written to support unit tests, were we want to clone
     * objects to detect changes during a test.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T cloneSerializable(T object) {

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new ObjectOutputStream(baos).writeObject(object);
            return (T) new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Null-safe and array-enabled equals method. Returns true if both objects
     * are null. If both objects are arrays of the same type, compares using one
     * of the equals methods from the Arrays class, using deepEquals if they are
     * Object arrays. Otherwise, compares them using the first object's equals
     * method.
     */
    public static boolean equals(Object o1, Object o2) {

        if (o1 == null) {
            return o2 == null;
        } else if (o2 == null) {
            return false;
        } else if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        } else if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        } else if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        } else if (o1 instanceof short[] && o2 instanceof short[]) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        } else if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        } else if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        } else if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        } else if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        } else if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.deepEquals((Object[]) o1, (Object[]) o2);
        } else {
            return o1.equals(o2);
        }

    }

    /**
     * Method to help with array conversions. Say Object[] to Serializable[] if
     * all members of the first array are Serializable or vice versa
     *
     * @param <T>
     * @param array
     *            array to be converted
     * @param asClazz
     *            type of array to be returned
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T, F> T[] repackageArray(F[] array, Class<T> asClazz) {
        T[] objectArrayPayload = (T[]) Array.newInstance(asClazz, array.length);
        for (int i = 0; i < array.length; i++) {
            objectArrayPayload[i] = (T) array[i];
        }
        return objectArrayPayload;
    }

    private ObjectUtils() {
    }
}
