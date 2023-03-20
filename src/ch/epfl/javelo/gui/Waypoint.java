package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * représente un point de passage dans le systeme de coordonnees suisse
 *
 * @author Evan Massonnet (346642)
 */

public record Waypoint(PointCh pathPoint, int nodeId) {

    // PointCh pathPoint : la position du point de passage dans le système de coordonnées suisse,
    // int nodeId : l'identité du nœud JaVelo le plus proche de ce point de passage.
}
