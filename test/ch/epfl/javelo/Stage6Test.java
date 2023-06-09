package ch.epfl.javelo;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;

import java.io.IOException;
import java.nio.file.Path;

public final class Stage6Test {
    public static void main(String[] args) throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        long t0 = System.nanoTime();
        Route r = rc.bestRouteBetween(159049, 117669);
        System.out.printf("Itinéraire calculé en %d ms\n", (System.nanoTime() - t0) / 1_000_000);
        //KmlPrinter.write("javelo.kml", r);

        GpxGenerator.writeGpx("gpxTest.xml", r, ElevationProfileComputer.elevationProfile(r, 1));

        Graph g2 = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf2 = new CityBikeCF(g2);
        RouteComputer rc2 = new RouteComputer(g2, cf2);
        long t02 = System.nanoTime();
        Route r2 = rc2.bestRouteBetween(2046055, 2694240);
        System.out.printf("Itinéraire calculé en %d ms\n", (System.nanoTime() - t02) / 1_000_000);





    }
}