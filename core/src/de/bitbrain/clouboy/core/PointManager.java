package de.bitbrain.clouboy.core;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;

import de.bitbrain.clouboy.assets.Assets;
import de.bitbrain.clouboy.assets.SharedAssetManager;
import de.bitbrain.clouboy.core.PlayerBehavior.PlayerListener;
import de.bitbrain.clouboy.graphics.FX;
import de.bitbrain.clouboy.social.Leaderboard;
import de.bitbrain.clouboy.social.SocialManager;
import de.bitbrain.clouboy.ui.Tooltip;

public class PointManager implements PlayerListener {

  private GameObject player;

  private int record;

  private int points;

  private int jumps, maxJumps, lastJumps;

  private int multiplicator = 1;

  private Tooltip tooltip = Tooltip.getInstance();

  private FX fx = FX.getInstance();

  private SocialManager socialManager;

  public PointManager(GameObject player, SocialManager socialManager) {
    this.player = player;
    this.socialManager = socialManager;
  }


  public int getJumps() {
    return jumps;
  }

  public int getMaxJumps() {
    return maxJumps;
  }

  public void setPlayer(GameObject player) {
    lastJumps = 0;
    multiplicator = 1;
    if (hasNewRecord()) {
      this.record = getPoints();
    }
    this.player = player;
    this.points = 0;
  }

  public void setRecord(int record) {
    this.record = record;
  }

  public int getRecord() {
    return this.record;
  }

  public boolean hasNewRecord() {
    return getPoints() > getRecord();
  }

  public void addPoints(int points) {
    this.points += points;
    Sound sound = SharedAssetManager.get(Assets.SND_KLING, Sound.class);
    sound.play(0.6f, 1f + multiplicator * 0.05f, 1f);
  }

  public void addPoint() {
    points++;
    Sound sound = SharedAssetManager.get(Assets.SND_KLING, Sound.class);
    sound.play(0.6f, 1f + multiplicator * 0.05f, 1f);
  }

  public int getPoints() {
    return points;
  }

  @Override
  public void onJump(GameObject player, int jumps, int maxJumps) {
    this.jumps = jumps;
    this.maxJumps = maxJumps;
  }

  @Override
  public void onLand(GameObject player, int jumps, int maxJumps) {
    if (player.getLastCollision() != null && player.getLastCollision().getType() == GameObjectType.CLOUD) {
      if (jumps == maxJumps && lastJumps == maxJumps) {
        multiplicator++;
        socialManager.submitScore(multiplicator, Leaderboard.HIGHEST_COMBO);
      } else {
        multiplicator = 1;
      }
      final int points = jumps * multiplicator;
      if (points > 0) {
        addPoints(points);
        tooltip.create(player.getLeft(), player.getTop(), "+" + String.valueOf(points));
        if (multiplicator > 1) {
          tooltip.create(player.getLeft(), player.getTop() + 40f, "x" + multiplicator, Color.GREEN);
        }
        if (multiplicator > 2 && multiplicator < 5) {
          fx.shake(multiplicator * 5f, 1.5f);
        } else if (multiplicator > 4) {
          fx.thunder();
        }
      }
      lastJumps = jumps;
    }
  }

}
