package com.example.crud.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SerialVersionUID {

    /**
     * Compute a UID for the serialization version of a class.
     *
     * The Java object serialization standard defines an algorithm for computing the default serialVersionUID of a
     * class: http://docs.oracle.com/javase/6/docs/platform/serialization/spec/class.html#4100. This method computes a
     * serialVersionUID value models classes. For example, the com.example.models.User class should specify the following:
     *
     * <code><pre>
     *   private static final long serialVersionUID = SerialVersionUID.computeUID(User.class);
     * </pre></code>
     *
     * Java provides a way to access and possibly compute a default value for serialVersionUID using
     * java.io.ObjectStreamClass. If a class has no serialVersionID, the
     * {@link java.io.ObjectStreamClass#getSerialVersionUID()} computes a default value according to the algorithm
     * defined by the object serialization standard. However, we do not use this method because there is no clean way to
     * access the private method computeDefaultSUID that performs the computation.
     *
     * Therefore, this method is based on, but different from the implementation of
     * java.io.ObjectStreamClass#computeDefaultSIUD(Class<?>). This implementation does not factor various elements of
     * the default computation into the resulting hash because the goal is to characterize versions of the class which
     * are compatible for serialization and deserialization of their fields even if method signatures change. We don't
     * want the addition of a method or a constructor to change the computed value. Therefore, this method uses only
     * some of the internal logic of the default algorithm: it implements steps 1, 4, 8 and 9 of the standard algorithm
     * and omits steps 2, 3, 5, 6 and 8.
     *
     * Like the standard algorithm, this implementation writes various elements of the class definition to a
     * DataOutputStream and then computes a SHA-1 digest of the stream and returns a hash based on the digest. Unlike
     * the standard algorithm, this implementation does not include interface, method and constructor signatures.
     *
     * @param clazz
     *            The class
     * @return A version UID.
     */
    public static long compute(final Class<?> clazz) {
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(bytesOut);

            // #1
            dataOut.writeUTF(clazz.getName());

            // #4
            List<Field> sorted = Arrays.asList(clazz.getDeclaredFields());
            sorted.sort(new Comparator<Field>() {

                    @Override
                    public int compare(Field field1, Field field2) {
                        return field1.getName().compareTo(field2.getName());
                    }

                });
            for (final Field field : sorted) {
                int mods = field.getModifiers() &
                    (Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED |
                     Modifier.STATIC | Modifier.FINAL | Modifier.VOLATILE |
                     Modifier.TRANSIENT);
                if (((mods & Modifier.PRIVATE) == 0) ||
                    ((mods & (Modifier.STATIC | Modifier.TRANSIENT)) == 0))
                    // Don't include private static or
                    // private transient fields
                    {
                        dataOut.writeUTF(field.getName());
                        dataOut.writeInt(mods);
                        dataOut.writeUTF(field.getType().getName());
                    }
            }
            dataOut.flush();

            // #8
            MessageDigest md = MessageDigest.getInstance("SHA");
            byte[] hashBytes = md.digest(bytesOut.toByteArray());

            // #9
            long hash = 0L;
            for (int i = Math.min(hashBytes.length, 8) - 1; i >= 0; i--) {
                hash = (hash << 8) | (hashBytes[i] & 0xFF);
            }
            return hash;

        }
        catch (IOException ex) {
            throw new InternalError(ex);
        }
        catch (NoSuchAlgorithmException ex) {
            throw new SecurityException(ex.getMessage());
        }
    }

}
