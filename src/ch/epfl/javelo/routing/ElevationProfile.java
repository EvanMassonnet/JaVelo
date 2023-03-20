package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.function.DoubleUnaryOperator;

/**
 * représente le profil en long d'un itinéraire simple ou multiple
 *
 * @author Evan Massonnet (346642)
 */

public final class ElevationProfile {

    private float[] samples;
    private double length;
    private float minSample;
    private float maxSample;
    private double totalAscent;
    private double totalDescent;

    private DoubleUnaryOperator profile;

    /**
     * Constructeur public d'ElevationProfile
     * @param length
     * @param elevationSamples
     * @throws IllegalArgumentException si length <=0 ou si elevationSamples contient moins de deux elements
     */
    public ElevationProfile(double length, float[] elevationSamples){
        Preconditions.checkArgument(!(length <= 0 || elevationSamples.length < 2));

        this.length = length;

        samples = new float[elevationSamples.length];
        minSample = maxSample = elevationSamples[0];

        totalAscent = 0;
        totalDescent = 0;

        for(int i = 0; i < elevationSamples.length-1; ++i){
         samples[i] = elevationSamples[i];
         minSample = Math.min(samples[i], minSample);
         maxSample = Math.max(samples[i], maxSample);

         float diff = elevationSamples[i+1] - elevationSamples[i] ;
         totalAscent += (diff > 0)? diff : 0;
         totalDescent += (diff < 0)? -diff : 0;

        }

        samples[elevationSamples.length-1] = elevationSamples[elevationSamples.length-1];
        minSample = Math.min(samples[elevationSamples.length-1], minSample);
        maxSample = Math.max(samples[elevationSamples.length-1], maxSample);

        profile = Functions.sampled(samples, length);
    }

    /**
     * retourne la longueur du profil
     * @return longueur (en mètres)
     */
    public double length(){
        return this.length;
    }

    /**
     * retourne l'altitude minimum du profil
     * @return altitude (en mètres)
     */
    public double minElevation(){
        return minSample;
    }

    /**
     * retourne l'altitude maximum du profil
     * @return altitude (en mètres)
     */
    public double maxElevation(){
        return maxSample;
    }

    /**
     * retourne le dénivelé positif total du profil
     * @return dénivelé positif (en mètres)
     */
    public double totalAscent(){
        return totalAscent;
    }

    /**
     * retourne le dénivelé négatif total du profil
     * @return dénivelé négatif (en mètres)
     */
    public double totalDescent(){
        return totalDescent;
    }

    /**
     * retourne l'altitude du profil à la position donnée, qui n'est pas forcément
     * comprise entre 0 et la longueur du profil; le premier échantillon est
     * retourné lorsque la position est négative, le dernier lorsqu'elle est supérieure à la longueur
     * @param position
     * @return l'altitude du profil à la position donnée
     */
    public double elevationAt(double position){
        return profile.applyAsDouble(position);
    }



}
