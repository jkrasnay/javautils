package ca.krasnay.javautils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder for creating the strings returned by an Object's toString method.
 * Similar to the similarly-named class in Apache commons lang.
 *
 * @author John Krasnay <john@krasnay.ca>
 */
public class ToStringBuilder {

    private Object object;

    private List<String> keys = new ArrayList<String>();

    private List<Object> values = new ArrayList<Object>();

    public ToStringBuilder(Object object) {
        this.object = object;
    }

    public ToStringBuilder append(String key, Object value) {
        keys.add(key);
        values.add(value);
        return this;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(object.getClass().getName())
        .append("@")
        .append(object.hashCode())
        .append("[");

        boolean first = true;
        for (int i = 0; i < keys.size(); i++) {

            String key = keys.get(i);
            Object value = values.get(i);

            if (!first) {
                sb.append(", ");
            }

            sb.append(key).append("=");

            if (value instanceof Object[]) {
                sb.append(Arrays.toString((Object[]) value));
            } else {
                sb.append(value);
            }

            first = false;
        }

        sb.append("]");
        return sb.toString();
    }

}
