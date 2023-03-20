package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * représente un itinéraire multiple, c.-à-d. composé d'une séquence d'itinéraires contigus nommés segments.
 *
 * @author Evan Massonnet (346642)
 */

public final class MultiRoute implements Route{

    private final List<Route> segments;
    private final double[] distance;

    /**
     *  construit un itinéraire multiple composé des segments donnés,
     *  ou lève IllegalArgumentException si la liste des segments est vide
     * @param segments
     */
    public MultiRoute(List<Route> segments){
        Preconditions.checkArgument(!segments.isEmpty());

        this.segments = List.copyOf(segments);

        distance = new double[segments.size()+1];
        distance[0] = 0;
        for(int i = 1; i<distance.length; ++i){
            distance[i] = distance[i-1] + segments.get(i-1).length();
        }
    }

    @Override
    public int indexOfSegmentAt(double position) {
        int result = 0;
        //compter avant
        if (position > length())
            position = length();
        if(position <= 0)
            return 0;

        int i = 0;
        while(position > distance[i+1]){
            result += segments.get(i).indexOfSegmentAt(position) + 1;
            ++i;
        }

        //compter reste
        result += segments.get(i).indexOfSegmentAt(position - distance[i]);

        return result;

    }

    @Override
    public double length() {
        return distance[distance.length - 1];
    }

    @Override
    public List<Edge> edges() {
        List<Edge> result = new ArrayList<>();
        for(Route segment : segments){
            for(Edge edge : segment.edges()){
                result.add(edge);
            }
        }
        return result;
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> result = new ArrayList<>();
        for(Route segment : segments){
            for(PointCh point : segment.points()){
                if(!result.contains(point))
                    result.add(point);
            }
        }
        return result;
    }

    @Override
    public PointCh pointAt(double position) {
        if(position<= 0)
            return segments.get(0).pointAt(0);
        else if (position >= length())
            return segments.get(segments.size() - 1).pointAt(distance[distance.length - 1] - distance[distance.length - 2]);

        for(int i = 0; i < segments.size(); ++i){
            if(position < distance[i+1])
                return segments.get(i).pointAt(position - distance[i]);
        }
        return null;
    }

    @Override
    public double elevationAt(double position) {
        if(position< 0)
             return segments.get(0).elevationAt(0);
        else if (position > length())
            return segments.get(segments.size() - 1).elevationAt(distance[distance.length - 1] - distance[distance.length - 2]);

        for(int i = 0; i < segments.size(); ++i){
            if(position < distance[i+1])
                return segments.get(i).elevationAt(position - distance[i]);
        }
        return 0;
    }

    @Override
    public int nodeClosestTo(double position) {
        if(position< 0)
            position = 0;
        else if (position > length())
            position = length();

        if(position <= distance[1]){
            return segments.get(0).nodeClosestTo(position);
        }

        for(int i = 0; i < segments.size(); ++i){
            if(position <= distance[i+1])
                return segments.get(i).nodeClosestTo(position - distance[i]);
        }
        return 0;
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint result = segments.get(0).pointClosestTo(point);
        double shift = 0;
        for(int i = 1; i < segments.size(); ++i){
            shift += segments.get(i-1).length();
            result = result.min(segments.get(i).pointClosestTo(point).withPositionShiftedBy(shift));
        }
        return result;
    }

}
