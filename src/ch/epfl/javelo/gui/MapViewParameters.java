package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * représente les paramètres du fond de carte présenté dans l'interface graphique.
 * x et y en webMercator
 * @author Evan Massonnet (346642)
 */

public record MapViewParameters(int zoom, double x, double y) {

    /**
     * retourne les coordonnées du coin haut-gauche sous la forme d'un objet de type Point2D
     * @return Point2D
     */
    public Point2D topLeft(){
        return new Point2D(x, y);
    }

    /**
     *  retourne une instance de MapViewParameters identique au récepteur,
     *  si ce n'est que les coordonnées du coin haut-gauche sont celles passées en arguments à la méthode
     * @param x
     * @param y
     * @return
     */
    public MapViewParameters withMinXY(double x, double y){
        return new MapViewParameters(zoom, x, y);
    }

    /**
     * prend en arguments les coordonnées x et y d'un point, exprimées par
     * rapport au coin haut-gauche de la portion de carte affichée à l'écran,
     * et retourne ce point sous la forme d'une instance de PointWebMercator
     * @param x
     * @param y
     * @return
     */
    public PointWebMercator pointAt(double x, double y){
        return PointWebMercator.of(zoom,x + this.x, y + this.y);
    }

    /**
     *  prennent en argument un point Web Mercator et retournent la position x
     *  correspondante, exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran
     * @param point
     * @return position x
     */
    public double viewX(PointWebMercator point){
        return this.x - point.x();
    }

    /**
     *  prennent en argument un point Web Mercator et retournent la position y
     *  correspondante, exprimée par rapport au coin haut-gauche de la portion de carte affichée à l'écran
     * @param point
     * @return position y
     */
    public double viewY(PointWebMercator point){
        return this.y - point.y();
    }

}
