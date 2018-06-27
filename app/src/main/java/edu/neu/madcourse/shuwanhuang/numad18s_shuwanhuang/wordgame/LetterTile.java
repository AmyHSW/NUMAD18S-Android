package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.widget.Button;

public class LetterTile extends Tile {

    private enum State {
        UNSELECTED, SELECTED, CLOSED
    }

    private static final int LEVEL_UNSELECTED = 0;
    private static final int LEVEL_SELECTED = 1;
    private static final int LEVEL_CLOSED = 2;
    private final char letter;
    private State state;

    public LetterTile(GameFragment game, int index, Tile superTile, char letter) {
        super(game, index, superTile, null);
        this.letter = letter;
        this.state = State.UNSELECTED;
    }

    public char getLetter() {
        return letter;
    }

    @Override
    public boolean isAvailable(GameFragment.Phase phase) {
        if (phase == GameFragment.Phase.ONE) {
            return isUnselected();
        } else {
            return !isClosed();
        }
    }

    public boolean isUnselected() {
        return state == State.UNSELECTED;
    }

    public void unselect() {
        state = State.UNSELECTED;
    }

    public boolean isClosed() {
        return state == State.CLOSED;
    }

    public void close() {
        state = State.CLOSED;
    }

    public boolean isSelected() {
        return state == State.SELECTED;
    }

    public void select() {
        state = State.SELECTED;
    }

    public void updateDrawableState() {
        if (view == null) return;
        int level = getLevel();
        if (view instanceof Button) {
            Button button = (Button) view;
            if (level == LEVEL_CLOSED) {
                button.setText("");
            } else {
                button.setText(Character.toString(letter));
            }
            button.getBackground().setLevel(level);
        }
    }

    private int getLevel() {
        switch (state) {
            case UNSELECTED: return LEVEL_UNSELECTED;
            case SELECTED: return LEVEL_SELECTED;
            case CLOSED: return LEVEL_CLOSED;
            default: return LEVEL_UNSELECTED;
        }
    }
}
