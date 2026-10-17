/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.handlers.EventHandler;
import com.cevapinxile.minddirector.obs.ObsService;

import java.util.Iterator;
import java.util.LinkedHashSet;

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
            case 1 -> showOne();
            case 2 -> showTwo();
            case 3 -> showThree();
            default -> showAll();
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

    private void showOne() {
        replaceAndShow(4, "Card" + (set.getFirst() + 1));
    }

    private void showTwo() {
        replaceAndShow(5, "C2P" + (set.getFirst() + 1));
        replaceAndShow(6, "C2P" + (set.getLast() + 1));
    }

    private void showThree() {
        final Iterator<Integer> iterator = set.iterator();
        final int first = iterator.next() + 1;
        final int second = iterator.next() + 1;
        final int third = iterator.next() + 1;

        replaceAndShow(7, "Card" + first);
        replaceAndShow(9, "C3P" + second);
        replaceAndShow(8, "Card" + third);
    }

    private void replaceAndShow(final int index, final String source) {
        obs.replaceSceneItemByIndex(Temp, index, source);
        obs.setVisibilty(Temp, index, true);
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
