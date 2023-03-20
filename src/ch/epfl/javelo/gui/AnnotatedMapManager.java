package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

/**
 * gère l'affichage de la carte annotée, c.-à-d. le fond de carte au-dessus duquel sont superposés l'itinéraire et les points de passage
 *
 * @author Evan Massonnet (346642)
 */

public final class AnnotatedMapManager {

    //valeur de depart pour l'affichage de la carte
    private final static int DEFAULT_ZOOM = 12;
    private final static int DEFAULT_X = 543200;
    private final static int DEFAULT_Y = 370650;

    private final static int MAX_MOUSE_DISTANCE = 15; //distance maximal pour afficher le point sur la carte (en pixel)

    private final  StackPane stackPane;
    private final DoubleProperty mousePositionOnRouteProperty;
    private final ObjectProperty<Point2D> currentMousePosProperty;

    private final ObjectProperty<MapViewParameters> mapViewParametersP;

    public AnnotatedMapManager(Graph graph, TileManager tileManager, RouteBean bean, Consumer<String> errorConsumer){


        currentMousePosProperty = new SimpleObjectProperty<>(null);

        MapViewParameters mapViewParameters =
                new MapViewParameters(DEFAULT_ZOOM, DEFAULT_X, DEFAULT_Y );
        mapViewParametersP =
                new SimpleObjectProperty<>(mapViewParameters);

        RouteManager routeManager =
                new RouteManager(bean,
                        mapViewParametersP);

        WaypointsManager waypointsManager =
                new WaypointsManager(graph,
                        mapViewParametersP,
                        bean.waypoints,
                        errorConsumer);
        BaseMapManager baseMapManager =
                new BaseMapManager(tileManager,
                        waypointsManager,
                        mapViewParametersP);

        stackPane = new StackPane();
        stackPane.getChildren().addAll(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
        stackPane.getStylesheets().add("map.css");

        mousePositionOnRouteProperty = new SimpleDoubleProperty(Double.NaN);

        stackPane.setOnMouseMoved(e -> currentMousePosProperty.set(new Point2D(e.getX(), e.getY())));

        stackPane.setOnMouseExited(e -> currentMousePosProperty.set(null));


        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(() -> {

            if(currentMousePosProperty.get() == null || bean.routeProperty().get() == null) //si la souris n'a encore jamais ete placé sur l'écran ou si il n'y a pas de route, retourne NaN
                return Double.NaN;

            PointCh pos = PointWebMercator.of(mapViewParametersP.get().zoom(), mapViewParametersP.get().x() + currentMousePosProperty.get().getX(), mapViewParametersP.get().y() + currentMousePosProperty.get().getY()).toPointCh();
            RoutePoint nearestPoint = bean.routeProperty().get().pointClosestTo(pos);

            //conversion de la distance max exprimer en pixel en distance PointCh
            double maxDistance = PointWebMercator.of(mapViewParametersP.get().zoom(), mapViewParametersP.get().x() + MAX_MOUSE_DISTANCE, mapViewParametersP.get().y()).toPointCh().distanceTo(PointWebMercator.of(mapViewParametersP.get().zoom(), mapViewParametersP.get().x(), mapViewParametersP.get().y()).toPointCh());

            if(nearestPoint.distanceToReference() <= maxDistance)
                return nearestPoint.position();

            return Double.NaN;
        }, mapViewParametersP, currentMousePosProperty, bean.routeProperty()));

    }

    /**
     * retourne le panneau contenant la carte annotée
     * @return stackPane
     */
    public Pane pane(){
        return stackPane;
    }


    /**
     * retoure la propriété contenant la position du pointeur de la souris le long de l'itinéraire
     *  ssi la distance séparant ce point du pointeur est inférieure ou égale à 15 unités
     *  JavaFX (pixels). Sinon, elle contient NaN.
     * @return mousePositionOnRouteProperty
     */
    public ReadOnlyDoubleProperty mousePositionOnRouteProperty(){
        return mousePositionOnRouteProperty;
    }

}