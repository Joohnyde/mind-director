/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.util;

import com.cevapinxile.minddirector.gui.GuiApp;
import java.io.FileInputStream;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 *
 * @author denijal
 */
public class CardLabel extends Label implements Comparable<CardLabel> {
    
    private static final Font FONT = new Font("System", 40);

    public Integer card = 0;
    
    public CardLabel(){}

    public CardLabel(CardLabel copy){
        this(copy.card);
    }
    
    public CardLabel(Integer card) {
        if(card != null){
            this.card = card;
            setText(card + "");
        }
        setFont(FONT);
        setTextFill(Paint.valueOf("#fff"));
        setAlignment(Pos.CENTER);
        setPrefWidth(103);
        setPrefHeight(132);

        try (FileInputStream fis = new FileInputStream(GuiApp.class.getResource("card_bg.png").getFile())) {
            Image image = new Image(fis);
            setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        } catch (Exception ignored){
            
        }
    }

    @Override
    public int compareTo(CardLabel o) {
        return card.compareTo(o.card);
    }

    public void showText(String string) {
        Platform.runLater(() -> {
            setText(string);
        });
    }
}
