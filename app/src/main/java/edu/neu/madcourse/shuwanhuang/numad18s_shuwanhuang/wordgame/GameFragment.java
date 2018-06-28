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

import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.DatabaseTable;
import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.R;

import java.util.List;
import java.util.Random;
import java.util.Stack;

public class GameFragment extends Fragment {

    public enum Phase {
        ONE, TWO, OVER,
    }

    public static final String GAME_NAME = "Scroggle";
    public static final int N = 9;
    public static final int STARTING_SCORE = 0;
    public static final int SECONDS_PER_PHASE = 30; // TODO: change to 90

    private static final long MILLIS_PER_PHASE = SECONDS_PER_PHASE * 1000;
    private static final long MILLIS_PER_TICK = 1000L;
    private static final int[] LARGE_IDS = {R.id.large1, R.id.large2, R.id.large3,
            R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8,
            R.id.large9,};
    private static final int[] SMALL_IDS = {R.id.small1, R.id.small2, R.id.small3,
            R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8,
            R.id.small9,};

    private GameActivity activity;
    private String[] words;
    private Tile board;
    private Stack<LetterTile> current;
    private int score;
    private CountDownTimer timer;
    private Phase phase;

    // TODO Sound & DB
    private int mSoundX, mSoundO, mSoundMiss, mSoundRewind;
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

        // TODO: Sound, maybe move to somewhere else?
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
//        mSoundX = mSoundPool.load(getActivity(), R.raw.sergenious_movex, 1);
//        mSoundO = mSoundPool.load(getActivity(), R.raw.sergenious_moveo, 1);
//        mSoundMiss = mSoundPool.load(getActivity(), R.raw.erkanozan_miss, 1);
//        mSoundRewind = mSoundPool.load(getActivity(), R.raw.joanne_rewind, 1);
    }

    public void initGame() {
        Log.d(GAME_NAME, "init game");
        activity = (GameActivity) getActivity();
        initWords();
        initBoard();
        current = new Stack<>();
        score = 0;
        initTimer();
        phase = Phase.ONE;
        Log.d(GAME_NAME, "starting phase1");
        timer.start();
    }

    // TODO ?
    private void initWords() {
        words = new String[N];
        dbTable = new DatabaseTable(activity);
        List<String> wordsLengthNine = dbTable.getWordsByLength(N);
        Random random = new Random();
        for (int i = 0; i < N; i++) {
            words[i] = wordsLengthNine.get(random.nextInt(wordsLengthNine.size()));
            // TODO: arrange
        }
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

    private void initTimer() {
        timer = new CountDownTimer(MILLIS_PER_PHASE, MILLIS_PER_TICK) {
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
            Log.d(GAME_NAME, "save game data");
            activity.getSharedPreferences(GameActivity.PREF_NAME, Context.MODE_PRIVATE).edit()
                    .putString(GameActivity.PREF_RESTORE, getState()).commit();
        }
        activity.finish();
    }

    // Serialize the game state into a string
    private String getState() {
        return "dummy"; // TODO
    }

    /**
     * Restores run-time game state from stateStr.
     * @param stateStr serialized game state
     */
    public void restoreState(String stateStr) {
        
    }

    // Called when the player click on the letter.
    private void onClickLetter(LetterTile letter) {
        if (!current.isEmpty() && current.peek() == letter) {
            Log.d(GAME_NAME, "unselect " + letter.getLetter());
            current.pop().unselect();
        } else if (isValidMove(letter)) {
            Log.d(GAME_NAME, "select " + letter.getLetter());
            letter.select();
            current.push(letter);
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

        if (phase == Phase.ONE) {
            closeAllSubTiles(current.peek().getSuperTile());
        } else {
            closeAllSubTiles(board);
        }
        updateBoardView();

        String word = getSelectedWord();
        current.clear();
        int delta = GameUtils.calculateScore(dbTable, word);
        // TODO if delta>0, beep
        score += delta;
        updateScore(score);
        Log.d(GAME_NAME, "select word: " + word + " (" + delta + " points)");

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

    private void updateScore(int score) {
        activity.updateScore(score);
    }

    private void updateTime(long millisUntilFinished) {
        activity.updateTime((int) (millisUntilFinished / 1000));
    }

    private void moveToPhase2() {
        timer.cancel();

        while (!current.isEmpty()) {
            current.pop().unselect();
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
        Log.d(GAME_NAME, "starting phase2");
        timer.start();
    }

    private void gameOver() {
        phase = Phase.OVER;
        timer.cancel();
        Log.d(GAME_NAME, "game over");
        ((GameActivity) getActivity()).showScore(score);
    }

//    public void restartGame() {
//        mSoundPool.play(mSoundRewind, mVolume, mVolume, 1, 0, 1f);
//        // ...
//        initGame();
//        initViews(getView());
//        updateAllViews();
//    }

    /** Restore the state of the game from the given string. */
//    public void putState(String gameData) {
//        String[] fields = gameData.split(",");
//        int index = 0;
//        mLastLarge = Integer.parseInt(fields[index++]);
//        mLastSmall = Integer.parseInt(fields[index++]);
//        for (int large = 0; large < 9; large++) {
//            for (int small = 0; small < 9; small++) {
//                Tile.Owner owner = Tile.Owner.valueOf(fields[index++]);
//                mSmallTiles[large][small].setOwner(owner);
//            }
//        }
//        setAvailableFromLastMove(mLastSmall);
//        updateAllViews();
//    }
}
