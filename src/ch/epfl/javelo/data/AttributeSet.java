package ch.epfl.javelo.data;

import java.util.StringJoiner;

/**
 * représente un ensemble d'attributs OpenStreetMap
 *
 * @author Evan Massonnet (346642)
 */

public record AttributeSet(long bits) {

    /**
     * Constructeur public de AttributeSet
     * @param bits
     * @throws IllegalArgumentException si bits < 0 ou > au nombre d'attributs
     */
    public AttributeSet{
        if(bits >= 1L << Attribute.COUNT || bits < 0){
            throw new IllegalArgumentException();
        }
    }

    /**
     * retourne un ensemble contenant uniquement les attributs donnés en argument
     * @param attributes
     * @return AttributeSet contenant les attributs
     */
    public static AttributeSet of(Attribute... attributes){
        long newBits = 0;
        for(Attribute a : attributes){
            newBits =  newBits | (long)Math.pow(2, a.ordinal());
        }
        return new AttributeSet(newBits);
    }

    /**
     * retourne vrai ssi l'ensemble récepteur (this) contient l'attribut donné
     * @param attribute
     * @return boolean
     */
    public boolean contains(Attribute attribute){
        long and = (long)Math.pow(2, attribute.ordinal()) & bits;
        if(and != 0)
            return true;
        return false;
    }

    /**
     * retourne vrai ssi l'intersection de l'ensemble récepteur (this)
     * avec celui passé en argument (that) n'est pas vide
     * @param that
     * @return boolean
     */
    public boolean intersects(AttributeSet that){
        return (that.bits & bits) != 0;
    }

    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",", "{", "}");
        for(Attribute attribute : Attribute.ALL){
            if(contains(attribute))
                j.add(attribute.toString());
        }
        return j.toString();
    }
}
