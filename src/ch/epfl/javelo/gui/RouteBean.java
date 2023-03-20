package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * regroupe les propriétés relatives aux points de passage et à l'itinéraire correspondant.
 *
 * @author Evan Massonnet (346642)
 */

public final class RouteBean {


    private static final int MAX_LIST_SIZE = 200;   // limite le nombre max de point à 200
    private final HashMap<Dkey, Route> pathMap;     //liste des singleRoute entre chaque point, taille max de MAX_LIST_SIZE

    private final static int MAX_STEP_LENGTH = 5;

    public ObservableList<Waypoint> waypoints;
    private final DoubleProperty highlightedPosition;

    private final ObjectProperty<Route> route;
    private final ObjectProperty<ElevationProfile> elevationProfile;

    private final RouteComputer routeComputer;


    public RouteBean(RouteComputer routeComputer){

        this.routeComputer = routeComputer;
        this.waypoints = FXCollections.observableArrayList();
        this.highlightedPosition = new SimpleDoubleProperty();
        this.route = new SimpleObjectProperty<>();
        this.elevationProfile = new SimpleObjectProperty<>();

        pathMap = new HashMap<>();


        //si la liste des points de passage change, on recalcule l'itinéraire
        this.waypoints.addListener((ListChangeListener<? super Waypoint>) c -> {
            if(waypoints.size() <2){
                route.set(null);
                elevationProfile.set(null);
            }else{
                computePath();
            }
                });


    }

    /**
     * methode pour le calcule de l'itineraire
     */
    private void computePath(){

        if(waypoints.size() <2) //si il n'y a pas assez de point, on ne fait rien
            return;

        List<Route> singleRoutes= new ArrayList<>();

        while(pathMap.size() > MAX_LIST_SIZE){     //on vide la liste si la liste et trop grande (peu mieux faire)
            pathMap.clear();
        }

        //on calcule les nouvelles routes

        for(int i = 1; i < waypoints.size(); ++i){
            Dkey key = new Dkey(waypoints.get(i-1).nodeId(), waypoints.get(i).nodeId());
            if(key.startId != key.endId){
                if(!pathMap.containsKey(key)){  //si l'itineraire n'est pas deja dans la liste, on le calcule et on l'ajoute

                    Route newRoute =  routeComputer.bestRouteBetween(waypoints.get(i-1).nodeId(), waypoints.get(i).nodeId());          //probleme de performance quand aucun chemin n'est trouvé

                    if(newRoute != null)
                        pathMap.put(key, newRoute);
                    else{
                        route.set(null);
                        elevationProfile.set(null);
                        return;
                    }

                }
                if(pathMap.containsKey(key))
                    singleRoutes.add(pathMap.get(key));
            }
        }

        if(!singleRoutes.isEmpty()){
            MultiRoute multiRoute = new MultiRoute(singleRoutes);
            route.set(multiRoute);
            elevationProfile.set(ElevationProfileComputer.elevationProfile(multiRoute, MAX_STEP_LENGTH));
        }else{                  //si les points placé sont valide mais qu'aucun chemin n'a pu etre trouvé
            route.set(null);
            elevationProfile.set(null);
        }

    }

    /**
     * retourne la route en readOnlyProperty
     * @return route
     */
    public ReadOnlyObjectProperty<Route> routeProperty(){
        return route;
    }

    /**
     * retourne le profile en readOnlyProperty
     * @return elevationProfile
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty(){
        return elevationProfile;
    }

    /**
     * retourne la position mise en evidence en DoubleProperty
     * @return highlightedPosition
     */
    public DoubleProperty highlightedPositionProperty(){ //n'est pas en readOnly pour pouvoir ajouter un bind dans la class JaVelo
        return highlightedPosition;
    }

    /**
     * prend en argument une position le long de l'itinéraire et retourne
     * l'index du segment la contenant, en ignorant les segments vides
     * @param position le long de l'itineraire (en metres)
     * @return index du segment
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route.get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).nodeId();
            int n2 = waypoints.get(i + 1).nodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    /**
     * represente un couple de points de passage, utilisé par
     * la hashMap pathMap pour stocker l'itineraire associé
     */
    private record Dkey(int startId, int endId){
        public boolean equals(Dkey that){
            return that.startId == startId && that.endId == endId;
        }
    }
}
