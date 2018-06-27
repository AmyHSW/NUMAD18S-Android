package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

public class GameUtils {

    public static int calculateScore(String word) {
        return 0; // TODO: HSW
    }

    public static boolean isConnected(int a, int b) {
        switch (a) {
            case 0: return b == 1 || b == 3 || b == 4;
            case 1: return b != 6 && b != 7 && b != 8;
            case 2: return b == 1 || b == 4 || b == 5;
            case 3: return b != 2 && b != 5 && b != 8;
            case 4: return true;
            case 5: return b != 0 && b != 3 && b != 6;
            case 6: return b == 3 || b == 4 || b == 7;
            case 7: return b != 0 && b != 1 && b != 2;
            case 8: return b == 4 || b == 5 || b == 7;
            default: return false;
        }
    }
}
