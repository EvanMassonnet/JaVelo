package ch.epfl.javelo;

/**
 *Class fournissant des outils mathématiques
 *
 * @author Evan Massonnet (346642)
 */

public final class Math2 {
    private Math2(){
    }

    /**
     * Retourne la partie entière par excès de la division de x par y
     * @param x numérateur
     * @param y dénominateur
     * @throws IllegalArgumentException si x est négatif ou si y est negatif ou nul
     * @return int
     */
    public static int ceilDiv(int x, int y){
        Preconditions.checkArgument(!(x < 0 || y <= 0));
        return (x +y -1) / y;
    }

    /**
     * retourne la coordonnée y du point se trouvant sur la droite passant par (0,y0) et (1,y1) et de coordonnée x
     * @param y0    point en 0
     * @param y1    point en 1
     * @param x     abscisse du point
     * @return      ordonnée du point
     */
    public static double interpolate(double y0, double y1, double x){
        return Math.fma(y1 - y0, x,  y0);
    }

    /**
     * limite la valeur v entre min et max
     * @param min   borne inf
     * @param v     valeur a limiter
     * @param max   borne min
     * @return      une valeur comprise entre min et max
     */
    public static int clamp(int min, int v, int max){
        return (int) clamp((double) min, (double) v, (double) max);
    }

    public static double clamp(double min, double v, double max){
        Preconditions.checkArgument(max >= min);

        if(v <=  min) return min;
        if(v >= max) return max;
        return v;

    }

    /**
     * retourne le sinus hyperbolique inverse de son argument x
     * @param x argument
     * @return sinus hyperbolique inverse
     */
    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1 + x*x));
    }

    /**
     * calcule le produit scalaire de deux vecteur de coordonnee (uX, uY) et (vX, vY)
     * @param uX    première coordonnee du vecteur U
     * @param uY    deuxieme coordonnee du vecteur U
     * @param vX    première coordonnee du vecteur V
     * @param vY    deuxieme coordonnee du vecteur V
     * @return  produit scalaire entre U et V
     */
    public static double dotProduct(double uX, double uY, double vX, double vY){
        return Math.fma(uX, vX, uY*vY);
    }

    /**
     * Calcule le carré de la norme du vecteur de composante (uX, uY)
     * @param uX    première coordonnee du vecteur U
     * @param uY    deuxieme coordonnee du vecteur U
     * @return  carré de la norme
     */
    public static double squaredNorm(double uX, double uY){
        return uX*uX + uY*uY;
    }

    /**
     * Calcule la norme du vecteur de composante (uX, uY)
     * @param uX    première coordonnee du vecteur U
     * @param uY    deuxieme coordonnee du vecteur U
     * @return      la norme du vecteur U
     */
    public static double norm(double uX, double uY){
        return Math.sqrt(squaredNorm(uX, uY));
    }

    /**
     * retourne la longueur de la projection du vecteur allant du point A (de coordonnées aX et aY) au point P (de coordonnées pX et pY) sur le vecteur allant du point A au point B (de composantes bY et bY)
     * @param aX    première coordonnee du point A
     * @param aY    deuxieme coordonnee du point A
     * @param bX    première coordonnee du point B
     * @param bY    deuxieme coordonnee du point B
     * @param pX    première coordonnee du point P
     * @param pY    deuxieme coordonnee du point P
     * @return      projection du vecteur PA sur le vecteur BA
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY){
        double uX = pX -aX;
        double uY = pY - aY;
        double vX = bX - aX;
        double vY = bY - aY;

        return dotProduct(uX, uY, vX, vY) / norm(vX, vY);
    }


}
