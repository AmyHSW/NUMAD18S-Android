package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang.wordgame;

import android.view.View;

public class Tile {

    private final GameFragment game;
    private final int index;
    private final Tile superTile;
    private final Tile[] subTiles;
    protected View view;

    public Tile(GameFragment game, int index, Tile superTile, Tile[] subTiles) {
      this.game = game;
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

    public boolean isAvailable(GameFragment.Phase phase) {
       for (Tile subTile: subTiles) {
           if (subTile.isAvailable(phase)) return true;
       }
       return false;
    }

    public boolean isConnectedTo(Tile that) {
       if (this == that) return false;
       if (this.superTile != that.superTile) return false;
       return GameUtils.isConnected(this.index, that.index);
    }

//
//   public void animate() {
//      Animator anim = AnimatorInflater.loadAnimator(mGame.getActivity(),
//            R.animator.tictactoe);
//      if (getView() != null) {
//         anim.setTarget(getView());
//         anim.start();
//      }
//   }
}
