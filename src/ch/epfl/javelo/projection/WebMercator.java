package ch.epfl.javelo.projection;


import ch.epfl.javelo.Math2;

/**
 * permet de convertir entre les coordonnées WGS 84 et les coordonnées Web Mercator
 *
 * @author Evan Massonnet (346642)
 */


public final class WebMercator {
    private WebMercator(){
    }

    /**
     * retourne la coordonnée x de la projection d'un point se trouvant à la longitude lon
     * @param lon (en radians)
     * @return x
     */
    public static double x(double lon){
        return 1/(2*Math.PI) * (lon + Math.PI);
    }

    /**
     * retourne la coordonnée y de la projection d'un point se trouvant à la latitude lat
     * @param lat (en radians)
     * @return y la coordonnée de la projection
     */
    public static double y(double lat){
        return 1/(2*Math.PI) * (Math.PI - Math2.asinh(Math.tan(lat)));
    }

    /**
     * retourne la longitude d'un point dont la projection se trouve à la coordonnée x donnée
     * @param x
     * @return  longitude (en radians)
     */
    public static double lon(double x){
        return 2 * Math.PI * x - Math.PI;
    }

    /**
     * retourne la latitude d'un point dont la projection se trouve à la coordonnée y donnée
     * @param y
     * @return latitude (en radians)
     */
    public static double lat(double y){
        return Math.atan(Math.sinh(Math.PI - 2 * Math.PI * y));
    }

}
