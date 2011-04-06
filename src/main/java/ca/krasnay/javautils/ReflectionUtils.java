package ca.krasnay.javautils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Collection of utilities to simplify working with Java reflection.
 *
 * @author John Krasnay <john@krasnay.ca>
 * @author Alex Rykov
 *
 */
public final class ReflectionUtils {

    private static Map<Class<?>, Class<?>> boxTypes = new HashMap<Class<?>, Class<?>>();
    private static Map<Class<?>, Set<Class<?>>> promotions = new HashMap<Class<?>, Set<Class<?>>>();

    static {

        boxTypes.put(boolean.class, Boolean.class);
        boxTypes.put(byte.class, Byte.class);
        boxTypes.put(char.class, Character.class);
        boxTypes.put(short.class, Short.class);
        boxTypes.put(int.class, Integer.class);
        boxTypes.put(long.class, Long.class);
        boxTypes.put(float.class, Float.class);
        boxTypes.put(double.class, Double.class);

        addPromotion(Byte.class, Short.class);
        addPromotion(Byte.class, Integer.class);
        addPromotion(Byte.class, Long.class);
        addPromotion(Byte.class, Float.class);
        addPromotion(Byte.class, Double.class);

        addPromotion(Character.class, Integer.class);
        addPromotion(Character.class, Long.class);
        addPromotion(Character.class, Float.class);
        addPromotion(Character.class, Double.class);

        addPromotion(Short.class, Integer.class);
        addPromotion(Short.class, Long.class);
        addPromotion(Short.class, Float.class);
        addPromotion(Short.class, Double.class);

        addPromotion(Integer.class, Long.class);
        addPromotion(Integer.class, Float.class);
        addPromotion(Integer.class, Double.class);

        addPromotion(Long.class, Float.class);
        addPromotion(Long.class, Double.class);

        addPromotion(Float.class, Double.class);

    }

    private static void addPromotion(Class<?> from, Class<?> to) {
        Set<Class<?>> set = promotions.get(from);
        if (set == null) {
            set = new HashSet<Class<?>>();
            promotions.put(from, set);
        }
        set.add(to);
    }

    /**
     * Asserts the given array of method arguments will work when invoking the
     * given method. If not, a RuntimeException will be thrown with a detailed
     * description of the problem. If you went ahead an invoked the method,
     * you'd also get an exception but with much less detail about the problem.
     *
     * @param m
     *            The method you want to invoke.
     * @param args
     *            The list of arguments you plan on sending to the method.
     */
    public static void assertMethodArgs(Method m, Object... args) {

        String message = null;

        Class<?>[] argTypes = m.getParameterTypes();

        if (argTypes.length != args.length) {
            message = String.format("expected %d args, received %d)",
                    argTypes.length, args.length);
        } else {
            for (int i = 0; i < argTypes.length; i++) {
                Class<?> clazz = argTypes[i];
                Object arg = args[i];
                if (arg == null) {
                    if (clazz.isPrimitive()) {
                        message = String.format("arg %d is a primitive, must not be null", i);
                        break;
                    }
                } else if (!isPromotableFrom(clazz, arg.getClass())) {
                    message = String.format("arg %d expected type %s, got type %s", i, clazz, arg.getClass());
                    break;
                }
            }
        }

        if (message != null) {
            throw new RuntimeException(
                    String.format("Error invoking %s with arguments (%s): %s",
                            m, StringUtils.join(", ", args), message));
        }
    }

    /**
     * Returns the field with the given name in the class hierarchy. If multiple
     * fields with the same name exist in hierarchy, the field in the class
     * closest to the clazz gets returned.
     *
     * @param clazz
     *            class to look for fields in the hierarchy
     * @param fieldName
     *            fieldName to find in the class
     */
    public static Field getDeclaredFieldInHierarchy(Class<?> clazz, String fieldName) {
        assert fieldName != null : "fieldName cannot be null";
        Field[] fields = getDeclaredFieldsInHierarchy(clazz);
        for (Field field : fields) {
            if (fieldName.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    /**
     * Returns an array of all declared fields in the given class and all
     * super-classes.
     */
    public static Field[] getDeclaredFieldsInHierarchy(Class<?> clazz) {

        if (clazz.isPrimitive()) {
            throw new IllegalArgumentException("Primitive types not supported.");
        }

        List<Field> result = new ArrayList<Field>();

        while (true) {

            if (clazz == Object.class) {
                break;
            }

            result.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }

        return result.toArray(new Field[result.size()]);
    }

    /**
     * Returns the value of a field in an object. Can traverse hierarchies by
     * separating field names with dots, e.g. "customer.address.postalCode".
     *
     * @param object
     *            Object at the top of the hierarchy from which to get the field.
     * @param fieldName
     *            Name of the field, including dots to traverse a hierarchy.
     * @param value
     *            Value to set into the final field in fieldName.
     */
    public static Object getDeepFieldValue(Object object, String fieldName) {

        while (true) {

            int delimPos = fieldName.indexOf('.');

            if (delimPos == -1) {
                return getFieldValue(object, fieldName);
            } else {
                String prefix = fieldName.substring(0, delimPos);
                object = getFieldValue(object, prefix);
                fieldName = fieldName.substring(delimPos + 1);
            }
        }
    }

    /**
     * Returns the value of a named field in the given object.
     *
     * @param object
     *            Object from which to retrieve the value.
     * @param deepFieldName
     *            Name of the field whose value to return.
     */
    public static Object getFieldValue(Object object, String fieldName) {
        try {

            Field field = getDeclaredFieldInHierarchy(object.getClass(), fieldName);

            if (field == null) {
                throw new RuntimeException(String.format("Class %s does not have field %s in its hierarchy.", object.getClass(), fieldName));
            }

            field.setAccessible(true);

            return field.get(object);

        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new RuntimeException(ex);
            }
        }

    }

    /**
     * Invokes a public method on an object given its name. This will invoke the
     * first public method with the given name without regards to the number or
     * type of the arguments. The method should therefore not be overloaded.
     *
     * @param o
     *            Object on which to invoke the method.
     * @param methodName
     *            Name of the method to invoke.
     * @param args
     *            Arguments to pass to the method.
     * @throws MethodNotFoundException
     *             if no matching exception was found.
     */
    public static Object invokeMethodByName(Object o, String methodName, Object... args) throws MethodNotFoundException {

        assert o != null;
        assert methodName != null;

        for (Method m : o.getClass().getMethods()) {
            if (methodName.equals(m.getName())) {
                try {
                    return m.invoke(o, args);
                } catch (IllegalArgumentException e) {
                    assertMethodArgs(m, args); // This throws an exception if
                                               // there's a problem with the
                                               // arguments
                    throw e; // In case there was some other problem
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        throw new MethodNotFoundException(String.format("Method %s not found in class %s", methodName, o.getClass()));
    }

    /**
     * Returns true if a variable of a given class can be assigned from a
     * variable of a given class. This is equivalent to the
     * {@link Class#isAssignableFrom(Class)} method, but converts primitive
     * types to their boxed versions, and will consider promotions, e.g. from
     * int to long.
     *
     * @param assigneeClass
     *            Class of the variable to which the value being assigned, for
     *            example the argument in a method call.
     * @param valueClass
     *            Class of value being assigned.
     */
    public static boolean isPromotableFrom(Class<?> assigneeClass, Class<?> valueClass) {

        assert assigneeClass != null;
        assert valueClass != null;

        if (assigneeClass.isPrimitive()) {
            Class<?> boxClass = boxTypes.get(assigneeClass);
            assert boxClass != null : "Can't find box type for " + assigneeClass.getName();
            assigneeClass = boxClass;
        }

        if (valueClass.isPrimitive()) {
            Class<?> boxClass = boxTypes.get(valueClass);
            assert boxClass != null : "Can't find box type for " + valueClass.getName();
            valueClass = boxClass;
        }

        assert assigneeClass != null;
        assert valueClass != null;

        if (assigneeClass.isAssignableFrom(valueClass)) {
            return true;
        } else if (promotions.get(valueClass) != null && promotions.get(valueClass).contains(assigneeClass)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Sets the value of a field in an object. Can traverse hierarchies by
     * separating field names with dots, e.g. "customer.address.postalCode".
     *
     * @param object
     *            Object at the top of the hierarchy in which to set the field.
     * @param fieldName
     *            Name of the field, including dots to traverse a hierarchy.
     * @param value
     *            Value to set into the final field in fieldName.
     */
    public static void setDeepFieldValue(Object object, String fieldName, Object value) {

        while (true) {

            int delimPos = fieldName.indexOf('.');

            if (delimPos == -1) {
                setFieldValue(object, fieldName, value);
                break;
            } else {
                String prefix = fieldName.substring(0, delimPos);
                object = getFieldValue(object, prefix);
                fieldName = fieldName.substring(delimPos + 1);
            }
        }
    }

    /**
     * Sets the value of a field in an object.
     *
     * @param object
     *            Object in which to set the field.
     * @param fieldName
     *            Name of the field.
     * @param value
     *            Value to which to set the field.
     */
    public static void setFieldValue(Object object, String fieldName, Object value) {
        try {
            Field field = getDeclaredFieldInHierarchy(object.getClass(), fieldName);
            if (field == null) {
                throw new RuntimeException(String.format("Class %s does not have field %s in its hierarchy.", object.getClass(), fieldName));
            }
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new RuntimeException(ex);
            }
        }

    }

    /**
     * Prevent instantiation.
     */
    private ReflectionUtils() {
    }

}
