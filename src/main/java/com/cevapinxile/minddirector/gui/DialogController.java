/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cevapinxile.minddirector.gui;

import com.cevapinxile.minddirector.Main;
import com.cevapinxile.minddirector.model.GameState;
import com.cevapinxile.minddirector.model.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 *
 * @author denijal
 */
public class DialogController {

    private final String SELECT_LABEL_TEXT = "Select player %d:";

    @FXML
    private GridPane playerGrid;

    @FXML
    private Button confirmButton;

    @FXML
    private Label label;

    public static Set<Player> loadPlayers() {
        Player[] allPlayers = {};
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final InputStream is = Main.class.getResourceAsStream("players/players.json");
            allPlayers = mapper.readValue(is, Player[].class);
        } catch (IOException ignored) {
        }
        return Arrays.stream(allPlayers).collect(Collectors.toSet());
    }

    private Player selected = null;

    void drawPlayers(Set<Player> allPlayers) {

        final String naslov = SELECT_LABEL_TEXT.formatted(1 + GameState.PLAYERS.size());
        this.label.setText(naslov);
        playerGrid.getChildren().clear();

        final AtomicInteger count = new AtomicInteger(0);
        allPlayers.forEach(player -> {
            final int index = count.getAndIncrement();
            final String fileName = Main.class.getResource("players/avatars/" + player.avatar).getFile();
            try (FileInputStream input = new FileInputStream(fileName)) {
                ImageView avatar = new ImageView(new Image(input));
                avatar.setOpacity(.6);
                avatar.setOnMouseClicked(eh -> {
                    playerGrid.getChildren().forEach(e -> e.setOpacity(.6));
                    this.selected = (this.selected == player) ? null : player;
                    confirmButton.setDisable(this.selected == null);
                    avatar.setOpacity(this.selected == null ? .6 : 1);
                });
                playerGrid.add(avatar, index % 4, index / 4);
            } catch (FileNotFoundException ex) {
                System.getLogger(GuiApp.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            } catch (IOException ex) {
                System.getLogger(GuiApp.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
    }
   
    public void initialize() {
        Set<Player> allPlayers = loadPlayers();
        drawPlayers(allPlayers);
        confirmButton.setOnMouseClicked(eh -> {
            if (selected == null) {
                return;
            }
            allPlayers.remove(selected);
            GameState.PLAYERS.add(selected);
            selected = null;
            confirmButton.setDisable(true);

            if (GameState.PLAYERS.size() == GameState.NUMBER_OF_PLAYERS) {
                try {
                    GuiApp.setRoot("mainPane", 1280, 720);
                } catch (IOException ex) {
                    System.getLogger(DialogController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            }
            drawPlayers(allPlayers);
        });
    }

}
