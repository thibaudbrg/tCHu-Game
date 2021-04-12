package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;

import java.util.Arrays;
import java.util.function.Function;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface Serde<E> {


    String serialize(E e);

    E deserialize(String s);

    static <T> Serde<T> of(Function<T, String> s, Function<String, T> ds) {
        return new Serde<T>() {
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
                Preconditions.checkArgument(list.indexOf(t) != -1);
                return String.valueOf(list.indexOf(t));
            }

            @Override
            public T deserialize(String s) {
                Preconditions.checkArgument(Integer.parseInt(s) < list.size());
                return list.get(Integer.parseInt(s));
            }
        };
    }

    static <T> Serde<List<T>> listOf(Serde<T> se, String sep) {
        return new Serde<List<T>>() {


            @Override
            public String serialize(List<T> ts) {
                List<String> serializedList = ts.stream().map(t -> {
                    return se.serialize(t);
                }).collect(Collectors.toList());
                return String.join("+", serializedList);
            }

            @Override
            public List<T> deserialize(String s) {
                String[] serializedArray = s.split(Pattern.quote("+"), -1);
                List<T> deserializedList = Arrays.stream(serializedArray).map(t -> {
                    return se.deserialize(t);
                }).collect(Collectors.toList());
                return deserializedList;
            }
        };
    }
}
