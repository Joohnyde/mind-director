/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.input;

import com.cevapinxile.minddirector.controllers.InputController;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

/**
 *
 * @author denijal
 */
public final class KeyboardListener implements NativeKeyListener {

    private static final KeyboardListener INSTANCE = new KeyboardListener();
    public static KeyboardListener getInstance(){ return INSTANCE; }
    
    private Robot robot;
    private InputController inputController = InputController.getInstance();
    private boolean listens = false;
        
    private KeyboardListener() {
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            System.getLogger(KeyboardListener.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    @Override
    public void nativeKeyPressed(final NativeKeyEvent nativeEvent) {
        final int keyCode = nativeEvent.getKeyCode();
        if (keyCode == NativeKeyEvent.VC_CAPS_LOCK) {
            listens = !listens;
            if(listens){
                inputController.idle();
            }else{
                inputController.reset();
            }
            return;
        }

        if (!listens) {
            return;
        }

        if (InputController.MODES.containsKey(keyCode)){
            inputController.set(InputController.MODES.get(keyCode));
            return;
        }
        
        if (NativeKeyEvent.VC_1 <= keyCode && keyCode <= NativeKeyEvent.VC_4){
            inputController.selectPlayer(keyCode - NativeKeyEvent.VC_1);
            return;
        }
        
        if (keyCode == NativeKeyEvent.VC_P){
            // TODO: call progress
            return;
        }
        
        if (keyCode == NativeKeyEvent.VC_ENTER){
            inputController.confirm();
            return;
        }

        // Escape cancels the state
        if (keyCode == NativeKeyEvent.VC_ESCAPE){
            inputController.idle();
        }
    }
    public void forceDisableCapsLock() {
        try {
            // Check if Caps Lock is currently on
            boolean isOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);

            if (isOn) {
                // Simulate a key press and release to turn it off
                robot.keyPress(KeyEvent.VK_CAPS_LOCK);
                robot.keyRelease(KeyEvent.VK_CAPS_LOCK);
            }
        } catch (Exception e) {
            System.getLogger(KeyboardListener.class.getName()).log(System.Logger.Level.ERROR, (String) null, e);
        }
    }

    public void registerListener() {
        try {
            forceDisableCapsLock();
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        } catch (Exception ex) {
            System.getLogger(KeyboardListener.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

}
