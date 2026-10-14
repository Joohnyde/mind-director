/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.handlers.EventHandler;
import com.cevapinxile.minddirector.obs.ObsService;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static com.cevapinxile.minddirector.model.GameState.NUMBER_OF_PLAYERS;
import static com.cevapinxile.minddirector.obs.ObsScene.Cards;
import static com.cevapinxile.minddirector.obs.ObsScene.Temp;

/**
 *
 * @author denijal
 */
public class CardsHandler implements EventHandler {

  private final LinkedHashSet<Integer> set = new LinkedHashSet<>();
  private final ObsService obs = ObsService.getInstance();

  @Override
  public void handlePlayerPick(final int playerIndex) {
    for (int i = 0; i < 10; i++) {
      obs.setVisibilty(Temp, i, false);
    }

    if (set.contains(playerIndex)) {
      set.remove(playerIndex);
    } else {
      set.addLast(playerIndex);
    }

    switch (set.size()) {
      case 1:
      case 2:
        showOneOrTwo();
        break;
      case 3:
        showThree();
        break;
      default:
        showAll();
        break;
    }
  }

  @Override
  public boolean handleReturn() {
    obs.commit();
    return true; // Always allowed
  }

  private void showAll() {
    Iterator<Integer> iterator = set.iterator();
    for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
      if (iterator.hasNext()) {
        obs.replaceSceneItemByIndex(Temp, i, "Card" + (iterator.next() + 1));
      }
      obs.setVisibilty(Temp, i, true);
    }
  }

  private void showOneOrTwo() {
    Iterator<Integer> iterator = set.iterator();
    for (int i = 0; i < set.size(); i++) {
      final int index = 3 + i + set.size();
      obs.replaceSceneItemByIndex(Temp, index, "Card" + (iterator.next() + 1));
      obs.setVisibilty(Temp, index, true);
    }
  }

  private void showThree() {
    final Iterator<Integer> iterator = set.iterator();
    final int first = iterator.next() + 1;
    final int second = iterator.next() + 1;
    final int third = iterator.next() + 1;

    obs.replaceSceneItemByIndex(Temp, 7, "Card" + first);
    obs.replaceSceneItemByIndex(Temp, 9, "C3PB" + second);
    obs.replaceSceneItemByIndex(Temp, 8, "Card" + third);
    for (int i = 7; i <= 9; i++)
      obs.setVisibilty(Temp, i, true);
  }

  @Override
  public void onEnter() {
    set.clear();
    obs.prepareScene(Cards);
    for (int i = 0; i < 10; i++) {
      obs.setVisibilty(Temp, i, i <= 3);
    }

  }

}
