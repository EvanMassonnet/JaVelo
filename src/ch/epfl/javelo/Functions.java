package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 *contient des méthodes permettant de créer des objets
 * représentant des fonctions mathématiques des réels vers les réels
 *
 * @author Evan Massonnet (346642)
 */

public final class Functions {
    private  Functions(){
    }

    /**
     * retourne une fonction constante, dont la valeur est toujours y
     * @param y
     * @return une fonction constante
     */
    public static DoubleUnaryOperator constant(double y){
        return new Constant(y);
    }

    /**
     * retourne une fonction obtenue par interpolation linéaire entre les
     * échantillons samples, espacés régulièrement et couvrant la plage allant de 0 à xMax
     * @param samples
     * @param xMax
     * @return une fonction obtenue par interpolation linéaire
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        if(samples.length < 2 || xMax <= 0){
            throw new IllegalArgumentException();
        }
        return new Sampled(samples, xMax);
    }

    /**
     * Classe fonction constante
     */
    private static final class Constant implements DoubleUnaryOperator {
        private final double y;

        public Constant(double y){
            this.y = y;
        }

        @Override
        public double applyAsDouble(double operand) {
            return y;
        }
    }

    /**
     * Class fonction interpolé
     */
    private static final class Sampled implements DoubleUnaryOperator {

        private final double[] x;
        private final float[] y;

        public Sampled(float[] samples, double xMax){
            x = new double[samples.length];
            y = new float[samples.length];

            for(int i = 0; i < samples.length; ++i){
                x[i] = i * xMax / (samples.length - 1);
                y[i] = samples[i];
            }
        }

        @Override
        public double applyAsDouble(double operand) {
            if(operand <= 0){
                return y[0];
            }else if(operand >= x[x.length - 1]){
                return y[y.length - 1];
            }

            int i = 0;
            while(x[i] < operand){
                ++i;
            }

            return y[i-1] + (operand - x[i-1]) * ((y[i] - y[i-1]) / (x[i] - x[i-1])) ;
        }
    }
}
