package ch.epfl.javelo.projection;

/**
 *Methode pour le passage en coordonnee WGS84 et en coordonnees suisse
 *
 * @author Evan Massonnet (346642)
 */

public final class Ch1903 {
    private  Ch1903(){
    }

    /**
     * Convertie WGS84 en la composante E des coordonnées suisses
     * @param lon longitude (en radians)
     * @param lat latitude (en radians)
     * @return E dans les coordonnées suisses (en mètres)
     */
    public static double e(double lon, double lat){
        lon = Math.toDegrees(lon);
        lat = Math.toDegrees(lat);

        double lon1 = 1e-4 * (3600 * lon - 26782.5);
        double lat1 = 1e-4 * (3600 * lat - 169028.66);

        double E = 2600072.37
                + 211455.93 * lon1
                - 10938.51 * lon1 * lat1
                - 0.36 * lon1 * lat1 * lat1
                - 44.54 * lon1 * lon1 * lon1;

        return E;
    }

    /**
     * Convertie WGS84 en la composante N des coordonnées suisses
     * @param lon (en radians)
     * @param lat (en radians)
     * @return N en coordonnées suisses (en mètres)
     */
    public static double n(double lon, double lat){
        lon = Math.toDegrees(lon);
        lat = Math.toDegrees(lat);
        double lon1 = 1e-4 * (3600*lon - 26782.5);
        double lat1 = 1e-4 * (3600*lat - 169028.66);

        double N = 1200147.07
                + 308807.95 * lat1
                + 3745.25 * lon1 * lon1
                + 76.63 * lat1 * lat1
                - 194.56 * lon1 * lon1 * lat1
                + 119.79 * lat1 * lat1 * lat1;

        return N;
    }

    /**
     * Convertie les coordonnées suisses en la longitude de WGS84
     * @param e (en mètres)
     * @param n (en mètres)
     * @return la longitude dans le système WGS84 (en radians)
     */
    public static double lon(double e, double n){
        double x = 1e-6 * (e - 2600000);
        double y = 1e-6 * (n - 1200000);

        double lon0 = 2.6779094
                + 4.728982 * x
                + 0.791484 * x * y
                + 0.1306 * x * y * y
                - 0.0436 * x * x * x;

        return Math.toRadians(lon0 * (100f / 36));

    }

    /**
     * Convertie les coordonnées suisses en la latitude de WGS84
     * @param e (en mètres)
     * @param n (en mètres)
     * @return la latitude dans le système WGS84 (en radians)
     */
    public static double lat(double e, double n){
        double x = 1e-6 * (e - 2600000);
        double y = 1e-6 * (n - 1200000);

        double lat0 = 16.9023892
                + 3.238272 * y
                - 0.270978 * x * x
                - 0.002528 * y * y
                - 0.0447 * x * x * y
                - 0.0140 * y * y * y;

        return Math.toRadians(lat0 * (100f / 36));

    }

}
