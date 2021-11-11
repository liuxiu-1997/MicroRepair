package us.msu.cse.repair.toolsExpression;

import java.util.BitSet;
import java.util.Random;

public class RandCheck {
    public static void randCheck(BitSet bits, int size) {
        Random random = new Random();
        int flag = 0;
        int rand = random.nextInt(bits.size() + 1);//最大的数不会到达bit.size()+1
        for (int j = 0; j < size; j++) {
            if (bits.get(j)) {
                flag++;
                if (flag == 2)//一次只去修一个位置
                    break;
            }
        }
        while (flag != 2) {
            rand = random.nextInt(bits.size() + 1);
            bits.set(rand, true);
            flag++;
        }
    }

}
