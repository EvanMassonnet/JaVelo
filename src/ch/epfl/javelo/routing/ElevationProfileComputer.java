package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

import static java.lang.Float.NaN;

/**
 * représente un calculateur de profil en long. C'est-à-dire qu'elle contient
 * le code permettant de calculer le profil en long d'un itinéraire donné
 *
 * @author Evan Massonnet (346642)
 */
public final class ElevationProfileComputer {

    private ElevationProfileComputer(){}

    /**
     * retourne le profil en long de l'itinéraire route, en garantissant que l'espacement
     * entre les échantillons du profil est d'au maximum maxStepLength mètres
     * lève IllegalArgumentException si cet espacement n'est pas strictement positif
     * @param route
     * @param maxStepLength
     * @return le profil en long de l'itinéraire route
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength){
        if(route == null)
            return null;

        Preconditions.checkArgument(maxStepLength > 0);

        int nbStep = (int)((route.length() + maxStepLength -1) / maxStepLength) + 2;
        float[] elevations = new float[nbStep];

        for(int i = 0; i < nbStep; ++i){
            elevations[i] = (float)route.elevationAt(i * maxStepLength);
        }

        //remplir le debut du tableau
        float min = NaN;
        int minIndex = 0;

        //on cherche le premiere element du tableau different de NaN
        while(Float.isNaN(min) && minIndex != nbStep){
            if(!Float.isNaN(elevations[minIndex]))
                min = elevations[minIndex];
            ++minIndex;
        }

        //Si on ne n'en a pas trouvé, on contruit un tableau de 0 et on le renvoie
        if(Float.isNaN(min)){
            Arrays.fill(elevations, 0);
            return new ElevationProfile(route.length(), elevations);
        }

        //Sinon on comble le debut du tableau avec la premeier valeur trouvé
        Arrays.fill(elevations, 0, minIndex ,min);

        //remplir la fin du tableau

        float max = NaN;
        int maxIndex = (int)nbStep - 1;

        //on cherche le dernier element du tableau different de NaN (il existe forcement)
        while(Float.isNaN(max)){
            if(!Float.isNaN(elevations[maxIndex]))
                max = elevations[maxIndex];
            --maxIndex;
        }

        //on remplie la fin du tableau avec cette valeur
        Arrays.fill(elevations, maxIndex + 1, elevations.length ,max);

       //remplir le milieu du tableau
        int startGap = 0;
        int endGap = 0;
        for(int j =1; j < nbStep; ++j){
            if(startGap == 0 && Float.isNaN(elevations[j])){            //debut des valeurs manquantes
                startGap = j;
                endGap = j;
            }else if(Float.isNaN(elevations[j])){
                endGap = j;

            }else if(!Float.isNaN(elevations[j]) && startGap != 0){     //fin des valeurs manquantes

                int x1 = startGap - 1;
                int x2 = endGap +1;
                float y1 = elevations[x1];
                float y2 = elevations[x2];

                for(int k = startGap; k < endGap +1; ++k){
                    elevations[k] = ((y1 - y2) / (x1 - x2)) * k + (x1 * y2 - x2 * y1) / (x1 - x2);
                }
                startGap = endGap = 0;
            }

        }

        return new ElevationProfile(route.length() , elevations);
    }

}
