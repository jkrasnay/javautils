package ca.krasnay.javautils;

import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Utility methods for dealing with IO.
 *
 * None of these methods throw checked exceptions. Any IOException caught during
 * their operation is wrapped in a RuntimeException and re-thrown.
 *
 * @author John Krasnay <john@krasnay.ca>
 */
public class IOUtils {

    private static final String UTF_8 = "UTF-8";

    public static final int BYTE_BUFFER_SIZE = 4096;

    public static final int CHAR_BUFFER_SIZE = 4096;

    /**
     * Quietly loses the given closeable. Ignores null if passed, and catches
     * and ignores any IOException thrown by the close method.
     *
     * @param c
     *            Closeable (e.g. InputStream, Reader) to be closed. May be null.
     */
    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
            }
        }
    }


    /**
     * Copies the entire contents of an input stream to an output stream.
     *
     * @param in
     *            InputStream to be copied.
     * @param out
     *            OutputStream to receive the copy.
     */
    public static void copy(InputStream in, OutputStream out) {
        copy(in, out, BYTE_BUFFER_SIZE);
    }

    /**
     * Copies the entire contents of an input stream to an output stream.
     *
     * @param in
     *            InputStream to be copied.
     * @param out
     *            OutputStream to receive the copy.
     * @param bufferSize
     *            Size of buffer used for transfers.
     */
    public static void copy(InputStream in, OutputStream out, int bufferSize) {

        try {
            byte[] buffer = new byte[bufferSize];
            while (true) {
                int count = in.read(buffer);
                if (count < 0) {
                    break;
                } else if (count > 0) {
                    out.write(buffer, 0, count);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Copies the entire contents of a reader to a writer.
     *
     * @param reader
     *            Reader to be copied.
     * @param writer
     *            Writer to receive the copy.
     */
    public static void copy(Reader reader, Writer writer) {
        copy(reader, writer, CHAR_BUFFER_SIZE);
    }

    /**
     * Copies the entire contents of a reader to a writer.
     *
     * @param reader
     *            Reader to be copied.
     * @param writer
     *            Writer to receive the copy.
     * @param bufferSize
     *            Size of buffer used for transfers.
     */
    public static void copy(Reader reader, Writer writer, int bufferSize) {

        try {
            char[] buffer = new char[bufferSize];
            while (true) {
                int count = reader.read(buffer);
                if (count < 0) {
                    break;
                } else if (count > 0) {
                    writer.write(buffer, 0, count);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the given resource as a string using the given encoding.
     *
     * @param clazz
     *            Class that is the base for the resource.
     * @param resource
     *            Resource name to load.
     * @param encoding
     *            Encoding to use.
     */
    public static String toString(Class<?> clazz, String resource, String encoding) {
        InputStream in = null;
        try {
            in = clazz.getResourceAsStream(resource);
            return toString(in, encoding);
        } finally {
            close(in);
        }
    }

    /**
     * Reads the contents of a file into a string.
     *
     * @param file
     *            File to be loaded.
     * @param encoding
     *            Text encoding of the file, e.g. "UTF-8".
     */
    public static String toString(File file, String encoding) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return toString(fis, encoding);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            close(fis);
        }
    }

    /**
     * Reads the contents of an InputStream into a string.
     *
     * @param in
     *            InputStream to read.
     * @param encoding
     *            Text encoding of the stream, e.g. "UTF-8".
     */
    public static String toString(InputStream in, String encoding) {
        try {
            return toString(new InputStreamReader(in, encoding));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the contents of a Reader into a string.
     *
     * @param reader
     *            Reader to be read.
     */
    public static String toString(Reader reader) {
        CharArrayWriter caw = new CharArrayWriter();
        copy(reader, caw);
        return caw.toString();
    }

    /**
     * Loads the given resource as a string using UTF-8 encoding.
     *
     * @param clazz
     *            Class that is the base for the resource.
     * @param resource
     *            Resource name to load.
     */
    public static String toStringUtf8(Class<?> clazz, String resource) {
        return toString(clazz, resource, UTF_8);
    }

    /**
     * Reads the contents of a file into a string using UTF-8 character
     * encoding.
     *
     * @param file
     *            File to be read.
     */
    public static String toStringUtf8(File file) {
        return toString(file, UTF_8);
    }

    /**
     * Reads the contents of an input stream into a string using UTF-8 character
     * encoding.
     *
     * @param in
     *            Input stream to be read.
     */
    public static String toStringUtf8(InputStream in) {
        return toString(in, UTF_8);
    }

}
