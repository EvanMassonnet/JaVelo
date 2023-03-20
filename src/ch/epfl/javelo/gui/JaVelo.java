package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * classe principale de l'application
 *
 * @author Evan Massonnet (346642)
 */

public final class JaVelo extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println(grade(124, 102, 153,1));


        /*Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePath = Path.of("osm-cache");
        String tileServerHost = "https://tile.openstreetmap.org/";

        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        CostFunction cf = new CityBikeCF(graph);
        RouteComputer rc = new RouteComputer(graph, cf);
        RouteBean bean = new RouteBean(rc);

        ErrorManager errorManager= new ErrorManager();
        Consumer<String> errorConsumer = new ErrorConsumer(errorManager);

        AnnotatedMapManager mapManager = new AnnotatedMapManager(graph, tileManager, bean, errorConsumer);

        ElevationProfile profile = ElevationProfileComputer
                .elevationProfile(bean.routeProperty().get(), 5);

        ObjectProperty<ElevationProfile> profileProperty =
                new SimpleObjectProperty<>(profile);

        profileProperty.bind(Bindings.createObjectBinding(()-> ElevationProfileComputer
                .elevationProfile(bean.routeProperty().get(), 5), bean.routeProperty()));

        ElevationProfileManager profileManager =
                new ElevationProfileManager(profileProperty,
                        bean.highlightedPositionProperty());

        bean.highlightedPositionProperty().bind(Bindings.createDoubleBinding(() -> {    //mise en commun de mousePositionOnProfile et de mousePositionOnRoute pour l'affichage du point a mettre en evidence

            if(!Double.isNaN(profileManager.mousePositionOnProfileProperty().get()))
                return profileManager.mousePositionOnProfileProperty().get();
            if(!Double.isNaN(mapManager.mousePositionOnRouteProperty().get()))
                return mapManager.mousePositionOnRouteProperty().get();
            return Double.NaN;

        }, profileManager.mousePositionOnProfileProperty(), mapManager.mousePositionOnRouteProperty()));


        //Menu pour exporter en GPX
        MenuItem export = new MenuItem("Exporter GPX");
        export.setOnAction(e -> GpxGenerator.writeGpx("javelo.gpx", bean.routeProperty().get(), profileProperty.get()));
        export.disableProperty().bind(Bindings.createObjectBinding(() -> bean.routeProperty().get() == null, bean.routeProperty()));

        Menu file = new Menu("Fichier");
        file.getItems().add(export);

        MenuBar menu = new MenuBar();
        menu.getMenus().add(file);

        SplitPane mainPane = new SplitPane(new StackPane(mapManager.pane(), errorManager.pane()));  //mapPane et errorPane sont imbriqué dans une StackPane pour pouvoir se superposer
        mainPane.setOrientation(Orientation.VERTICAL);

        Pane profilePane = profileManager.pane();
        SplitPane.setResizableWithParent(profilePane, false);

        bean.routeProperty().addListener(e -> {     //si il y une route on affiche son profile sinon on cache la profilePane
            if(bean.routeProperty().get() != null){
                if(!mainPane.getItems().contains(profilePane))
                mainPane.getItems().add(profilePane);
            }else{
                mainPane.getItems().remove(profilePane);
            }
        });

        BorderPane global = new BorderPane();
        global.setCenter(mainPane);
        global.setTop(menu);

        global.getStylesheets().add("map.css");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(global));
        primaryStage.setTitle("JaVelo");
        primaryStage.show();

    }

    private record ErrorConsumer(ErrorManager errorManager)
            implements Consumer<String> {

        @Override
        public void accept(String s) {
            errorManager.displayError(s);
            //System.out.println(s);
        }*/
    }

    double grade(int p1, int p2, int pE, double b) {
        double p2b = Math.ceil(130d * Math.pow(p2 / 130d, 1d / b));
        double rawGrade = 0.875 + 5.25 * ((p1 + p2b + pE) / 500d);
        return Math.rint(rawGrade * 4) / 4;
    }
}
