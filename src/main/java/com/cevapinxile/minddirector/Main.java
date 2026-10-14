/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.cevapinxile.minddirector;

import com.cevapinxile.minddirector.input.KeyboardListener;
/**
 *
 * @author denijal
 */
public class Main {

    public static void main(String[] args) {
        System.setProperty(
          "org.slf4j.simpleLogger.log.io.obswebsocket.community.client",
          "warn"
        );
        KeyboardListener.getInstance().registerListener();
    }
}