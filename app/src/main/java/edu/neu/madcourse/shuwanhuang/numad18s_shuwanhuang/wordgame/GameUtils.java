package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.DatabaseTable;

public class GameUtils {

    private static final int[] SCORES = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3,
            1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10,};
    private static final int BONUS = 20;
    private static final int PENALTY = -5;

    public static int calculateScore(DatabaseTable dbTable, String word) {
        if (!dbTable.containsWord(word)) {
            return PENALTY;
        }
        char[] chs = word.toCharArray();
        int score = 0;
        for (char c : chs) {
            score += SCORES[c - 'a'];
        }
        if (word.length() == GameFragment.N) {
            score += BONUS;
        }
        return score;
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

    public static String toResultString(GameResult result, boolean useUsername) {
        StringBuilder sb = new StringBuilder();
        if (useUsername) {
            sb.append(result.username).append(" won ");
        }
        sb.append(result.finalScore).append(" points\n");
        sb.append("at ").append(result.dateTime);
        if (result.words != null && result.words.size() > 0) {
            sb.append("\n");
            int index = 0;
            for (int i = 0; i < result.scores.size(); i++) {
                if (result.scores.get(i) > result.scores.get(index))
                    index = i;
            }
            sb.append("Best word: " + result.words.get(index));
            sb.append(" (" + result.scores.get(index) + " points)");
        }
        return sb.toString();
    }
}
