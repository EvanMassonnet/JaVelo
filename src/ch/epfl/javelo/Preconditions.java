package ch.epfl.javelo;
/**
 *Verification de précondition
 *
 * @author Evan Massonnet (346642)
 */

public final class Preconditions {
    private Preconditions(){
    }

    /**
     * Methode qui lève une exception IllegalArgumentException si son arguement est faux
     * @param shouldBeTrue boolean
     * @throws IllegalArgumentException si shouldBeTrue est faux
     */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue)
            throw new IllegalArgumentException();
    }
}
