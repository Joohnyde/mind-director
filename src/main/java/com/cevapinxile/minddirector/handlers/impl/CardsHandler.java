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
public class CardsHandler implements EventHandler {

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
                showOne();
            case 2 ->
                showTwo();
            case 3 ->
                showThree();
            default ->
                showAll();
        }

        return true; // Always allowed
    }

    private String printSet() {
        return set.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }

    private void showAll() {
        System.out.println("Showing all cards in order " + printSet());
    }

    private void showOne() {
        System.out.println("Showing only the card of: " + set.getFirst());
    }

    private void showTwo() {
        System.out.println("Showing the cards of both " + set.getFirst() + " and " + set.getLast());
    }

    private void showThree() {
        System.out.println("Showing the cards of " + printSet());
    }

    @Override
    public void onEnter(){
        set.clear();
        EventHandler.super.onEnter();
    }
}
