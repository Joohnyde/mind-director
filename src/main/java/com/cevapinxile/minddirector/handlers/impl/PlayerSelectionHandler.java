/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.handlers.impl;

import com.cevapinxile.minddirector.gui.MainController;
import com.cevapinxile.minddirector.handlers.EditStageHandler;
import com.cevapinxile.minddirector.model.EditStage;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import static com.cevapinxile.minddirector.gui.MainController.Accent.*;

/**
 *
 * @author denijal
 */
public class PlayerSelectionHandler implements EditStageHandler {

    private Integer index = -1;

    @Override
    public EditStage handleClick(int keyCode) {
        if (NativeKeyEvent.VC_1 <= keyCode && keyCode <= NativeKeyEvent.VC_4) {
            if (index != -1) {
                MainController.setAccent(index, NONE);
            }
            this.index = keyCode - NativeKeyEvent.VC_1;
            MainController.setAccent(index, HOVER);
        }

        if (keyCode == NativeKeyEvent.VC_ESCAPE) {
            if (index == -1) {
                return null;
            }

            MainController.setAccent(index, NONE);
            System.out.println("Unhover player: " + index);
            index = -1;
        }

        if (keyCode == NativeKeyEvent.VC_ENTER && index >= 0) {
            System.out.println("Select player: " + index);
            MainController.setAccent(index, SELECTED);
            EditStage.CARD_SELECTION.getHandler().enter(index);
            return EditStage.CARD_SELECTION;
        }
        return EditStage.PLAYER_SELECTION;
    }

    @Override
    public void enter(Object args) {
        if (this.index != -1) {
            MainController.setAccent(index, HOVER);
        }
    }

}
