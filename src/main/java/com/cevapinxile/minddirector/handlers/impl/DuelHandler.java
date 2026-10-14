/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.handlers.EventHandler;
import static com.cevapinxile.minddirector.model.GameState.NUMBER_OF_PLAYERS;
import static com.cevapinxile.minddirector.obs.ObsScene.Duel;
import static com.cevapinxile.minddirector.obs.ObsScene.Temp;
import com.cevapinxile.minddirector.obs.ObsService;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 *
 * @author denijal
 */
public class DuelHandler implements EventHandler {

    private final LinkedHashSet<Integer> set = new LinkedHashSet<>();
    private final ObsService obs = ObsService.getInstance();

    @Override
    public void handlePlayerPick(final int playerIndex) {
        for (int i = 0; i < 9; i++) {
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
        if (set.size() == 1) {
            return false;
        }
        obs.commit();
        return true;
    }

    private void showAll() {
        Iterator<Integer> iterator = set.iterator();
        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            if (iterator.hasNext()) {
                obs.replaceSceneItemByIndex(Temp, i, "Player" + (iterator.next() + 1));
            }
            obs.setVisibilty(Temp, i, true);
        }
    }

    private void showOne() {
        replaceAndShow(4, "Player" + (set.getFirst() + 1));
    }

    private void showTwo() {
        replaceAndShow(4, "Player" + (set.getFirst() + 1));
        replaceAndShow(5, "D2P" + (set.getLast()+ 1));
    }

    private void showThree() {
        final Iterator<Integer> iterator = set.iterator();
        for (int i = 6; iterator.hasNext(); i++) {
            replaceAndShow(i, "Player" + (iterator.next() + 1));
        }
    }

    private void replaceAndShow(final int index, final String source) {
        obs.replaceSceneItemByIndex(Temp, index, source);
        obs.setVisibilty(Temp, index, true);
    }

    @Override
    public void onEnter() {
        set.clear();
        obs.prepareScene(Duel);
        for (int i = 0; i < 9; i++) {
            obs.setVisibilty(Temp, i, i <= 3);
        }
    }
}
