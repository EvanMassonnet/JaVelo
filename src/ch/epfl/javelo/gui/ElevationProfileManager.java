package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;

/**
 * gère l'affichage et l'interaction avec le profil en long d'un itinéraire
 *
 * @author Evan Massonnet (346642)
 */

public final class ElevationProfileManager {

    private final static Insets padding = new Insets(10, 10, 20, 40);

    private final static int[] POS_STEPS =
            { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    private final static int[] ELE_STEPS =
            { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

    private final static int MIN_GAP_HORIZONTAL = 25;
    private final static int MIN_GAP_VERTICAL = 50;

    private final ReadOnlyObjectProperty<ElevationProfile> profileProperty;

    private final ObjectProperty<Rectangle2D> rectangle2D;
    private final ObjectProperty<Transform> screenToWorld;
    private final ObjectProperty<Transform> worldToScreen;

    private final SimpleDoubleProperty mousePositionOnProfileProperty;

    private final BorderPane borderPane;
    private final Pane pane;
    private final VBox vBox;
    private final Path path;
    private final Group group;
    private final Polygon polygon;


    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profileProperty, ReadOnlyDoubleProperty highlightProperty){

        this.profileProperty = profileProperty;

        mousePositionOnProfileProperty = new SimpleDoubleProperty(Double.NaN);

        borderPane = new BorderPane();
        borderPane.getStylesheets().add("elevation_profile.css");

        pane = new Pane();


        vBox = new VBox();
        vBox.setId("profile_data");

        path = new Path();
        path.setId("grid");

        group = new Group();

        polygon = new Polygon();
        polygon.setId("profile");

        rectangle2D = new SimpleObjectProperty<>();

        rectangle2D.bind(Bindings.createObjectBinding(() -> {
            double h = pane.getHeight() - padding.getBottom() - padding.getTop();
            double w = pane.getWidth() - padding.getRight() - padding.getLeft();
            return new Rectangle2D(padding.getLeft(), padding.getTop(), w<0 ? 0 : w , h<0 ? 0 : h);
        }, pane.heightProperty(), pane.widthProperty()));


        worldToScreen = new SimpleObjectProperty<>(new Affine());
        worldToScreen.bind(Bindings.createObjectBinding(() -> {
            Affine worldToScreenAffine = new Affine();
            if(profileProperty.get() != null){
                double useWidth = pane.getWidth() - padding.getLeft() - padding.getRight();
                double useHeight = pane.getHeight() - padding.getBottom() - padding.getTop();

                worldToScreenAffine.prependTranslation(0, - profileProperty.get().minElevation());
                worldToScreenAffine.prependScale(useWidth / profileProperty.get().length(), -useHeight / (profileProperty.get().maxElevation() - profileProperty.get().minElevation()));
                worldToScreenAffine.prependTranslation(padding.getLeft(), pane.getHeight() - padding.getBottom());
            }
            return worldToScreenAffine;
        }, pane.heightProperty(), pane.widthProperty(), profileProperty));

        screenToWorld = new SimpleObjectProperty<>(new Affine());

        screenToWorld.bind(Bindings.createObjectBinding(() -> {
            try {
                return worldToScreen.get().createInverse();
            }catch (Exception e){
                System.out.println("worldToScreen Transform non inversible");
                return null;
            }
        }, worldToScreen));


        Line line = new Line();

        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> worldToScreen.get().transform(highlightProperty.get(),0).getX(), worldToScreen, highlightProperty));

        line.startYProperty().bind(Bindings.select(rectangle2D, "minY"));
        line.endYProperty().bind(Bindings.select(rectangle2D, "maxY"));
        line.visibleProperty().bind(highlightProperty.greaterThanOrEqualTo(0));

        borderPane.setCenter(pane);
        borderPane.setBottom(vBox);

        pane.getChildren().addAll(path, group, polygon, line);

        pane.setOnMouseMoved(event -> {
            if(rectangle2D.get().contains(event.getX(), event.getY()) && profileProperty.get() != null){
                mousePositionOnProfileProperty.set(Math.round( (event.getX()- padding.getLeft()) * profileProperty.get().length() / rectangle2D.get().getWidth()));
            }else{
                mousePositionOnProfileProperty.set(Double.NaN);
            }
        });

        pane.setOnMouseExited(event -> mousePositionOnProfileProperty.set(Double.NaN));

        //listener pour la largeur du pane (resize)
        pane.widthProperty().addListener((p, oldS, newS) -> refresh());

        //listener pour la hauteur du pane (resize)
        pane.heightProperty().addListener((p, oldS, newS) -> refresh());

        //listener pour un changement de profile
        profileProperty.addListener((p, oldS, newS) -> refresh());

    }


    /**
     * retourne le panneau contenant le dessin du profil
     * @return pane
     */
    public Pane pane(){
        return borderPane;
    }


    /**
     * retourne une propriété en lecture seule contenant la position du pointeur de la souris le
     * long du profil (en mètres, arrondie à l'entier le plus proche), ou NaN si le pointeur
     * de la souris ne se trouve pas au-dessus du profil
     * @return mousePositionOnProfileProperty
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty(){
        return mousePositionOnProfileProperty;
    }


    /**
     * Dessine l'integraliter du profile
     */
    private void refresh(){
        if(profileProperty.get() != null ){

            path.getElements().clear();
            group.getChildren().clear();

            drawPolygone();

            drawHorizontalLineAndText();
            drawVerticalLineAndText();

            drawText();
        }
    }

    /**
     * Affiche le text avec les informations de l'itineraire
     */
    private void drawText(){

        vBox.getChildren().clear();
        vBox.getChildren().add(new Text(String.format("Longueur : %.1f km" +
                        "     Montée : %.0f m" +
                        "     Descente : %.0f m" +
                        "     Altitude : de %.0f m à %.0f m",
                profileProperty.get().length()/1000,
                profileProperty.get().totalAscent(),
                profileProperty.get().totalDescent(),
                profileProperty.get().minElevation(),
                profileProperty.get().maxElevation())));
    }


    /**
     * Dessin du profile
     */
    private void drawPolygone(){

        List<Double> array = new ArrayList<>();

        //bas droite
        array.add(pane.getWidth() - padding.getRight());
        array.add(pane.getHeight() - padding.getBottom());

        //bas gauche
        array.add(padding.getLeft());
        array.add(pane.getHeight() - padding.getBottom());

        int i = 0;
        while(i <= profileProperty.get().length()){
            Point2D point = worldToScreen.get().transform(i,profileProperty.get().elevationAt(i));
            array.add(point.getX());
            array.add(point.getY());

            ++i;
        }

        polygon.getPoints().setAll(array);

    }

    /**
     * Dessin des lignes horizontal et du text associé
     */
    private void drawHorizontalLineAndText(){


        int i = POS_STEPS.length-1;
        double gap = POS_STEPS[i];

        while(i >= 0){
            double pixelStep = rectangle2D.get().getWidth()/ (profileProperty.get().length() / POS_STEPS[i]);
            if(pixelStep >= MIN_GAP_HORIZONTAL){
               gap = POS_STEPS[i];
                --i;
            }else{
                i=-1;
            }
        }

        for(int j = 0; j < profileProperty.get().length() / gap; ++j){

            path.getElements().add(new MoveTo( worldToScreen.get().transform(gap * j, 0.0).getX(), padding.getTop()));
            path.getElements().add(new LineTo( worldToScreen.get().transform(gap * j, 0.0).getX(), pane.getHeight()-padding.getBottom()));

            Text textBottom = new Text();
            textBottom.setTextOrigin(VPos.TOP);
            textBottom.getStyleClass().add("grid_label");
            textBottom.getStyleClass().add("horizontal");
            textBottom.setFont(Font.font("Avenir", 10));

            textBottom.setText(Integer.toString((int) (j * gap / 1000)));
            textBottom.setX(worldToScreen.get().transform(gap * j, 0.0).getX() - textBottom.prefWidth(0)/2);
            textBottom.setY(pane.getHeight()-padding.getBottom());
            group.getChildren().add(textBottom);
        }
    }


    /**
     * Dessin des lignes vertical et du text associé
     */
    private void drawVerticalLineAndText(){



        int i = ELE_STEPS.length-1;
        int gap = ELE_STEPS[i];
        double totalHigh = profileProperty.get().maxElevation() - profileProperty.get().minElevation();

        while(i >= 0){
            double pixelStep = rectangle2D.get().getHeight() / (totalHigh / ELE_STEPS[i]);
            if(pixelStep >= MIN_GAP_VERTICAL){
                gap = ELE_STEPS[i];
                --i;
            }else{
                i=-1;
            }
        }

        double shift = Math2.ceilDiv((int) profileProperty.get().minElevation(), gap) * gap;
        double step = Math.round(totalHigh / gap);

        for(int j = 0; j <  step; ++j){

            path.getElements().add(new MoveTo(padding.getLeft(), worldToScreen.get().transform(0, shift + j * gap ).getY()));
            path.getElements().add(new LineTo(pane.getWidth()- padding.getRight(), worldToScreen.get().transform(0, shift + j * gap).getY()));

            Text textLeft = new Text();
            textLeft.setTextOrigin(VPos.CENTER);
            textLeft.getStyleClass().add("grid_label");
            textLeft.getStyleClass().add("vertical");
            textLeft.setFont(Font.font("Avenir", 10));

            textLeft.setText(Integer.toString(gap * (int)(profileProperty.get().minElevation()/gap +j+1)));
            textLeft.setX(padding.getLeft() - textLeft.prefWidth(0) -2);
            textLeft.setY(worldToScreen.get().transform(0, shift + j * gap ).getY());
            group.getChildren().add(textLeft);
        }

    }

}
