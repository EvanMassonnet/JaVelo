package ch.epfl.javelo.gui;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TileManagerTest {

    @Test
    void TileCreator() {
        var a = new TileManager(Path.of("tiles"), "https://tile.openstreetmap.org/");
        //var image = a.imageForTileAt(new TileManager.TileId(19, 271725, 185422));
        //var image2 = a.imageForTileAt(new TileManager.TileId(19, 271725, 185422));

        for(int i =0; i < 200; ++i){
            System.out.println(i);
            var image3 = a.imageForTileAt(new TileManager.TileId(18, i, i));
        }
        for(TileManager.TileId k : a.memoryTile.keySet()){
            System.out.println(k.x());
        }
        var image4 = a.imageForTileAt(new TileManager.TileId(18, 100, 100));
        var image3 = a.imageForTileAt(new TileManager.TileId(18, 200, 200));

        for(TileManager.TileId k : a.memoryTile.keySet()){
            System.out.println(k.x());
        }
    }

}