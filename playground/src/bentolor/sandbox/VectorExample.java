package bentolor.sandbox;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;

import java.util.Arrays;

public class VectorExample {
    public static void main(String[] args) {
        classic();
        naiveVectorAPI();
        masked();
        crossLane();
    }

    private static void masked() {
        int[] a1 = {2, 49, -12, 3, 11, 32, 11, 455, 0, 283};
        int[] a2 = {0, -3, 185, 221, 76, 2, -2, 0, 0, 0};
        int[] aResult = new int[a1.length];
        int[] vResult = new int[a1.length];

        // Classic approach
        for (int i = 0; i < a1.length; i++) {
            aResult[i] = a1[i] + a2[i];
        }

        var species = IntVector.SPECIES_PREFERRED;

        for (int i = 0; i < a1.length; i += species.length()) {
            var mask = species.indexInRange(i, a1.length);
            var v1 = IntVector.fromArray(species, a1, i, mask);
            var v2 = IntVector.fromArray(species, a2, i, mask);
            var result = v1.add(v2);
            result.intoArray(vResult, i, mask);
        }

        System.out.println(Arrays.equals(aResult, vResult) ? "Yep!" : "Duh!");
    }

    private static void crossLane() {
        int[] a1 = {2, 49, -12, 3, 11, 32, 11, 455, 0, 283};
        var species = IntVector.SPECIES_PREFERRED;

        double sqrSums = 0d;
        for (int i = 0; i < a1.length; i += species.length()) {
            var mask = species.indexInRange(i, a1.length);
            var v1 = IntVector.fromArray(species, a1, i, mask);
            var v2 = v1.mul(v1, mask);
            sqrSums += v2.reduceLanes(VectorOperators.ADD, mask);
        }
        double vBetrag = Math.sqrt(sqrSums);
    }



    public static void classic() {
        int[] a1 = {2, 49, -12, 3, 11, 32, 11, 455, 0, 283};
        int[] a2 = {0, -3, 185, 221, 76, 2, -2, 0, 0, 0};
        int[] aResult = new int[a1.length];

        // Classic approach
        for (int i = 0; i < a1.length; i++) {
            aResult[i] = a1[i] + a2[i];
        }

        System.out.println(aResult[1] == 46 ? "Yep!" : "Duh!");
    }


    public static void naiveVectorAPI() {
        int[] a1 = {2, 49, -12, 3, 11, 32, 11, 455, 0, 283};
        int[] a2 = {0, -3, 185, 221, 76, 2, -2, 0, 0, 0};
        int[] aResult = new int[a1.length];
        int[] vResult = new int[a1.length];

        // Classic approach
        for (int i = 0; i < a1.length; i++) {
            aResult[i] = a1[i] + a2[i];
        }

        var species = IntVector.SPECIES_256;

        // Vector API
        var v1 = IntVector.fromArray(species, a1, 0);
        var v2 = IntVector.fromArray(species, a2, 0);

        var result = v1.add(v2);
        result.intoArray(vResult, 0);

        System.out.println(Arrays.equals(aResult, vResult) ? "Yep!" : "Duh!");
    }

}
