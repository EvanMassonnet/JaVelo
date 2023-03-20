package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *représente un itinéraire simple, c.-à-d. reliant un point de départ à un
 * point d'arrivée, sans point de passage intermédiaire
 *
 * @author Evan Massonnet (346642)
 */
public final class SingleRoute implements Route{

    private final List<Edge> edges;
    private final double[] distance;

    /**
     * retourne l'itinéraire simple composé des arêtes données,
     * ou lève IllegalArgumentException si la liste d'arêtes est vide
     * @param edges
     * @throws IllegalArgumentException si la liste des edges est vide
     */
    public SingleRoute(List<Edge> edges){
        Preconditions.checkArgument(!edges.isEmpty());

        this.edges = List.copyOf(edges);

        distance = new double[edges.size()+1];
        distance[0] = 0;
        for(int i = 1; i<distance.length; ++i){
            distance[i] = distance[i-1] + edges.get(i-1).length();
        }
    }

    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    public double length() {
        return distance[distance.length - 1];
    }

    @Override
    public List<Edge> edges() {
        return List.copyOf(edges);
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> result = new ArrayList<>();
        for(int i = 0; i < edges.size(); ++i){
            result.add(edges.get(i).pointAt(0));
        }
        result.add(edges.get(edges.size()-1).pointAt(edges.get(edges.size()-1).length()));
        return result;
    }

    @Override
    public PointCh pointAt(double position) {
        if(position< 0)
            position = 0;
        else if (position > length())
            position = length();
        int positionIndex = Arrays.binarySearch(distance, position);
        positionIndex = (positionIndex<0)?-positionIndex-2:positionIndex;
        if(position == length()){
            --positionIndex;
        }
        return edges.get(positionIndex).pointAt(position - distance[positionIndex]);
    }

    @Override
    public double elevationAt(double position) {
        if(position< 0)
            position = 0;
        else if (position > length())
            position = length();
        int positionIndex = Arrays.binarySearch(distance, position);
        positionIndex = (positionIndex<0)?-positionIndex-2:positionIndex;
        if(position == length()){
            --positionIndex;
        }
        double result = edges.get(positionIndex).elevationAt(position - distance[positionIndex]);
        if(Double.isNaN(result) && position - distance[positionIndex] == 0 && positionIndex > 0){          //si il y a discontinuité avec un NaN on regarde le profile de l'edge précedente
            return edges.get(positionIndex-1).elevationAt(position - distance[positionIndex-1]);
        }
        return result;
    }

    @Override
    public int nodeClosestTo(double position) {
        if(position< 0)
            position = 0;
        else if (position > length())
            position = length();
        int positionIndex = Arrays.binarySearch(distance, position);
        positionIndex = (positionIndex<0)?-positionIndex-2:positionIndex;
        if(position == length()){
            --positionIndex;
        }

        double edgePosition = position - distance[positionIndex];
        if(edgePosition <= edges.get(positionIndex).length()/2)
            return edges.get(positionIndex).fromNodeId();
        return edges.get(positionIndex).toNodeId();
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        Edge a = edges.get(0);
        PointCh projection = a.pointAt(Math2.clamp(0, a.positionClosestTo(point), a.length()));
        double norm = Math2.squaredNorm(projection.e() - point.e(),projection.n() -point.n());
        RoutePoint result = new RoutePoint(projection, a.positionClosestTo(point), norm);

        for(Edge edge : edges){
            projection = edge.pointAt(Math2.clamp(0, edge.positionClosestTo(point), edge.length()));

            norm = Math2.norm(projection.e() - point.e(),projection.n() -point.n());
            double position = edge.positionClosestTo(point);
            position = Math2.clamp(0, position, edge.length());
            result = result.min(projection, position + distance[edges.indexOf(edge)], norm);
        }
        return result;
    }
}
