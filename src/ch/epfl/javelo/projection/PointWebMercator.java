package ch.epfl.javelo.projection;

/**
 * représente un point dans le système Web Mercator
 *
 * @author Evan Massonnet (346642)
 */

public record PointWebMercator(double x, double y) {

    private final static int ZOOM_CONST = 8;

    /**
     * Contructeur de PointWebMercator
     * @param x
     * @param y
     * @throws IllegalArgumentException si x n'est pas entre 0 et 1 ou si y n'est pas entre 0 et 1
     */
    public PointWebMercator{
        if(!(x >=0 && x<= 1 && y >=0 && y<= 1)){
            throw new IllegalArgumentException();
        }
    }

    /**
     * retourne le point dont les coordonnées sont x et y au niveau de zoom zoomLevel
     * @param zoomLevel
     * @param x
     * @param y
     * @return PointWebMercator
     */
    public static PointWebMercator of(int zoomLevel, double x, double y){
        return new PointWebMercator(Math.scalb(x, -(zoomLevel + ZOOM_CONST)), Math.scalb(y, -(zoomLevel + ZOOM_CONST)));
    }

    /**
     * retourne le point Web Mercator correspondant au point du système de coordonnées suisse donné
     * @param pointCh
     * @return PointWebMercator
     */
    public static PointWebMercator ofPointCh(PointCh pointCh){
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }

    /**
     * retourne la coordonnée x au niveau de zoom donné
     * @param zoomLevel
     * @return double
     */
    public double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x, zoomLevel + ZOOM_CONST);
    }

    /**
     * retourne la coordonnée y au niveau de zoom donné
     * @param zoomLevel
     * @return
     */
    public double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y, zoomLevel + ZOOM_CONST);
    }

    /**
     * retourne la longitude du point
     * @return longitude (en radians)
     */
    public double lon(){
        return WebMercator.lon(x);
    }

    /**
     * retourne la latitude du point
     * @return latitude (en radians)
     */
    public double lat(){
        return WebMercator.lat(y);
    }

    /**
     * retourne le point de coordonnées suisses se trouvant à la même position que le récepteur (this) ou null si ce point n'est pas dans les limites de la Suisse définies par SwissBounds
     * @return le point de coordonnées suisses
     */
    public PointCh toPointCh(){
        double e = Ch1903.e(lon(), lat());
        double n =  Ch1903.n(lon(), lat());
        if(SwissBounds.containsEN(e,n)){
            return new PointCh(e,n);
        }else{
            return null;
        }
    }
}
