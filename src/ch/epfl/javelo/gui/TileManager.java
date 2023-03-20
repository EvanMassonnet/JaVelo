package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * représente un gestionnaire de tuiles OSM.
 * Son rôle est d'obtenir les tuiles depuis un serveur de
 * tuile et de les stocker dans un cache mémoire et dans un cache disque
 *
 * @author Evan Massonnet (346642)
 */

public final class TileManager {

    private final static int MEMORY_SIZE = 100; //nombre max de tuille en mémoire

    private final Path savePath;
    private final String serverURL;
    public LinkedHashMap<TileId, Image> memoryTile;


    public TileManager(Path path, String server){
        savePath = path;
        serverURL = server;
        memoryTile = new LRU(MEMORY_SIZE);
    }

    public Image imageForTileAt(TileId tileId){

        System.out.println(serverURL + tileId.zoomLevel + "/" + tileId.x + "/" + tileId.y + ".png");

        if (!memoryTile.containsKey(tileId)) {
            loadNewTile(tileId);
        }
        return memoryTile.get(tileId);
    }

    private void loadNewTile(TileId tileId){
        Path currentPath = savePath.resolve(tileId.zoomLevel + "\\" + tileId.x + "\\" + tileId.y + ".png");

        if(Files.exists(currentPath)){
            try(InputStream image = new FileInputStream(currentPath.toString())){
                memoryTile.put(tileId, new Image(image));

            }catch (IOException e){
                e.printStackTrace();
            }

        }else{
            try{
                URL u = new URL(
                        serverURL + tileId.zoomLevel + "/" + tileId.x + "/" + tileId.y + ".png");
                URLConnection c = u.openConnection();
                c.setRequestProperty("User-Agent", "JaVelo");
                try(InputStream i = c.getInputStream()){

                    //ajouter au cache disque

                    Files.createDirectories(savePath.resolve(tileId.zoomLevel + "\\" + tileId.x + "\\"));
                    File outPutFile = new File(currentPath.toString());
                    OutputStream outStream = new FileOutputStream(outPutFile);
                    i.transferTo(outStream);
                    outStream.close();
                    loadNewTile(tileId);    //on rappel la meme fonction pour charger la tuille
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Represente une tuille grace a son triplé (zoom,x,y)
     */
    record TileId(int zoomLevel, int x, int y){
        public TileId{
            if(!isValid(zoomLevel, x, y))
                throw new IllegalArgumentException();
        }
        boolean isValid(int zoomLevel, int x, int y){
            if(zoomLevel < 0)
                return false;
            double nbTile = Math.pow(2, zoomLevel);
            return !(x < 0 || y < 0 || x >= nbTile || y >= nbTile);
        }
    }

    /**
     * LRU cache (Least Recently Used) pour la gestion de la memoire
     */
    private static class LRU extends LinkedHashMap<TileId, Image>{
        private final int cacheSize;
        LRU(int size){
            super(size, 0.75F, true);
            cacheSize = size;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<TileId, Image> eldest) {
            return this.size() > cacheSize;
        }
    }
}
