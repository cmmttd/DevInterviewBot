package com.belogrudov.javabot.utils;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomUtil {

    public static int get() {
        return new Random().nextInt();
    }

    public static int in(int min, int max) {
        return new Random()
                .ints(min, max)
                .findAny()
                .getAsInt();
    }

    public static int in(List<Integer> list) {
        return list.get(new Random()
                .ints(0, list.size())
                .findAny()
                .getAsInt());
    }

    /**
     * @param minInclude
     * @param maxInclude
     * @param excludeList
     * @return random number in range (min, max) exclude List
     */
    public static int inRangeExcludeList(int min, int max, List<Integer> excludeList) {
        List<Integer> randInts = IntStream.rangeClosed(min, max).boxed().collect(Collectors.toList());
        randInts.removeAll(excludeList);
        if (randInts.isEmpty()) return 0;
        else return in(randInts);
    }
}
