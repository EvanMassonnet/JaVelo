package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 *représente un point dans le système de coordonnées suisse
 *
 * @author Evan Massonnet (346642)
 */

public record PointCh(double e, double n) {
    public PointCh{
        if(!SwissBounds.containsEN(e,n))
            throw new IllegalArgumentException();
    }

    /**
     * retourne le carré de la distance en mètres séparant le récepteur (this) de l'argument that
     * @param that
     * @return distance (en mètres)
     */
    public double squaredDistanceTo(PointCh that){
        return Math2.squaredNorm(that.e - e, that.n - n);
    }

    /**
     * retourne la distance en mètres séparant le récepteur (this) de l'argument that
     * @param that
     * @return distance (en mètres)
     */
    public double distanceTo(PointCh that){
        return Math.sqrt(squaredDistanceTo(that));
    }

    /**
     * retourne la longitude du point (e,n)
     * @return longitude (en radians)
     */
    public double lon(){
        return Ch1903.lon(e,n);
    }

    /**
     * retourne la longitude du point (e,n)
     * @return latitude (en radians)
     */
    public double lat(){
        return Ch1903.lat(e,n);
    }
}
