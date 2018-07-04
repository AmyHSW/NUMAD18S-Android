package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class GameResult {

    public String username;
    public Long finalScore;
    public List<String> words;
    public List<Long> scores;

    public String dateTime;  // set at the end of game
    public String bestWord;  // set at the end of game
    public Long bestWordScore;  // set at the end of game

    public GameResult() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public GameResult(String username) {
        this.username = username;
        this.finalScore = 0L;
        this.words = new ArrayList<>();
        this.scores = new ArrayList<>();
    }

    public void addWord(String word, int score) {
        finalScore += score;
        words.add(word);
        scores.add(Long.valueOf(score));
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("finalScore", finalScore);
        result.put("words", words);
        result.put("scores", scores);
        result.put("dateTime", dateTime);
        result.put("bestWord", bestWord);
        result.put("bestWordScore", bestWordScore);
        return result;
    }

    public String toStateString() {
        StringBuilder sb = new StringBuilder();
        sb.append(username).append("/");
        sb.append(finalScore).append("/");
        for (String word: words)
            sb.append(word).append("/");
        for (Long score: scores)
            sb.append(score).append("/");
        return sb.toString();
    }

    public static GameResult valueOf(String str) {
        GameResult result = new GameResult();
        String[] tokens = str.split("/");
        int index = 0;
        result.username = tokens[index++];
        result.finalScore = Long.parseLong(tokens[index++]);
        result.words = new ArrayList<>();
        result.scores = new ArrayList<>();
        int numWords = (tokens.length - index) / 2;
        for (int i = 0; i < numWords; i++)
            result.words.add(tokens[index++]);
        for (int i = 0; i < numWords; i++)
            result.scores.add(Long.parseLong(tokens[index++]));
        return result;
    }
}
