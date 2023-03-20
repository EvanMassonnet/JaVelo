package ch.epfl.javelo;

/**
 * permettent de convertir des nombres entre la représentation Q28.4 et d'autres représentations
 *
 * @author Evan Massonnet (346642)
 */

public final class Q28_4 {

    private final static int SHIFT_4 = 4;
    private final static int SHIFT_16 = 16;


    private Q28_4(){

    }

    /**
     * retourne la valeur Q28.4 correspondant à l'entier donné
     * @param i
     * @return int
     */
    public static int ofInt(int i){
        return i*SHIFT_16;
    }

    /**
     * retourne la valeur de type double égale à la valeur Q28.4 donnée
     * @param q28_4
     * @return double
     */
    public static double asDouble(int q28_4){
        return Math.scalb((double)q28_4, -SHIFT_4);
    }

    /**
     * retourne la valeur de type float correspondant à la valeur Q28.4 donnée
     * @param q28_4
     * @return float
     */
    public static float asFloat(int q28_4){
        return Math.scalb(q28_4, -SHIFT_4);
    }

}
