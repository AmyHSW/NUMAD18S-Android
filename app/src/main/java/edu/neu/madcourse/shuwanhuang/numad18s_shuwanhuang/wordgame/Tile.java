package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.view.View;

public class Tile {

    private final int index;
    private final Tile superTile;
    private final Tile[] subTiles;
    protected View view;

    Tile(int index, Tile superTile, Tile[] subTiles) {
      this.index = index;
      this.superTile = superTile;
      this.subTiles = subTiles;
    }

    public int getIndex() {
       return index;
    }

    public Tile getSuperTile() {
       return superTile;
    }

    public Tile[] getSubTiles() {
      return subTiles;
    }

    public View getView() {
       return view;
    }

    public void setView(View view) {
       this.view = view;
    }

    /**
     * Returns true if any part of this tile is open for player to click on.
     * @param phase the current game phase
     * @return true if any part of this tile is open for player to click on
     */
    public boolean isAvailable(GameFragment.Phase phase) {
       for (Tile subTile: subTiles) {
           if (subTile.isAvailable(phase)) return true;
       }
       return false;
    }

    /**
     * Returns true if the two tiles are connected horizontally, vertically, or diagonally.
     * @param that the other Tile
     * @return true if the two tiles are connected horizontally, vertically, or diagonally
     */
    public boolean isConnectedTo(Tile that) {
       if (this == that) return false;
       if (this.superTile != that.superTile) return false;
       return GameUtils.isConnected(this.index, that.index);
    }
}
