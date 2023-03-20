package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * représente le tableau de toutes les arêtes du graphe JaVelo
 *
 * @author Evan Massonnet (346642)
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    private static final int OFFSET_INVERTED_AND_ID_INDEX = 0;
    private static final int OFFSET_LENGTH = OFFSET_INVERTED_AND_ID_INDEX + Integer.BYTES;
    private static final int OFFSET_ELEVATION =  OFFSET_LENGTH + Short.BYTES;
    private static final int OFFSET_ID_OSM =  OFFSET_ELEVATION + Short.BYTES;
    private static final int EDGES_INTS =  OFFSET_ID_OSM + Short.BYTES;

    private static final int PROFILE_INTS = Integer.BYTES;

    /**
     * retourne vrai ssi l'arête d'identité donnée va dans le sens inverse de la voie OSM dont elle provient
     * @param edgeId
     * @return boolean
     */
    public boolean isInverted(int edgeId){
        int direction = edgesBuffer.getInt(edgeId * EDGES_INTS);
        return direction < 0;
    }

    /**
     * retourne l'identité du nœud destination de l'arête d'identité donnée
     * @param edgeId
     * @return int
     */
    public int targetNodeId(int edgeId){
        int idTarget = edgesBuffer.getInt(edgeId * EDGES_INTS);
        return idTarget >= 0 ? idTarget : ~idTarget;
    }

    /**
     * retourne la longueur de l'arête d'identité donnée
     * @param edgeId
     * @return longueur (en mètres)
     */
    public double length(int edgeId){
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(edgeId * EDGES_INTS + OFFSET_LENGTH)));
    }

    /**
     * retourne le dénivelé positif de l'arête d'identité donnée
     * @param edgeId
     * @return dénivelé (en mètres)
     */
    public double elevationGain(int edgeId){
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(edgeId * EDGES_INTS + OFFSET_ELEVATION)));
    }

    /**
     * retourne vrai ssi l'arête d'identité donnée possède un profil
     * @param edgeId
     * @return boolean
     */
    public boolean hasProfile(int edgeId){
        int profile = Bits.extractUnsigned(profileIds.get(edgeId), 30,2);
        return profile != 0;
    }

    /**
     * retourne le tableau des échantillons du profil de l'arête d'identité donnée,
     * qui est vide si l'arête ne possède pas de profil
     * @param edgeId
     * @return float[]
     */
    public float[] profileSamples(int edgeId){
        int profile = Bits.extractUnsigned(profileIds.get(edgeId), 30,2);
        int size = 1 + Math2.ceilDiv(edgesBuffer.getShort(edgeId * EDGES_INTS + OFFSET_LENGTH), Q28_4.ofInt(2));
        float[] result = new float[size];
        int profileStart = Bits.extractUnsigned(profileIds.get(edgeId), 0,30);

        if(profile == 0){
            return new float[0];
        }else if(profile == 1) {
            for (int i = 0; i < size; ++i) {
                result[i] = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(profileStart + i)));
            }

        }else if(profile == 2){
            result[0] = Q28_4.asFloat(elevations.get(profileStart));

            for(int i = 1; i < size; ++i){
                result[i] = result[i-1] + Q28_4.asFloat(Bits.extractSigned(elevations.get(profileStart + 1 + (i-1)/2), 8 - (i-1)%2 * 8, 8));
            }


        }else if (profile == 3){
            result[0] = Q28_4.asFloat(elevations.get(profileStart));

            for(int i = 1; i < size; ++i){
                result[i] = result[i-1] + Q28_4.asFloat(Bits.extractSigned(elevations.get(profileStart + 1 + (i-1)/4), 12 - (i-1)%4 * 4, 4));
            }
        }

        if(isInverted(edgeId)){
            for(int i = 0; i < size /2; ++i){
                float save = result[i];
                result[i] = result[size - i -1];
                result[size - i -1] = save;
            }
        }

        return result;

    }

    /**
     * retourne l'identité de l'ensemble d'attributs attaché à l'arête d'identité donnée
     * @param edgeId
     * @return identité de l'ensemble d'attributs
     */
    public int attributesIndex(int edgeId){

        return Short.toUnsignedInt(edgesBuffer.getShort(edgeId * EDGES_INTS + OFFSET_ID_OSM));
    }


}
