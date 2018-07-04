package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.DatabaseTable;
import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class GameFragment extends Fragment {

    public enum Phase {
        ONE, TWO, OVER,
    }

    public static final String GAME_NAME = "Scroggle";
    public static final int SIZE = 3;
    public static final int N = 9;
    public static final int STARTING_SCORE = 0;
    public static final int SECONDS_PER_PHASE = 90;
    public static final int TIME_REMINDER = 15;

    private static final long MILLIS_PER_PHASE = SECONDS_PER_PHASE * 1000;
    private static final long MILLIS_PER_TICK = 1000L;
    private static final int[] LARGE_IDS = {R.id.large1, R.id.large2, R.id.large3,
            R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8,
            R.id.large9,};
    private static final int[] SMALL_IDS = {R.id.small1, R.id.small2, R.id.small3,
            R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8,
            R.id.small9,};

    private GameActivity activity;
    private CountDownTimer timer;
    private Random random;
    private List<int[]> validArrangements;

    // Below are fields relevant to game state
    private String[] words;
    private Tile board;
    private GameResult result;
    private long time;
    private Phase phase;
    private Stack<LetterTile> current;

    private int mSoundClick, mSoundEarnPoints, mSoundLosePoints;
    private SoundPool mSoundPool;
    private float mVolume = 1f;
    private DatabaseTable dbTable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(GAME_NAME, "start onCreate GameFragment");
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        initGame();
        Log.d(GAME_NAME, "finish onCreate GameFragment");

        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundClick = mSoundPool.load(getActivity(), R.raw.sergenious_movex, 1);
        mSoundEarnPoints = mSoundPool.load(getActivity(), R.raw.oldedgar_winner, 1);
        mSoundLosePoints = mSoundPool.load(getActivity(), R.raw.erkanozan_miss, 1);
    }

    public void initGame() {
        Log.d(GAME_NAME, "init game");
        activity = (GameActivity) getActivity();
        random = new Random();
        validArrangements = getAllValidArrangement(new ArrayList<int[]>());
        initWords();
        initBoard();
        result = new GameResult(activity
                .getSharedPreferences(GameActivity.PREF_NAME, Context.MODE_PRIVATE)
                .getString(GameActivity.PREF_USERNAME, null));
        time = MILLIS_PER_PHASE;
        startTimer(MILLIS_PER_PHASE);
        phase = Phase.ONE;
        Log.d(GAME_NAME, "starting phase1");
        current = new Stack<>();
    }

    private void initWords() {
        words = new String[N];
        dbTable = new DatabaseTable(activity);
        List<String> wordsLengthNine = dbTable.getWordsByLength(N);
        for (int i = 0; i < N; i++) {
            words[i] = wordsLengthNine.get(random.nextInt(wordsLengthNine.size()));
            Log.v("GameFragment", words[i]);
        }
        for (int i = 0; i < N; i++) {
            String word = words[i];
            int[] arrangement = getRandomArrangement();
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < word.length(); j++) {
                sb.append(word.charAt(arrangement[j]));
            }
            words[i] = sb.toString();
        }
    }

    private int[] getRandomArrangement() {
        return validArrangements.get(random.nextInt(validArrangements.size()));
    }

    private List<int[]> getAllValidArrangement(List<int[]> result) {
        int[][] board = new int[SIZE][SIZE];
        int[] nums = new int[N];
        for (int i = 0; i < N; i++) {
            nums[i] = i;
        }
        for (int[] row : board) {
            Arrays.fill(row, -1);
        }
        int x = random.nextInt(SIZE);
        int y = random.nextInt(SIZE);
        dfs(nums, x, y, 0, board, result);
        return result;
    }

    private void dfs(int[] nums, int x, int y, int index, int[][] board,
                     List<int[]> result) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE || board[x][y] != -1) {
            return;
        }
        board[x][y] = nums[index];
        if (index == nums.length - 1) {
            int[] newBoard = new int[SIZE * SIZE];
            for (int i = 0; i < SIZE; i++) {
                System.arraycopy(board[i], 0, newBoard, SIZE * i, SIZE);
            }
            result.add(newBoard);
            board[x][y] = -1;
            return;
        }
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (!(i == 0 && j == 0)) {
                    dfs(nums, x + i, y + j, index + 1, board, result);
                }
            }
        }
        board[x][y] = -1;
    }

    private void initBoard() {
        Tile[] largeTiles = new Tile[N];
        LetterTile[][] smallTiles = new LetterTile[N][N];
        board = new Tile(-1, null, largeTiles);
        for (int i = 0; i < N; i++) {
            largeTiles[i] = new Tile(i, board, smallTiles[i]);
            for (int j = 0; j < N; j++) {
                smallTiles[i][j] = new LetterTile(j, largeTiles[i], words[i].charAt(j));
            }
        }
    }

    private void startTimer(long total) {
        if (timer != null) timer.cancel();
        timer = new CountDownTimer(total, MILLIS_PER_TICK) {
            public void onTick(long millisUntilFinished) {
                updateTime(millisUntilFinished);
            }

            public void onFinish() {
                updateTime(0L);
                if (phase == Phase.ONE) {
                    moveToPhase2();
                } else {
                    gameOver();
                }
            }
        };
        timer.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(GAME_NAME, "start onCreateView GameFragment");
        View rootView = inflater.inflate(R.layout.large_board, container, false);
        initViews(rootView);
        updateBoardView();
        Log.d(GAME_NAME, "finish onCreateView GameFragment");
        return rootView;
    }

    private void initViews(View rootView) {
        Log.d(GAME_NAME, "init views");
        board.setView(rootView);
        Tile[] largeTiles = board.getSubTiles();
        for (int i = 0; i < N; i++) {
            View largeView = rootView.findViewById(LARGE_IDS[i]);
            largeTiles[i].setView(largeView);

            Tile[] smallTiles = largeTiles[i].getSubTiles();
            for (int j = 0; j < N; j++) {
                Button smallView = largeView.findViewById(SMALL_IDS[j]);
                final LetterTile letter = (LetterTile) smallTiles[j];
                letter.setView(smallView);
                smallView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickLetter(letter);
                    }
                });
            }
        }
    }

    private void updateBoardView() {
        Tile[] largeTiles = board.getSubTiles();
        for (int large = 0; large < N; large++) {
            Tile[] smallTiles = largeTiles[large].getSubTiles();
            for (int small = 0; small < N; small++) {
                ((LetterTile) smallTiles[small]).updateDrawableState();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        if (phase == Phase.OVER) {
            Log.d(GAME_NAME, "delete game data");
            activity.getSharedPreferences(GameActivity.PREF_NAME, Context.MODE_PRIVATE).edit()
                    .remove(GameActivity.PREF_RESTORE).commit();
        } else {
            String gameData = getState();
            Log.d(GAME_NAME, "save game data: " + gameData);
            activity.getSharedPreferences(GameActivity.PREF_NAME, Context.MODE_PRIVATE).edit()
                    .putString(GameActivity.PREF_RESTORE, gameData).commit();
        }
        activity.finish();
    }

    // Serialize the game state into a string
    private String getState() {
        StringBuilder sb = new StringBuilder();
        for (String word: words) {
            sb.append(word).append(",");
        }
        for (Tile largeTile: board.getSubTiles()) {
            for (Tile smallTile: largeTile.getSubTiles()) {
                LetterTile letter = (LetterTile) smallTile;
                sb.append(letter).append(",");
            }
        }
        sb.append(result.toStateString()).append(",");
        sb.append(time).append(",");
        sb.append(phase).append(",");
        for (LetterTile letter: current) {
            sb.append(letter.getSuperTile().getIndex()).append(",");
            sb.append(letter.getIndex()).append(",");
        }
        return sb.toString();
    }

    /**
     * Restores run-time game state from stateStr.
     * @param stateStr serialized game state
     */
    public void restoreState(String stateStr) {
        Log.d(GAME_NAME, "restore game data: " + stateStr);
        String[] state = stateStr.split(",");
        int index = 0;
        words = new String[N];
        for (int i = 0; i < words.length; i++) {
            words[i] = state[index++];
        }
        for (int i = 0; i < N; i++) {
            Tile largeTile = board.getSubTile(i);
            for (int j = 0; j < N; j++) {
                LetterTile letter = (LetterTile) largeTile.getSubTile(j);
                letter.setLetter(words[i].charAt(j));
                letter.setState(state[index++]);
            }
        }
        updateBoardView();
        result = GameResult.valueOf(state[index++]);
        updateScore();
        updateTime(Long.parseLong(state[index++]));
        startTimer(time);
        phase = Phase.valueOf(state[index++]);
        current = new Stack<>();
        while (index + 1 < state.length) {
            int i = Integer.parseInt(state[index++]);
            int j = Integer.parseInt(state[index++]);
            pushLetter((LetterTile) board.getSubTile(i).getSubTile(j));
        }
    }

    // Called when the player click on the letter.
    private void onClickLetter(LetterTile letter) {
        if (!current.isEmpty() && current.peek() == letter) {
            Log.d(GAME_NAME, "unselect " + letter.getLetter());
            popLetter();
            if (!current.contains(letter)) letter.unselect();
        } else if (isValidMove(letter)) {
            mSoundPool.play(mSoundClick, mVolume, mVolume, 1, 0, 1f);
            Log.d(GAME_NAME, "select " + letter.getLetter());
            letter.select();
            pushLetter(letter);
        }
        updateBoardView();
    }

    private boolean isValidMove(LetterTile letter) {
        if (!letter.isAvailable(phase)) {
            return false;
        }
        if (current.isEmpty()) return true;
        LetterTile lastLetter = current.peek();
        if (phase == Phase.ONE) {
            return lastLetter.isConnectedTo(letter);
        } else {
            return lastLetter.getSuperTile().isConnectedTo(letter.getSuperTile());
        }
    }

    /**
     * Called when the play click on the select button.
     */
    public void onSelectWord() {
        if (current.isEmpty()) return;

        String word = getSelectedWord();
        int score = GameUtils.calculateScore(dbTable, word);
        if (score > 0) {
            Toast.makeText(activity, getString(R.string.toast_find_valid_word, word),
                    Toast.LENGTH_LONG).show();
            mSoundPool.play(mSoundEarnPoints, mVolume, mVolume, 1, 0, 1f);
        } else {
            Toast.makeText(activity, getString(R.string.toast_find_invalid_word, word),
                    Toast.LENGTH_LONG).show();
            mSoundPool.play(mSoundLosePoints, mVolume, mVolume, 1, 0, 1f);
        }

        result.addWord(word, score);
        updateScore();
        Log.d(GAME_NAME, "select word: " + word + " (" + score + " points)");

        if (phase == Phase.ONE) {
            closeAllSubTiles(current.peek().getSuperTile());
            if (score < 0) {
                for (LetterTile letter: current) {
                    letter.close();
                }
            }
        } else {
            closeAllSubTiles(board);
        }
        updateBoardView();
        clearCurrent();

        if (phase == Phase.ONE) {
            if (!board.isAvailable(phase)) {
                moveToPhase2();
            }
        } else {
            gameOver();
        }
    }

    private void closeAllSubTiles(Tile tile) {
        for (Tile subTile: tile.getSubTiles()) {
            if (subTile instanceof LetterTile) {
                LetterTile letter = (LetterTile) subTile;
                if (letter.isUnselected()) {
                    letter.close();
                }
            } else {
                closeAllSubTiles(subTile);
            }
        }
    }

    private String getSelectedWord() {
        StringBuilder sb = new StringBuilder();
        for (LetterTile tile: current) {
            sb.append(tile.getLetter());
        }
        return sb.toString();
    }

    private void updateScore() {
        activity.updateScore(result.finalScore.intValue());
    }

    private void updateTime(long millisUntilFinished) {
        time = millisUntilFinished;
        activity.updateTime((int) (time / 1000));
    }

    private void updateCurrentWord() {
        StringBuilder sb = new StringBuilder();
        for (LetterTile letter: current) {
            sb.append(letter.getLetter());
        }
        activity.updateCurrentWord(sb.toString());
    }

    private void pushLetter(LetterTile letter) {
        current.push(letter);
        updateCurrentWord();
    }

    private LetterTile popLetter() {
        LetterTile letter = current.pop();
        updateCurrentWord();
        return letter;
    }

    private void clearCurrent() {
        current.clear();
        updateCurrentWord();
    }

    private void moveToPhase2() {
        timer.cancel();

        while (!current.isEmpty()) {
            popLetter().unselect();
        }

        for (Tile largeTile: board.getSubTiles()) {
            for (Tile smallTile: largeTile.getSubTiles()) {
                LetterTile letter = (LetterTile) smallTile;
                if (letter.isSelected()) {
                    letter.unselect();
                } else {
                    letter.close();
                }
            }
        }
        updateBoardView();

        phase = Phase.TWO;
        if (!board.isAvailable(phase)) {
            gameOver();
            return;
        }
        Toast.makeText(activity, R.string.toast_move_to_phase_two, Toast.LENGTH_LONG).show();
        Log.d(GAME_NAME, "starting phase2");
        startTimer(MILLIS_PER_PHASE);
    }

    private void gameOver() {
        phase = Phase.OVER;
        timer.cancel();
        Log.d(GAME_NAME, "game over");
        ((GameActivity) getActivity()).showResult(result);
    }
}
