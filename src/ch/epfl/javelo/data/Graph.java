package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * représente le graphe JaVelo
 *
 * @author Evan Massonnet (346642)
 */

public final class Graph {

    private static final String attributesPath = "attributes.bin";
    private static final String edgesPath = "edges.bin";
    private static final String elevationsPath = "elevations.bin";
    private static final String nodesPath = "nodes.bin";
    private static final String nodeOsmIdPath = "nodes_osmid.bin";
    private static final String profileIdsPath = "profile_ids.bin";
    private static final String sectorsPath = "sectors.bin";

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;


    /**
     * Constructeur public de Graph
     * @param nodes
     * @param sectors
     * @param edges
     * @param attributeSets
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     * retourne le graphe JaVelo obtenu à partir des fichiers se trouvant dans le répertoire dont le chemin d'accès est basePath
     * @param basePath
     * @return un nouveau graph
     * @throws IOException si le chemin n'est pas trouvé
     */
    public static Graph loadFrom(Path basePath) throws IOException {

        IntBuffer graphNodeBuffer;

        try (FileChannel nodesChannel = FileChannel.open(basePath.resolve(nodesPath))){
            graphNodeBuffer = nodesChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, nodesChannel.size())
                    .asIntBuffer();
        }

        ByteBuffer sectorsBuffer;

        try (FileChannel sectorsChannel = FileChannel.open(basePath.resolve(sectorsPath))){
            sectorsBuffer = sectorsChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, sectorsChannel.size());
        }

        ByteBuffer edgesBuffer;

        try (FileChannel edgesChannel = FileChannel.open(basePath.resolve(edgesPath))){
            edgesBuffer = edgesChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, edgesChannel.size());
        }

        IntBuffer profileIds;

        try (FileChannel profileIdsChannel = FileChannel.open(basePath.resolve(profileIdsPath))){
            profileIds = profileIdsChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, profileIdsChannel.size())
                    .asIntBuffer();
        }

        ShortBuffer elevations;

        try (FileChannel elevationsChannel = FileChannel.open(basePath.resolve(elevationsPath))){
            elevations = elevationsChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, elevationsChannel.size())
                    .asShortBuffer();
        }

        LongBuffer attributesBuffer;

        try (FileChannel attributesChannel = FileChannel.open(basePath.resolve(attributesPath))){
            attributesBuffer = attributesChannel
                    .map(FileChannel.MapMode.READ_ONLY, 0, attributesChannel.size())
                    .asLongBuffer();
        }

        List<AttributeSet> attributeSets = new ArrayList<>();
        for(int i = 0; i < attributesBuffer.capacity(); ++i){
            attributeSets.add(new AttributeSet(attributesBuffer.get(i)));
        }

        return new Graph(new GraphNodes(graphNodeBuffer),
                new GraphSectors(sectorsBuffer),
                new GraphEdges(edgesBuffer,profileIds,elevations),
                attributeSets
                );
    }

    /**
     * retourne le nombre total de nœuds dans le graphe
     * @return int
     */
    public int nodeCount(){
        return nodes.count();
    }

    /**
     * retourne la position du nœud d'identité donnée
     * @param nodeId
     * @return PointCh
     */
    public PointCh nodePoint(int nodeId){
        return new PointCh(nodes.nodeE(nodeId),nodes.nodeN(nodeId));
    }

    /**
     * retourne le nombre d'arêtes sortant du nœud d'identité donnée
     * @param nodeId
     * @return int
     */
    public int nodeOutDegree(int nodeId){
        return nodes.outDegree(nodeId);
    }

    /**
     * retourne l'identité de la edgeIndex-ième arête sortant du nœud d'identité nodeId
     * @param nodeId
     * @param edgeIndex
     * @return int
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex){
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * retourne l'identité du nœud se trouvant le plus proche du point donné,
     * à la distance maximale donnée (en mètres), ou -1 si aucun nœud ne correspond à ces critères
     * @param point
     * @param searchDistance
     * @return int
     */
    public int nodeClosestTo(PointCh point, double searchDistance){

        double minDistance = Double.MAX_VALUE;
        int nodeId = 0;

        List<GraphSectors.Sector> searchingSectors = sectors.sectorsInArea(point, searchDistance);

        for (GraphSectors.Sector currenSector : searchingSectors){
            for(int i = currenSector.startNodeId(); i < currenSector.endNodeId(); ++i ){
                double currentDistance = point.squaredDistanceTo(nodePoint(i));
                if(currentDistance < minDistance){
                    minDistance = currentDistance;
                    nodeId = i;
                }
            }
        }

        if(Math.sqrt(minDistance) > searchDistance)
            return -1;
        return nodeId;

    }

    /**
     * retourne l'identité du nœud destination de l'arête d'identité donnée
     * @param edgeId
     * @return int
     */
    public int edgeTargetNodeId(int edgeId){
        return edges.targetNodeId(edgeId);
    }

    /**
     * retourne vrai ssi l'arête d'identité donnée va dans le sens contraire de la voie OSM dont elle provient
     * @param edgeId
     * @return boolean
     */
    public boolean edgeIsInverted(int edgeId){
        return edges.isInverted(edgeId);
    }

    /**
     * retourne l'ensemble des attributs OSM attachés à l'arête d'identité donnée
     * @param edgeId
     * @return AttributeSet
     */
    public AttributeSet edgeAttributes(int edgeId){
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     * retourne la longueur de l'arête d'identité donnée
     * @param edgeId
     * @return longueur (en mètres)
     */
    public double edgeLength(int edgeId){
        return edges.length(edgeId);
    }

    /**
     * retourne le dénivelé positif total de l'arête d'identité donnée
     * @param edgeId
     * @return dénivelé (en mètres)
     */
    public double edgeElevationGain(int edgeId){
        return edges.elevationGain(edgeId);
    }

    /**
     * retourne le profil en long de l'arête d'identité donnée, sous la forme d'une fonction
     * @param edgeId
     * @return profil de l'arête d'identité donnée
     */
    public DoubleUnaryOperator edgeProfile(int edgeId){
        if(!edges.hasProfile(edgeId))
            return Functions.constant(Double.NaN);
        return Functions.sampled(edges.profileSamples(edgeId), edges.length(edgeId));
    }
}
