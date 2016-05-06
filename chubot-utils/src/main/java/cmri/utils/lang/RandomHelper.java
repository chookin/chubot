package cmri.utils.lang;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by zhuyin on 11/5/15.
 */
public class RandomHelper {
    private static Random random = ThreadLocalRandom.current();
    protected RandomHelper(){}

    /**
     * @param bound the upper bound (exclusive).  Must be positive.
     * @return the next pseudo random, uniformly distributed {@code int}
     *         value between zero (inclusive) and {@code bound} (exclusive)
     *         from this random number generator's sequence
     */
    public static int rand(int bound){
        return random.nextInt(bound);
    }

    /**
     * Returns a pseudorandom, uniformly distributed {@code int} value
     * between {@code min} (inclusive) and {@code max} (exclusive)
     *
     * @param min the lower bound, inclusive
     * @param max the upper bound, exclusive
     * @return the next pseudorandom
     */
    public static int rand(int min, int max){
        return random.nextInt(max - min)+ min;
    }

    public static String rand(String dict, int number){
        Random random = new Random();
        String sRand = "";
        for (int i = 0; i < number; i++) {
            String rand = String.valueOf(dict.charAt(random.nextInt(dict.length())));
            sRand += rand;
        }
        return sRand;
    }
}
