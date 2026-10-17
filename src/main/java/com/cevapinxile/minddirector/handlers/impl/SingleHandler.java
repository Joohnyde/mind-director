/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.handlers.EventHandler;
import com.cevapinxile.minddirector.obs.ObsService;

import static com.cevapinxile.minddirector.obs.ObsScene.Single;
import static com.cevapinxile.minddirector.obs.ObsScene.Temp;

/**
 *
 * @author denijal
 */
public class SingleHandler implements EventHandler {

  private final ObsService obs = ObsService.getInstance();
  private Integer player = null;

  @Override
  public void handlePlayerPick(int index) {
    player = index;
    obs.replaceSceneItemByIndex(Temp, 0, "Player" + (index + 1));
  }

  @Override
  public boolean handleReturn() {
    if (player == null) {
      return false;
    }
    obs.commit();
    return true;
  }

  @Override
  public void onEnter() {
    player = -1;
    obs.prepareScene(Single);
  }

}
