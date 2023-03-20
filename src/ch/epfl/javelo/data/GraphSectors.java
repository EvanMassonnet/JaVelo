package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * représente le tableau contenant les 16384 secteurs de JaVelo
 *
 * @author Evan Massonnet (346642)
 */
public record GraphSectors(ByteBuffer buffer) {

    private static final int OFFSET_SECTOR_INDEX = 0;
    private static final int OFFSET_SECTOR_SIZE = OFFSET_SECTOR_INDEX + Integer.BYTES;
    private static final int SECTOR_INTS =  OFFSET_SECTOR_SIZE + Short.BYTES;

    private static final double SWISS_WIDTH = 349_000;
    private static final double SWISS_HEIGHT = 221_000;

    private static final int SUBDIVISIONS_PER_SIDE = 128;
    private static final double SECTOR_WIDTH = SWISS_WIDTH / SUBDIVISIONS_PER_SIDE;
    private static final double SECTOR_HEIGHT = SWISS_HEIGHT / SUBDIVISIONS_PER_SIDE;

    private final static int SECTOR_SIDE = 128;


    public record Sector(int startNodeId, int endNodeId){
        //int startNodeId, l'identité (index) du premier nœud du secteur
        //int endNodeId, l'identité (index) du nœud situé juste après le dernier nœud du secteur.
    }


    /**
     * retourne la liste de tous les secteurs ayant une intersection avec le carré centré
     * au point donné et de côté égal au double (!) de la distance donnée
     * @param center
     * @param distance
     * @return liste de tous les secteurs
     */
    public List<Sector> sectorsInArea(PointCh center, double distance){
        ArrayList<Sector> result = new ArrayList<>();

        double xMin = Math2.clamp(SwissBounds.MIN_E, center.e() - distance, SwissBounds.MAX_E);
        double xMax = Math2.clamp(SwissBounds.MIN_E, center.e() + distance, SwissBounds.MAX_E-1);
        double yMin = Math2.clamp(SwissBounds.MIN_N, center.n() - distance, SwissBounds.MAX_N);
        double yMax = Math2.clamp(SwissBounds.MIN_N, center.n() + distance, SwissBounds.MAX_N-1);

        int upRightCorner = currentSector(new PointCh(xMax, yMax));
        int downLeftCorner = currentSector(new PointCh(xMin, yMin));
        int downRightCorner = currentSector(new PointCh(xMax, yMin));


        for(int j = 0; j < (upRightCorner - downRightCorner) / SECTOR_SIDE + 1; ++j){
            for(int i = downLeftCorner; i < downRightCorner +1 ; ++i){
                int bufferInt = (i + j * SECTOR_SIDE) * SECTOR_INTS;
                if(bufferInt <= 98298){
                    int startNodeId = buffer.getInt(bufferInt);
                    int endNodeId = buffer.getInt(bufferInt) + Short.toUnsignedInt(buffer.getShort((i+ j * SECTOR_SIDE) * SECTOR_INTS + OFFSET_SECTOR_SIZE));
                    result.add(new Sector(startNodeId, endNodeId));
                }
            }
        }

        return result;
    }

    /**
     * Retorune le sector du point donné en parametre
     * @param center
     * @return int
     */
    private int currentSector(PointCh center){
        double x = (center.e() - SwissBounds.MIN_E) / SECTOR_WIDTH;
        double y = (center.n() - SwissBounds.MIN_N) / SECTOR_HEIGHT;

        return (int)x + SECTOR_SIDE * (int)y;
    }

}
