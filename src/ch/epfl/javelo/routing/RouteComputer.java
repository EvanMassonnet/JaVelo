package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * représente un planificateur d'itinéraire
 *
 * @author Evan Massonnet (346642)
 */

public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

    public RouteComputer(Graph graph, CostFunction costFunction){
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * retourne l'itinéraire de coût total minimal allant du nœud d'identité startNodeId au nœud
     * d'identité endNodeId dans le graphe passé au constructeur, ou null si aucun itinéraire n'existe.
     * Si le nœud de départ et d'arrivée sont identiques, lève IllegalArgumentException
     * @param startNodeId noeud de depart
     * @param endNodeId noeud d'arriver
     * @return Route
     * @throws IllegalArgumentException si le noeud de depart est le meme que le noeud d'arriver
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId){
        PointCh endPoint = graph.nodePoint(endNodeId);

        Preconditions.checkArgument(startNodeId != endNodeId);

        float[] distance = new float[graph.nodeCount()];
        int[] predecessor = new int[graph.nodeCount()];

        Arrays.fill(distance, Float.POSITIVE_INFINITY);

        distance[startNodeId] = 0;
        WeightedNode firstNode = new WeightedNode(startNodeId, 0, 0);

        PriorityQueue<WeightedNode> exploring = new PriorityQueue<>();
        exploring.add(firstNode);

        while(!exploring.isEmpty()){

            WeightedNode currentNode = exploring.remove();
            int currentNodeId = currentNode.nodeId();

            //on a trouvé le point d'arriver, on construit la route
            if(currentNode.nodeId() == endNodeId){
                return constructRoute(startNodeId, endNodeId, predecessor);
            }

            if(currentNode.distanceToStart != Float.NEGATIVE_INFINITY){
                int counter = graph.nodeOutDegree(currentNodeId);
                for(int i = 0; i < counter; ++i){
                    int edgeId = graph.nodeOutEdgeId(currentNodeId, i);
                    int nextNodeId = graph.edgeTargetNodeId(edgeId);
                    double  factor = costFunction.costFactor(currentNodeId, edgeId);
                    float nextDistance = currentNode.distanceToStart + (float)(graph.edgeLength(edgeId) * factor);

                    if(nextDistance < distance[nextNodeId]){
                        distance[nextNodeId] = nextDistance;
                        predecessor[nextNodeId] = currentNodeId;
                        exploring.add(new WeightedNode(nextNodeId, nextDistance, (float)graph.nodePoint(nextNodeId).distanceTo(endPoint)));
                    }
                }
                distance[currentNodeId] = Float.NEGATIVE_INFINITY;
            }


        }

        return null;
    }


    /**
     * Représente un node utiliser par bestRouteBetween
     * Contient la distance par rapport au point de départ en fonction du chemin
     * et la distance par rapport au point d'arriver a vole d'oiseau
     */
    record WeightedNode(int nodeId, float distanceToStart, float distanceToEnd) //minimiser la somme
            implements Comparable<WeightedNode> {
        @Override
        public int compareTo(WeightedNode that) {
            return Float.compare(this.distanceToStart + this.distanceToEnd, that.distanceToStart + that.distanceToEnd);
        }
    }


    /**
     * Methode pour construire Route a partir du tableau des predecesseur de bestRouteBetween
     * @param startNode noeud de depart
     * @param endNode noeud d'arriver
     * @param predecessor
     * @return la route entre startNode et endNode
     */
    private Route constructRoute(int startNode, int endNode, int[] predecessor){

        List<Edge> edges = new ArrayList<>();
        int currentNodeId = endNode;

        while(currentNodeId != startNode){

            int edgeId = 0;
            for(int i = 0; i < graph.nodeOutDegree(predecessor[currentNodeId]); ++i){
                if(graph.edgeTargetNodeId(graph.nodeOutEdgeId(predecessor[currentNodeId], i)) == currentNodeId)
                    edgeId = graph.nodeOutEdgeId(predecessor[currentNodeId], i);
            }
            Edge edge = Edge.of(graph, edgeId, predecessor[currentNodeId], currentNodeId);
            edges.add(0, edge);
            currentNodeId = predecessor[currentNodeId];
        }

        return new SingleRoute(edges);
    }
}
