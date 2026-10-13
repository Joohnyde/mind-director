/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.handlers.EventHandler;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 *
 * @author denijal
 */
public class DuelHandler implements EventHandler{

   
    private final LinkedHashSet<Integer> set = new LinkedHashSet<>();

    @Override
    public void handlePlayerPick(final int playerIndex) {
        if (set.contains(playerIndex)) {
            set.remove(playerIndex);
            return;
        }
        set.addLast(playerIndex);
    }

    @Override
    public boolean handleReturn() {
        switch (set.size()) {
            case 1 ->
            {
                return false;
            }
            case 2 ->
                showTwo();
            case 3 ->
                showThree();
            default ->
                showAll();
        }

        return true;
    }

    private String printSet() {
        return set.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }

    private void showAll() {
        System.out.println("Showing all dueled players in order " + printSet());
    }

    private void showTwo() {
        System.out.println("Showing the both dueled players, " + set.getFirst() + " and " + set.getLast());
    }

    private void showThree() {
        System.out.println("Showing three dueled players " + printSet());
    }
    
    @Override
    public void onEnter(){
        set.clear();
        EventHandler.super.onEnter();
    }
    
}
