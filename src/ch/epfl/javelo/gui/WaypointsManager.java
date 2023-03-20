package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

/**
 * gère l'affichage et l'interaction avec les points de passage
 *
 * @author Evan Massonnet (346642)
 */

public final class WaypointsManager {
    private final static int MAX_SEARCH = 1000;

    private final Graph graph;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final ObservableList<Waypoint> waypoints;
    private final Consumer<String> errorConsumer;
    private final Pane root;

    private double mouseStartX;
    private double mouseStartY;



    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParametersP, ObservableList<Waypoint> waypoints, Consumer<String> errorConsumer ){
        this.graph = graph;
        this.mapViewParametersP = mapViewParametersP;
        this.waypoints = waypoints;
        this.errorConsumer = errorConsumer;

        root = new Pane();

        this.mapViewParametersP.addListener((o, oV, nV) -> drawGroups());
        this.waypoints.addListener((ListChangeListener) c -> drawGroups());
    }

    /**
     * retourne le panneau contenant les points de passage
     * @return pane
     */
    public Pane pane(){

        drawGroups();
        root.setPickOnBounds(false);
        return root;
    }

    /**
     * prend en arguments les coordonnées x et y d'un point et ajoute
     * un nouveau point de passage au nœud du graphe qui en est le plus proche
     * @param x coordonnées x
     * @param y coordonnées y
     */
    public void addWaypoint(double x, double y){
        Waypoint point = creatWaypoint(x,y);
        if(point != null)
            waypoints.add(point);
    }

    private Waypoint creatWaypoint( double x, double y){



        PointWebMercator a = PointWebMercator.of(mapViewParametersP.get().zoom(),x,y);

        int id = graph.nodeClosestTo(a.toPointCh(), MAX_SEARCH);
        if(id == -1){
            errorConsumer.accept("Aucune route à proximité !");
        }else{
            return new Waypoint(a.toPointCh(), id);
        }
        return null;
    }

    /**
     * Créé un nouvelle objet de type Group qui représente un pin
     * @param type first, middle ou last
     * @return pin
     */
    private Group creatPin(String type){

        SVGPath pin_outside = new SVGPath();
        pin_outside.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        pin_outside.getStyleClass().add("pin_outside");

        SVGPath pin_inside = new SVGPath();
        pin_inside.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
        pin_inside.getStyleClass().add("pin_inside");

        Group pin = new Group(pin_outside, pin_inside);
        pin.getStyleClass().add("pin");
        pin.getStyleClass().add(type);

        return pin;
    }

    private void drawGroups(){
        int nb = 0;
        root.getChildren().clear();

        for (Waypoint w : waypoints){

            Group group;

            if(nb == 0){
                group = creatPin("first");
            }else if (nb == waypoints.size()-1){
                group = creatPin("last");
            }else{
                group = creatPin("middle");
            }
            group.setLayoutX(PointWebMercator.ofPointCh(w.pathPoint()).xAtZoomLevel(mapViewParametersP.get().zoom()) - mapViewParametersP.get().x());
            group.setLayoutY(PointWebMercator.ofPointCh(w.pathPoint()).yAtZoomLevel(mapViewParametersP.get().zoom()) - mapViewParametersP.get().y());

            //deplacer
            group.setOnMousePressed(e -> {
                mouseStartX = e.getX();
                mouseStartY = e.getY();
            });

            group.setOnMouseDragged(e -> {
                group.setLayoutX(group.getLayoutX() + e.getX() - mouseStartX);
                group.setLayoutY(group.getLayoutY() + e.getY() - mouseStartY);
            });

            group.setOnMouseReleased(e -> {
                if(e.isStillSincePress()){      //si la souris n'a pas bougé, on supprime le point
                    waypoints.remove(w);
                }else {                         //sinon on met a jour la carte et la liste des points
                    Waypoint point = creatWaypoint(mapViewParametersP.get().x() + group.getLayoutX(), mapViewParametersP.get().y() + group.getLayoutY());
                    if(point != null)
                        waypoints.set(waypoints.indexOf(w), point);
                }
                drawGroups();
            });

            root.getChildren().add(group);
            ++nb;
        }

    }
}
