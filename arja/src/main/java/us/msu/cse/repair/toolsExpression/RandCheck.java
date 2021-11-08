package us.msu.cse.repair.toolsExpression;

import java.util.BitSet;
import java.util.Random;

public class RandCheck {
    public static void randCheck(BitSet bits,int size){
        Random random =new Random();
        int flag=-1;
        int rand = random.nextInt(bits.size()+1);
        for (int j = 0 ;j < size ; j++) {
            if (bits.get(j)) {
                flag = 1;
            }
        }
        if (flag == -1) {
            for (int i = 0; i < bits.size(); ++i) {
                if (rand < bits.size() / 3) {
                    bits.set(i, true);
                } else {
                    bits.set(i, false);
                }
            }
        }
    }

}
