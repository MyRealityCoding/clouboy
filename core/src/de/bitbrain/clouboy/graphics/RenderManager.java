package de.bitbrain.clouboy.graphics;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Batch;

import de.bitbrain.clouboy.assets.Assets;
import de.bitbrain.clouboy.core.GameObject;
import de.bitbrain.clouboy.core.GameObjectType;

public class RenderManager {

  private static Map<GameObjectType, Renderer> rendererMap = new HashMap<GameObjectType, Renderer>();

  static {
    rendererMap.put(GameObjectType.BOY, new BoyRenderer());
    rendererMap.put(GameObjectType.CLOUD, new SpriteRenderer(Assets.TEX_CLOUD));
    rendererMap.put(GameObjectType.DARK_CLOUD, new SpriteRenderer(Assets.TEX_CLOUD));
  }

  public void init() {
    for (Renderer renderer : rendererMap.values()) {
      renderer.init();
    }
  }

  public void render(GameObject object, Batch batch, float delta) {
    Renderer renderer = rendererMap.get(object.getType());
    if (renderer != null) {
      renderer.render(object, batch, delta);
    }
  }

  static interface Renderer {

    void init();

    void render(GameObject object, Batch batch, float delta);
  }
}
