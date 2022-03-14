package us.msu.cse.repair.toolsExpression;


public class LCS {

    public static int max(int i, int j) {
        return i > j ? i : j;
    }

    public static int longestSubSequence(char[] arr1, char[] arr2) {
        int m = arr1.length;
        int n = arr2.length;
        int[][] dpArr = new int[m + 1][n + 1];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (j == 0 || i == 0) {
                    dpArr[i][j] = 0;
                }
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (arr1[i - 1] == arr2[j - 1]) {
                    dpArr[i][j] = dpArr[i - 1][j - 1] + 1;
                } else {
                    dpArr[i][j] = max(dpArr[i - 1][j], dpArr[i][j - 1]);
                }
            }
        }
        return dpArr[m][n];
    }

    public static boolean acceptIngredient(String modificationStatement,String ingredientStatement) {
        int Num=longestSubSequence(modificationStatement.toCharArray(), ingredientStatement.toCharArray());
        return Num > (modificationStatement.toCharArray().length) / 10;
    }

}

