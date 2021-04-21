package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents what is sometimes called a serde (from serializer-deserializer),
 * i.e. an object capable of serializing and deserializing values of a given type
 *
 * @author Decotignie Matthieu (329953)
 * @author Bourgeois Thibaud (324604)
 */
public interface Serde<E> {

    /**
     * Takes as argument the object to serialize and returns the corresponding string
     *
     * @param e (E) The object to serialize
     * @return (String) The corresponding string
     */
    String serialize(E e);

    /**
     * Takes as argument a string and returns the corresponding object
     *
     * @param s (String) The String to deserialize
     * @return (Serde<T>) The corresponding serde
     */
    E deserialize(String s);




    /**
     * Takes as arguments a serialization function and a deserialization function,
     * and returns the corresponding serde
     *
     * @param s (Function<T, String>) The serialization function
     * @param ds (Function<String, T>)  The deserialization function
     * @param <T> The type of the object that the method serializes or deserializes
     * @return (Serde<T>) The corresponding serde
     */
    static <T> Serde<T> of(Function<T, String> s, Function<String, T> ds) {
        return new Serde<>() {
            @Override
            public String serialize(T t) {
                return s.apply(t);
            }

            @Override
            public T deserialize(String s) {
                return ds.apply(s);
            }
        };
    }

    /**
     * Takes as argument the list of all values of an enumerated set of values
     * and returns the corresponding serde
     *
     * @param list (List<T>) The list of all values of an enumerated set of values
     * @param <T> The type of the object that the method serializes or deserializes
     * @return (Serde) The corresponding serde
     */
    static <T> Serde oneOf(List<T> list) {
        return new Serde<T>() {
            @Override
            public String serialize(T t) {
                if (t == null) return new String();

                Preconditions.checkArgument(list.contains(t));
                return String.valueOf(list.indexOf(t));
            }

            @Override
            public T deserialize(String s) {
                if (s.isEmpty()) return null;

                Preconditions.checkArgument(Integer.parseInt(s) < list.size());
                return list.get(Integer.parseInt(s));
            }
        };
    }

    /**
     * Takes as argument a serde and a separator character
     * and returns a serde capable of (de)serializing lists of values (de)serialized by the given serde
     *
     * @param se (Serde<T>) The Serde capable to (de)serialize the objects in the List
     * @param sep (String) The separator character
     * @param <T> The type of the object that the method serializes or deserializes
     * @return (Serde<List<T>>) The corresponding serde
     */
    static <T> Serde<List<T>> listOf(Serde<T> se, String sep) {
        return new Serde<>() {
            @Override
            public String serialize(List<T> ts) {
                if (ts.isEmpty()) return new String();
                List<String> serializedList = ts.stream().map(se::serialize).collect(Collectors.toList());
                return String.join(sep, serializedList);
            }

            @Override
            public List<T> deserialize(String s) {
                if (s.isEmpty()) return List.of();

                String[] serializedArray = s.split(Pattern.quote(sep), -1);
                return Arrays.stream(serializedArray)
                        .map(se::deserialize)
                        .collect(Collectors.toList());
            }
        };
    }

    /**
     * Takes as argument a serde and a separator character
     * and returns a serde capable of (de)serializing a SortedBag (de)serialized by the given serde
     *
     * @param se (Serde<T>) The Serde capable to (de)serialize the objects in the SortedBag
     * @param sep (String) The separator character
     * @param <T> The type of the object that the method serializes or deserializes
     * @return (Serde<SortedBag<T>>) The corresponding serde
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> se, String sep) {
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> ts) {
                if (ts.isEmpty()) return new String();

                List<String> serializedList = ts.stream()
                        .map(se::serialize)
                        .collect(Collectors.toList());
                return String.join(sep, serializedList);
            }

            @Override
            public SortedBag<T> deserialize(String s) {
                if (s.isEmpty()) return SortedBag.of();
                String[] serializedArray = s.split(Pattern.quote(sep), -1);
                List<T> deserializedList = Arrays.stream(serializedArray)
                        .map(se::deserialize)
                        .collect(Collectors.toList());

                return SortedBag.of(deserializedList);
            }
        };
    }
}
