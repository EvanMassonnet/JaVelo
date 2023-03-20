package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

/**
 * représente le point d'un itinéraire le plus proche d'un point
 * de référence donné, qui se trouve dans le voisinage de l'itinéraire
 *
 * @author Evan Massonnet (346642)
 */

public record RoutePoint(PointCh point, double position, double distanceToReference) {

    //représente un point qui n'existe pas
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);

    /**
     * retourne un point identique au récepteur (this) mais dont la position est
     * décalée de la différence donnée, qui peut être positive ou négative
     * @param positionDifference
     * @return RoutePoint
     */
    public RoutePoint withPositionShiftedBy(double positionDifference){
        return new RoutePoint(point, position + positionDifference, distanceToReference);
    }

    /**
     * retourne this si sa distance à la référence est inférieure ou égale à celle de that, et that sinon
     * @param that
     * @return RoutePoint (this ou that)
     */
    public RoutePoint min(RoutePoint that){
        return (distanceToReference <= that.distanceToReference()) ? this : that;

    }

    /**
     * retourne this si sa distance à la référence est inférieure ou égale à thatDistanceToReference,
     * et une nouvelle instance de RoutePoint dont les attributs sont les arguments passés à min sinon
     * @param thatPoint
     * @param thatPosition
     * @param thatDistanceToReference
     * @return RoutePoint (this ou un nouveau RoutePoint)
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference){
        return (distanceToReference <= thatDistanceToReference) ? this : new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }

}
