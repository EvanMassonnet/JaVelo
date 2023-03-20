package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;

/**
 * représente le tableau de tous les nœuds du graphe JaVelo
 *
 * @author Evan Massonnet (346642)
 */
public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    private static final int EDGE_ID_START = 0;
    private static final int EDGE_ID_LENGTH = 28;
    private static final int EDGE_COUNT_START = 28;
    private static final int EDGE_COUNT_LENGTH = 4;

    /**
     * retourne le nombre total de nœuds
     * @return int
     */
    public int count(){
        return buffer.capacity() / NODE_INTS;
    }

    /**
     * retourne la coordonnée E du noeud d'identité donnée
     * @param nodeId id du noeud
     * @return la coordonnée E
     */
    public double nodeE(int nodeId){
        return Q28_4.asDouble(buffer.get(NODE_INTS * nodeId + OFFSET_E));
    }

    /**
     * retourne la coordonnée N du noeud d'identité donnée
     * @param nodeId id du noeud
     * @return la coordonnée N
     */
    public double nodeN(int nodeId){
        return Q28_4.asDouble(buffer.get(NODE_INTS * nodeId + OFFSET_N));
    }

    /**
     * retourne le nombre d'arêtes sortant du nœud d'identité donné
     * @param nodeId id du noeud
     * @return nb d'arêtes
     */
    public int outDegree(int nodeId){
        return Bits.extractUnsigned(buffer.get(NODE_INTS * nodeId + OFFSET_OUT_EDGES), EDGE_COUNT_START, EDGE_COUNT_LENGTH);
    }

    /**
     * retourne l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId
     * @param nodeId
     * @param edgeIndex
     * @return identité de la edgeIndex-ième arête
     */
    public int edgeId(int nodeId, int edgeIndex){
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return  Bits.extractUnsigned(buffer.get(NODE_INTS * nodeId + OFFSET_OUT_EDGES), EDGE_ID_START,EDGE_ID_LENGTH) + edgeIndex;
    }

}
