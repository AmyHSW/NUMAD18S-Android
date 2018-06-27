package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.app.Fragment;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.DatabaseTable;
import edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.R;

public class GameFragment extends Fragment {

    private static final String GAME = "Scroggle";
    private static final int N = 9;
    private static final int largeIds[] = {R.id.large1, R.id.large2, R.id.large3,
            R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8,
            R.id.large9,};
    private static final int smallIds[] = {R.id.small1, R.id.small2, R.id.small3,
            R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8,
            R.id.small9,};

    public enum Phase {
        ONE, TWO
    }

    private String[] words;
    private Tile board;
    private Phase phase;
    private Stack<LetterTile> current;
    private int score;

    // TODO Sound
    private int mSoundX, mSoundO, mSoundMiss, mSoundRewind;
    private SoundPool mSoundPool;
    private float mVolume = 1f;
    private DatabaseTable dbTable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: What is this? Retain this fragment across configuration changes.
        setRetainInstance(true);
        initGame();
        // TODO: Sound. What is this?
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
//        mSoundX = mSoundPool.load(getActivity(), R.raw.sergenious_movex, 1);
//        mSoundO = mSoundPool.load(getActivity(), R.raw.sergenious_moveo, 1);
//        mSoundMiss = mSoundPool.load(getActivity(), R.raw.erkanozan_miss, 1);
//        mSoundRewind = mSoundPool.load(getActivity(), R.raw.joanne_rewind, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.large_board, container, false);
        initViews(rootView);
        updateAllViews();
        return rootView;
    }

    public void initGame() {
        Log.d(GAME, "init game");

        initWords();

        // Create all the tiles
        Tile[] largeTiles = new Tile[N];
        Tile[][] smallTiles = new LetterTile[N][N];
        board = new Tile(this, -1, null, largeTiles);
        for (int large = 0; large < N; large++) {
            largeTiles[large] = new Tile(this, large, board, smallTiles[large]);
            for (int small = 0; small < N; small++) {
                smallTiles[large][small] =
                        new LetterTile(this, small, largeTiles[large], words[large].charAt(small));
            }
        }

        Log.d(GAME, "starting phase1");
        phase = Phase.ONE;
        current = new Stack<>();
        score = 0;
    }

    private void initViews(View rootView) {
        Log.d(GAME, "init views");
        board.setView(rootView);
        Tile[] largeTiles = board.getSubTiles();
        for (int large = 0; large < N; large++) {
            View largeView = rootView.findViewById(largeIds[large]);
            largeTiles[large].setView(largeView);

            LetterTile[] smallTiles = (LetterTile[]) largeTiles[large].getSubTiles();
            for (int small = 0; small < N; small++) {
                Button smallView = largeView.findViewById(smallIds[small]);
                final LetterTile letter = smallTiles[small];
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

    private void onClickLetter(LetterTile letter) {
        if (!current.isEmpty() && current.peek() == letter) {
            Log.d(GAME, "unselect " + letter.getLetter());
            current.pop().unselect();
        } else if (isValidMove(letter)) {
            Log.d(GAME, "select " + letter.getLetter());
            letter.select();
            current.push(letter);
        }
        updateAllViews();
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

    private void onSelectWord() {
        if (current.isEmpty()) return;
        if (phase == Phase.ONE) {
            closeSubTiles(current.peek().getSuperTile());
        } else {
            closeSubTiles(board);
        }
        String word = getSelectedWord();
        Log.d(GAME, "select word: " + word);
        updateScore(word);
        current.clear();
        updateAllViews();
        if (phase == Phase.ONE) {
            if (!board.isAvailable(phase)) {
                onMoveToPhase2();
            }
        } else {
            onGameOver();
        }

    }

    private void closeSubTiles(Tile superTile) {
        for (Tile tile: superTile.getSubTiles()) {
            if (tile instanceof LetterTile) {
                LetterTile letter = (LetterTile) tile;
                if (letter.isUnselected()) {
                    letter.close();
                }
            } else {
                closeSubTiles(tile);
            }
        }
    }

    private void updateScore(String word) {
        score += GameUtils.calculateScore(word);
    }

    private String getSelectedWord() {
        StringBuilder sb = new StringBuilder();
        for (LetterTile tile: current) {
            sb.append(tile.getLetter());
        }
        return sb.toString();
    }

    private void onMoveToPhase2() {
        // TODO: if timer1 is not off, turn it off

        Log.d(GAME, "starting phase2");
        phase = Phase.TWO;
        for (LetterTile letter: current) {
            letter.unselect();
        }
        current.clear();

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
        updateAllViews();
        if (!board.isAvailable(phase)) {
            onGameOver();
        }
    }

    private void onGameOver() {
        Log.d(GAME, "game over");
        //TODO: game over
    }

//    public void restartGame() {
//        mSoundPool.play(mSoundRewind, mVolume, mVolume, 1, 0, 1f);
//        // ...
//        initGame();
//        initViews(getView());
//        updateAllViews();
//    }

    private void initWords() {
        words = new String[N];
        dbTable = new DatabaseTable(getActivity());
        List<String> wordsLengthNine = dbTable.getWordsByLength(N);
        Random random = new Random();
        for (int i = 0; i < N; i++) {
            words[i] = wordsLengthNine.get(random.nextInt(wordsLengthNine.size()));
            // TODO: arrange
        }
    }

    private void updateAllViews() {
        Tile[] largeTiles = board.getSubTiles();
        for (int large = 0; large < N; large++) {
            Tile[] smallTiles = largeTiles[large].getSubTiles();
            for (int small = 0; small < N; small++) {
                ((LetterTile) smallTiles[small]).updateDrawableState();
            }
        }
    }

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
