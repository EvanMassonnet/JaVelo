package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;



/**
 * gère l'affichage et l'interaction avec le fond de carte
 *
 * @author Evan Massonnet (346642)
 */

public final class BaseMapManager {

    private final static int TILE_SIZE = 256;

    private final TileManager tileManager;

    private final Canvas canvas;
    private final Pane pane;

    private double mouseStartX;
    private double mouseStartY;

    private boolean redrawNeeded;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;

    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> mapViewParametersP){

        this.tileManager = tileManager;
        this.mapViewParametersP = mapViewParametersP;

        canvas = new Canvas();
        pane = new Pane();
        pane.getChildren().add(canvas);

        //Si les parametres du font de carte change, on le redessine
        this.mapViewParametersP.addListener((o, oV, nV) -> redrawOnNextPulse());


        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        //gere le scroll de la souris (donné par le prof, compatible avec les autres OS)
        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            if (e.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            int zoomDelta = (int) Math.signum(e.getDeltaY());

            int zoom = Math2.clamp(8, mapViewParametersP.get().zoom() + zoomDelta,19);

            PointWebMercator mousePos = mapViewParametersP.get().pointAt(e.getX(), e.getY());

            mapViewParametersP.set(new MapViewParameters(zoom, (int)(mousePos.xAtZoomLevel(zoom) -e.getX()), (int)(mousePos.yAtZoomLevel(zoom) -e.getY())));

        });

        pane.setOnMouseClicked(e ->{
            if(e.isStillSincePress())
                waypointsManager.addWaypoint(mapViewParametersP.get().x() + e.getX(), mapViewParametersP.get().y() + e.getY());
        });

        //debut du deplacement de la carte
        pane.setOnMousePressed(e -> {
            mouseStartX = e.getX();
            mouseStartY = e.getY();
        });

        pane.setOnMouseDragged(e -> {

            mapViewParametersP.set(mapViewParametersP.get().withMinXY(mapViewParametersP.get().x() - (e.getX() - mouseStartX), mapViewParametersP.get().y() - (e.getY() - mouseStartY)));
            mouseStartX = e.getX();
            mouseStartY = e.getY();
        });

        //redimensionner
        pane.heightProperty().addListener(e -> redrawOnNextPulse());

        pane.widthProperty().addListener(e -> redrawOnNextPulse());

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
    }

    /**
     * retournant le panneau JavaFX affichant le fond de carte
     * @return pane
     */
    public Pane pane(){
        return pane;
    }

    /**
     * Redessine la map si besoin
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        //représente le decalage entre l'origine de la tuille placé en haut a gauche avec le bords haut gauche de l'écran
        double borderX = (mapViewParametersP.get().x()/TILE_SIZE - (int)mapViewParametersP.get().x() /TILE_SIZE) * TILE_SIZE;
        double borderY = (mapViewParametersP.get().y()/TILE_SIZE - (int)mapViewParametersP.get().y() /TILE_SIZE) * TILE_SIZE;

        for(int i = 0 ; i < pane.getWidth()/TILE_SIZE + 1; ++i){
            for(int j = 0; j < pane.getHeight()/TILE_SIZE + 1; ++j){

                TileManager.TileId firstTile = new TileManager.TileId(mapViewParametersP.get().zoom(), (int)mapViewParametersP.get().x() /TILE_SIZE + i , (int)mapViewParametersP.get().y() /TILE_SIZE +j);
                canvas.getGraphicsContext2D().drawImage(tileManager.imageForTileAt(firstTile),i*TILE_SIZE  - borderX, j*TILE_SIZE - borderY);
            }
        }

    }

    /**
     * demande un redessin de la map
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

}
