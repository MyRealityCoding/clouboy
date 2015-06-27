package de.bitbrain.clouboy.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Pool;

import de.bitbrain.clouboy.graphics.RenderManager;
import de.bitbrain.clouboy.physics.Physics;

public class World {

  private RenderManager renderManager = new RenderManager();

  private Physics physics;

  private final List<GameObject> objects = new ArrayList<GameObject>();

  private final Map<GameObject, Behavior> behaviors = new HashMap<GameObject, Behavior>();

  private final Pool<GameObject> pool = new Pool<GameObject>() {
    @Override
    protected GameObject newObject() {
      return new GameObject();
    }
  };

  public void init() {
    renderManager.init();
    physics = new Physics(objects);
  }

  public GameObject addObject() {
    GameObject object = pool.obtain();
    objects.add(object);
    return object;
  }

  public void applyBehavior(GameObject object, Behavior behavior) {
    if (objects.contains(object)) {
      behaviors.put(object, behavior);
    }
  }

  public void remove(GameObject object) {
    pool.free(object);
    objects.remove(object);
    behaviors.remove(object);
  }

  public void updateAndRender(Batch batch, float delta) {
    for (GameObject object : objects) {
      Behavior behavior = behaviors.get(object);
      if (behavior != null) {
        behavior.update(object, delta);
      }
      physics.apply(object, delta);
      renderManager.render(object, batch);
    }
  }

  public static interface Behavior {
    void update(GameObject object, float delta);
  }

}
