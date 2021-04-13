package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface Serde<E> {


    String serialize(E e);

    E deserialize(String s);

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

    static <T> Serde oneOf(List<T> list) {
        return new Serde<T>() {
            @Override
            public String serialize(T t) {
                if (t==null) return new String();

                Preconditions.checkArgument(list.contains(t));
                return String.valueOf(list.indexOf(t));
            }

            @Override
            public T deserialize(String s) {
                if(s.isEmpty()) return null;

                Preconditions.checkArgument(Integer.parseInt(s) < list.size());
                return list.get(Integer.parseInt(s));
            }
        };
    }

    static <T> Serde<List<T>> listOf(Serde<T> se, String sep) {
        return new Serde<>() {
            @Override
            public String serialize(List<T> ts) {
                if (ts.isEmpty()) return new String();

                List<String> serializedList = ts.stream()
                        .map(se::serialize)
                        .collect(Collectors.toList());
                return String.join(sep, serializedList);
            }

            @Override
            public List<T> deserialize(String s) {
                if(s.isEmpty()) return null;

                String[] serializedArray = s.split(Pattern.quote(sep), -1);
                return Arrays.stream(serializedArray)
                        .map(se::deserialize)
                        .collect(Collectors.toList());
            }
        };
    }

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
                if(s.isEmpty()) return null;
                String[] serializedArray = s.split(Pattern.quote(sep), -1);
                List<T> deserializedList = Arrays.stream(serializedArray)
                        .map(se::deserialize)
                        .collect(Collectors.toList());

                return SortedBag.of(deserializedList);
            }
        };
    }
}
