package com.cevapinxile.minddirector.obs;

import io.obswebsocket.community.client.OBSRemoteController;
import io.obswebsocket.community.client.message.response.sceneitems.CreateSceneItemResponse;
import io.obswebsocket.community.client.model.SceneItem;
import io.obswebsocket.community.client.model.SceneItem.Transform;

import java.util.List;

import static com.cevapinxile.minddirector.obs.ObsScene.Temp;

public class ObsService {

  private static final ObsService INSTANCE = new ObsService();
  private static final String SCENE_TYPE = "OBS_SOURCE_TYPE_SCENE";
  public static final Integer DEFAULT_TIMEOUT = 100;

  private ObsService() {
    controller = OBSRemoteController.builder().build();
    controller.connect();
  }

  public static ObsService getInstance() {
    return INSTANCE;
  }

  private final OBSRemoteController controller;
  private ObsScene scene = null;

  public void prepareScene(ObsScene scene) {
    if (this.scene != null || scene == null) {
      return;
    }
    this.scene = scene;
    controller.removeScene(Temp.toString(), DEFAULT_TIMEOUT);
    controller.createScene(Temp.toString(), DEFAULT_TIMEOUT);

    controller.getSceneItemList(scene.toString(), DEFAULT_TIMEOUT)
              .getSceneItems()
              .forEach(item -> controller.duplicateSceneItem(scene.toString(), item.getSceneItemId(), Temp.toString(),
                                                             DEFAULT_TIMEOUT));
    controller.setCurrentPreviewScene(Temp.toString(), DEFAULT_TIMEOUT);
  }

  public void commit() {
    if (scene == null) {
      return;
    }
    controller.setCurrentProgramScene(Temp.toString(), DEFAULT_TIMEOUT);
    controller.removeScene(scene.toString(), DEFAULT_TIMEOUT);
    controller.setSceneName(Temp.toString(), scene.toString(), DEFAULT_TIMEOUT);
    scene = null;
  }

  public void reset() {
    if (scene == null) {
      return;
    }
    controller.removeScene(Temp.toString(), DEFAULT_TIMEOUT);
    scene = null;
  }

  private Integer getItemId(ObsScene scene, int index) {
    final List<SceneItem> items = controller.getSceneItemList(scene.toString(), DEFAULT_TIMEOUT).getSceneItems();
    if (items == null || items.size() <= index) {
      throw new RuntimeException("Non existant scene or not enough items (" + items.size() + " < " + index + ")");
    }
    return items.get(index).getSceneItemId();
  }

  private Transform toSafeTransform(Transform transform) {
    return transform.toBuilder()
                    .sourceWidth(null)
                    .sourceHeight(null)
                    .width(null)
                    .height(null)
                    .boundsWidth(transform.getBoundsWidth() != null && transform.getBoundsWidth() > 0 ?
                                   transform.getBoundsWidth() :
                                   null)
                    .boundsHeight(transform.getBoundsHeight() != null && transform.getBoundsHeight() > 0 ?
                                    transform.getBoundsHeight() :
                                    null)
                    .build();
  }

  public void printSceneItems(ObsScene scene, String label) {
    System.out.println("\n========== " + label + " ==========");

    controller.getSceneItemList(scene.toString(), DEFAULT_TIMEOUT)
              .getSceneItems()
              .forEach(item -> System.out.printf("ID: %d | Index: %d | Source: %s\n", item.getSceneItemId(),
                                                 item.getSceneItemIndex(), item.getSourceName()));
    System.out.println("====================================\n");
  }

  public void replaceSceneItemByIndex(ObsScene scene, int index, String newSource) {
    final Integer oldItemId = getItemId(scene, index);
    final Transform oldTransform =
      controller.getSceneItemTransform(scene.toString(), oldItemId, DEFAULT_TIMEOUT).getSceneItemTransform();

    final Number oldIndex =
      controller.getSceneItemIndex(scene.toString(), oldItemId, DEFAULT_TIMEOUT).getSceneItemIndex();
    final Boolean enabled =
      controller.getSceneItemEnabled(scene.toString(), oldItemId, DEFAULT_TIMEOUT).getSceneItemEnabled();

    final CreateSceneItemResponse created =
      controller.createSceneItem(scene.toString(), newSource, false, DEFAULT_TIMEOUT);
    final Number newItemId = created.getSceneItemId();

    // Adjust scale to preserve displayed dimensions.
    final Transform newTransform =
      controller.getSceneItemTransform(scene.toString(), newItemId, DEFAULT_TIMEOUT).getSceneItemTransform();

    final Transform transform = toSafeTransform(oldTransform).toBuilder()
                                                             .scaleX(
                                                               (float) (oldTransform.getWidth() / newTransform.getSourceWidth()))
                                                             .scaleY(
                                                               (float) (oldTransform.getHeight() / newTransform.getSourceHeight()))
                                                             .build();

    controller.setSceneItemTransform(scene.toString(), newItemId, transform, DEFAULT_TIMEOUT);
    controller.removeSceneItem(scene.toString(), oldItemId, DEFAULT_TIMEOUT);
    controller.setSceneItemIndex(scene.toString(), newItemId, oldIndex, DEFAULT_TIMEOUT);
    controller.setSceneItemEnabled(scene.toString(), newItemId, enabled, DEFAULT_TIMEOUT);
  }

  public void setVisibilty(ObsScene scene, int index, boolean enable) {
    final Integer itemId = getItemId(scene, index);
    controller.setSceneItemEnabled(scene.toString(), itemId, enable, DEFAULT_TIMEOUT);
  }

}
