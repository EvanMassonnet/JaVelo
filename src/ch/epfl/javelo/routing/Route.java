package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * interace pour la représentation d'un itinéraire
 *
 * @author Evan Massonnet (346642)
 */

public interface Route {

    /**
     * retourne l'index du segment à la position donnée
     * @param position (en mètres)
     * @return l'index du segment
     */
     int indexOfSegmentAt(double position);

    /**
     * retourne la longueur de l'itinéraire
     * @return longueur (en mètres)
     */
    double length();

    /**
     * retourne la totalité des arêtes de l'itinéraire
     * @return List<Edge>
     */
    List<Edge> edges();

    /**
     * retourne la totalité des points situés aux extrémités des arêtes de l'itinéraire
     * @return List<PointCh>
     */
    List<PointCh> points();

    /**
     * retourne le point se trouvant à la position donnée le long de l'itinéraire
     * @param position
     * @return PointCh
     */
    PointCh pointAt(double position);

    /**
     * retourne l'altitude à la position donnée le long de l'itinéraire
     * @param position
     * @return l'altitude
     */
    double elevationAt(double position);

    /**
     * retourne l'identité du nœud appartenant à l'itinéraire et se trouvant le plus proche de la position donnée
     * @param position
     * @return identité du nœud
     */
    int nodeClosestTo(double position);

    /**
     * retourne le point de l'itinéraire se trouvant le plus proche du point de référence donné
     * @param point
     * @return RoutePoint
     */
    RoutePoint pointClosestTo(PointCh point);
}
