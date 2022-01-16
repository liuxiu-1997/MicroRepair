package us.msu.cse.repair.toolsExpression;

import java.util.BitSet;
import java.util.Random;

public class RandCheck {
    public static void randCheck(BitSet bits, int size) {
        Random random = new Random();
        int rand = random.nextInt(bits.size() + 1);//最大的数不会到达bit.size()+1
        for (int j = 0; j < size; j++) {
           bits.set(j,false);
        }
        bits.set(rand,true);
//        if (rand > (bits.size()/2)) {
            rand = random.nextInt(bits.size() + 1);
            bits.set(rand, true);
//        }
    }
}
