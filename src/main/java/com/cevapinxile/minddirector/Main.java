/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.cevapinxile.minddirector;

import com.cevapinxile.minddirector.input.KeyboardListener;
import com.github.kwhat.jnativehook.NativeHookException;

/**
 *
 * @author denijal
 */
public class Main {

    public static void main(String[] args) throws NativeHookException {
        KeyboardListener.getInstance().registerListener();
    }
}