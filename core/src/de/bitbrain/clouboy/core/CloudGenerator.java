package de.bitbrain.clouboy.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.OrthographicCamera;

import de.bitbrain.clouboy.core.PlayerBehavior.PlayerListener;
import de.bitbrain.clouboy.tweens.GameObjectTween;

public class CloudGenerator implements PlayerListener {

  private OrthographicCamera camera;

  private float currentGap = 0;

  private float cloudDistance = 250;

  private GameObjectFactory factory;

  private GameObject recentCloud;

  private HashMap<String, Collection<GameObject>> data = new HashMap<String, Collection<GameObject>>();

  private TweenManager tweenManager;

  private String lastUUID;

  static {
    Tween.registerAccessor(GameObject.class, new GameObjectTween());
  }

  public CloudGenerator(OrthographicCamera camera, GameObjectFactory factory, TweenManager tweenManager) {
    this.camera = camera;
    this.factory = factory;
    this.tweenManager = tweenManager;
  }

  public void reset() {
    data.clear();
    lastUUID = "";
    recentCloud = null;
    currentGap = 0;

  }

  public void update(float delta) {
    if (recentCloud != null) {
      while (!recentCloud.getUUID().equals(lastUUID)) {
        if (currentGap >= maxGap()) {
          return;
        }
        generateNext();
      }
    }
    currentGap = recentCloud != null ? recentCloud.getRight() : 0;
    while (currentGap < maxGap()) {
      generateNext();
    }
  }

  private float maxGap() {
    return camera.position.x + camera.zoom * camera.viewportWidth * 3f;
  }

  private float getRandomY() {
    return camera.position.y - 200f + (float) (Math.random() * 500) - (float) (Math.random() * 500);
  }

  private void generateNext() {
    int size = (int) (6 + Math.random() * 12);
    List<GameObject> clouds = factory.createCloud(currentGap + cloudDistance, getRandomY(), size);
    recentCloud = clouds.get(0);
    float maxX = recentCloud.getRight();
    for (GameObject cloud : clouds) {
      if (cloud.getRight() > maxX) {
        maxX = cloud.getRight();
        recentCloud = cloud;
      }
    }
    currentGap = recentCloud.getRight();
    lastUUID = recentCloud.getUUID();
    data.put(recentCloud.getId(), clouds);
  }

  @Override
  public void onJump(GameObject player, int jumps, int maxJumps) {
    if (player.getLastCollision() != null && player.getLastCollision().getType() == GameObjectType.CLOUD) {
      GameObject cloud = player.getLastCollision();
      Collection<GameObject> clouds = data.get(cloud.getId());
      if (clouds != null) {
        for (GameObject object : clouds) {
          removeCloud(object);
        }
        data.remove(cloud.getId());
      }
    }
  }

  private void removeCloud(final GameObject cloud) {
    final float duration = (float) (1f - Math.random() * 0.5f);
    cloud.setCollision(null);
    tweenManager.killTarget(cloud);
    Tween.to(cloud, GameObjectTween.SCALE, duration).target(0f).setCallbackTriggers(TweenCallback.COMPLETE)
        .setCallback(new TweenCallback() {
          @Override
          public void onEvent(int type, BaseTween<?> source) {
            factory.getWorld().remove(cloud);
          }
        }).start(tweenManager);
    Tween.to(cloud, GameObjectTween.ALPHA, duration).target(0f).start(tweenManager);
  }
}
