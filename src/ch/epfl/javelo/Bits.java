package ch.epfl.javelo;

/**
 * opération sur les bits
 *
 * @author Evan Massonnet (346642)
 */

public final class Bits {

    private Bits(){
    }

    /**
     * extrait du vecteur de 32 bits value la plage de length bits commençant au bit d'index start (valeur signée en complément à deux)
     * @param value
     * @param start
     * @param length
     * @return int
     */
    public static int extractSigned(int value, int start, int length){
        if(start < 0 || length < 0 || start + length > Integer.SIZE){
            throw new IllegalArgumentException();
        }
        value = value << (Integer.SIZE - (start + length));
        value = value  >> (Integer.SIZE - length);
        return value;
    }

    /**
     * extrait du vecteur de 32 bits value la plage de length bits commençant au bit d'index start (valeur non signée)
     * @param value
     * @param start
     * @param length
     * @return int
     */
    public static int extractUnsigned(int value, int start, int length){

        if(start < 0 || length < 0 || start + length > Integer.SIZE){
            throw new IllegalArgumentException();
        }
        if(length == Integer.SIZE){
            throw new IllegalArgumentException();
        }

        value = value << (Integer.SIZE - (start + length));
        value = value  >>> (Integer.SIZE - length);
        return value;
    }

}
