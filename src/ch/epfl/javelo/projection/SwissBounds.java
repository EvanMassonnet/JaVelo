package ch.epfl.javelo.projection;

/**
 *Constante et méthode pour les limites de la suisse
 *
 * @author Evan Massonnet (346642)
 */
public final class SwissBounds {

    public final static double MIN_E = 2485000;
    public final static double MAX_E = 2834000;
    public final static double MIN_N = 1075000;
    public final static double MAX_N = 1296000;
    public final static double WIDTH = MAX_E - MIN_E;
    public final static double HEIGHT = MAX_N - MIN_N;


    private  SwissBounds(){
    }

    /**
     * retourne vrai ssi les coordonnées E et N données sont dans les limites de la Suisse.
     * @param e     composante e en coordonnee suisse
     * @param n     composante n en coordonnee suisse
     * @return      boolean, vrai si le point appartient a la suisse
     */
    public static boolean containsEN(double e, double n){
        return e >= MIN_E && e <= MAX_E && n >= MIN_N && n <= MAX_N;
    }


}
