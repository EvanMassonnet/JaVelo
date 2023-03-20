package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * représente une arête d'un itinéraire
 *
 * @author Evan Massonnet (346642)
 */

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length, DoubleUnaryOperator profile){

    /**
     * retourne une instance de Edge dont les attributs fromNodeId et toNodeId sont ceux donnés,
     * les autres étant ceux de l'arête d'identité edgeId dans le graphe Graph
     * @param graph
     * @param edgeId
     * @param fromNodeId
     * @param toNodeId
     * @return un Edge dont les attributs fromNodeId et toNodeId sont ceux donnés
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId), graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * retourne la position le long de l'arête qui se trouve la plus proche du point donné
     * @param point
     * @return position (en mètres)
     */
    public double positionClosestTo(PointCh point){
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(), toPoint.n(), point.e(), point.n());
    }

    /**
     * retourne le point se trouvant à la position donnée sur l'arête
     * @param position (en mètres)
     * @return PointCh
     */
    public PointCh pointAt(double position){
        return new PointCh(Math2.interpolate(fromPoint.e(), toPoint.e(), position/length),  Math2.interpolate(fromPoint.n(), toPoint.n(), position/length));
    }

    /**
     * retourne l'altitude à la position donnée sur l'arête
     * @param position
     * @return altitude (en mètres)
     */
    public double elevationAt(double position){
        if(profile == null)
            return Double.NaN;
        return profile.applyAsDouble(position);
    }


}
