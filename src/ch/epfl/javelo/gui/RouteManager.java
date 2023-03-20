package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * gère l'affichage de l'itinéraire et (une partie de) l'interaction avec lui.
 *
 * @author Evan Massonnet (346642)
 */

public final class RouteManager {

    private final static int CIRCLE_RADIUS = 5;

    private final RouteBean bean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParametersP;

    private final Pane root;
    private final Polyline polyLine;
    private final Circle circle;


    public RouteManager(RouteBean bean, ReadOnlyObjectProperty<MapViewParameters> mapViewParametersP){

        this.bean = bean;
        this.mapViewParametersP = mapViewParametersP;

        root = new Pane();

        polyLine = new Polyline();
        polyLine.setId("route");

        circle = new Circle();
        circle.setId("highlight");
        circle.setRadius(CIRCLE_RADIUS);

        //on met a jour le dessin du cercle
        bean.highlightedPositionProperty().addListener((p, oldS, newS) -> placeCircle());

        circle.setOnMouseClicked(e ->{
            if(e.isStillSincePress()){

                Point2D mouse = circle.localToParent(e.getX(),e.getY());
                PointWebMercator a = mapViewParametersP.get().pointAt(mouse.getX(), mouse.getY());

                int SegmentId = bean.indexOfNonEmptySegmentAt(bean.highlightedPositionProperty().get());
                int NodeId = bean.routeProperty().get().nodeClosestTo(bean.highlightedPositionProperty().get());

                bean.waypoints.add(SegmentId+1, new Waypoint(a.toPointCh(), NodeId));
            }
        });


        //Si la route change (ajout ou suppression de point)
        bean.routeProperty().addListener((p, oldS, newS) -> {
            if(newS == null){
                circle.setVisible(false);
                polyLine.setVisible(false);
            }else{
                circle.setVisible(true);
                polyLine.setVisible(true);
                //recalculer polyline
                creatPolyline();
                //repositionner cercle et polyline
                placePolyline();
                placeCircle();

            }
        });

        //si deplacement ou zoom de la carte
        mapViewParametersP.addListener((p, oldS, newS) -> {
            //si la carte a ete zoomé
            if(oldS.zoom() != newS.zoom()){
                //recalculer polyline
                creatPolyline();
            }

            //repositionner cercle et polyline
            placePolyline();
            placeCircle();
        });

    }

    /**
     * retourne la pane contenant la route
     * @return pane
     */
    public Pane pane(){

        root.getChildren().add(polyLine);
        root.getChildren().add(circle);

        placeCircle();
        placePolyline();

        root.setPickOnBounds(false);

        return root;
    }


    /**
     * calcul de dessin de la polyline
     */
    private void creatPolyline(){
        if(bean.routeProperty().get() != null){

            List<Double> points = new ArrayList<>();        //liste intermediaire, on evite d'appeler .add de polyline plusieurs fois
            List<PointCh> pointChs = bean.routeProperty().get().points();

            PointWebMercator polyLinePos = PointWebMercator.ofPointCh( bean.routeProperty().get().pointAt(0));
            double firstX = polyLinePos.xAtZoomLevel(mapViewParametersP.get().zoom());
            double firstY = polyLinePos.yAtZoomLevel(mapViewParametersP.get().zoom());

            //l'origine de la polyline est le premier point
            //on soustrait a tout les points les coordonnes du premier

            for(PointCh pointCh : pointChs){
                points.add(PointWebMercator.ofPointCh(pointCh).xAtZoomLevel(mapViewParametersP.get().zoom()) - firstX);
                points.add(PointWebMercator.ofPointCh(pointCh).yAtZoomLevel(mapViewParametersP.get().zoom()) - firstY);
            }
            polyLine.getPoints().setAll(points);
        }
    }

    /**
     * placmeent de la polyline, utile lors d'un simple deplacement (la polyline n'est pas recalculé)
     */
    private void placePolyline(){
        if(bean.routeProperty().get() != null){

            //on place la polyline au coordonnee du premeir point par rapport a la fenetre

            PointWebMercator polyLinePos = PointWebMercator.ofPointCh( bean.routeProperty().get().pointAt(0));

            polyLine.setLayoutX(polyLinePos.xAtZoomLevel(mapViewParametersP.get().zoom()) - mapViewParametersP.get().x());
            polyLine.setLayoutY(polyLinePos.yAtZoomLevel(mapViewParametersP.get().zoom()) - mapViewParametersP.get().y());

        }
    }

    /**
     * placement et dessin du cercle
     */
    private void placeCircle(){
        if(bean.routeProperty().get() != null && !Double.isNaN(bean.highlightedPositionProperty().get())){
            circle.setVisible(true);
            PointWebMercator circlePos = PointWebMercator.ofPointCh(bean.routeProperty().get().pointAt(bean.highlightedPositionProperty().get()));
            circle.setLayoutX(circlePos.xAtZoomLevel(mapViewParametersP.get().zoom()) - mapViewParametersP.get().x());
            circle.setLayoutY(circlePos.yAtZoomLevel(mapViewParametersP.get().zoom())  - mapViewParametersP.get().y());
        }else{
            circle.setVisible(false);       //si il n'y a pas de route ou si aucun point n'est mis en evidence, on désactive le cercle
        }
    }






}
