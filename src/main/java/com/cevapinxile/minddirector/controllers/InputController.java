/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.controllers;

import com.cevapinxile.minddirector.input.InputMode;
import static com.cevapinxile.minddirector.input.InputMode.*;
import static com.cevapinxile.minddirector.model.GameState.*;
import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.*;
import java.util.Map;

/**
 *
 * @author denijal
 */
public class InputController {

    private static final InputController INSTANCE = new InputController();

    private InputController() {
    }

    public static InputController getInstance() {
        return INSTANCE;
    }
    public static final Map<Integer, InputMode> MODES = Map.of(
            VC_A, ALL,
            VC_D, DUEL,
            VC_C, CARDS,
            VC_R, READ,
            VC_S, SINGLE
    );

    private InputMode CURRENT_MODE = NONE;

    public void set(final InputMode mode) {
        if (CURRENT_MODE == IDLE) {
            switchState(mode);
        }
    }

    public void idle() {
        switchState(IDLE);
    }

    public void reset() {
        switchState(NONE);
    }
    
    public void confirm() {
        if(CURRENT_MODE.getHandler().handleReturn()){
            switchState(IDLE);
        }
    }

    private void switchState(final InputMode mode) {
        CURRENT_MODE.getHandler().onExit();
        CURRENT_MODE = mode;
        CURRENT_MODE.getHandler().onEnter();
    }

    public void selectPlayer(int playerIndex) {
        if (playerIndex < NUMBER_OF_PLAYERS) {
            CURRENT_MODE.getHandler().handlePlayerPick(playerIndex);
        }
    }

}
