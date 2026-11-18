package com.cevapinxile.minddirector.gui;

import com.cevapinxile.minddirector.input.KeyboardListener;
import com.cevapinxile.minddirector.model.CardNode;
import com.cevapinxile.minddirector.model.GameState;
import com.cevapinxile.minddirector.model.Player;
import com.cevapinxile.minddirector.util.Bag;
import com.cevapinxile.minddirector.util.CardLabel;
import com.sun.javafx.scene.control.LabeledText;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Iterator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class MainController {

    @FXML
    private Label livesText, levelText, shurikensText, p1Name, p2Name, p3Name, p4Name, dialogCard;

    @FXML
    private ImageView p1Flag, p2Flag, p3Flag, p4Flag;

    @FXML
    private ImageView p1Avatar, p2Avatar, p3Avatar, p4Avatar;

    @FXML
    private GridPane p1Cards, p2Cards, p3Cards, p4Cards;

    @FXML
    private Pane dialog;

    private static GridPane[] panes = new GridPane[4];
    private static ImageView[] avatars = new ImageView[4];

    private static final Bag<CardLabel>[] CARDS = new Bag[4];
    private static Pane dialogRef;
    private static Label dialogCardRef;

    static {
        for (int i = 0; i < 4; i++) {
            CARDS[i] = new Bag<>();
        }
    }

    @FXML
    private void changeStats(MouseEvent event) throws Exception {
        // Button was clicked, do something...
        Object uncasted = event.getTarget();
        if (event.getTarget() instanceof LabeledText labeledText) {
            uncasted = labeledText.getParent();
        }
        final String[] name = ((Button) uncasted).getId().split("(?=\\p{Upper})");
        Field field = GameState.class.getField(name[0]);
        field.set(null, (Object) Math.max(0, (Integer) field.get(null) + (name[1].equals("Minus") ? -1 : 1)));
        showStats();
    }

    private void showStats() {
        livesText.setText(GameState.lives + "");
        levelText.setText(GameState.level + "");
        shurikensText.setText(GameState.shurikens + "");
    }

    public void initialize() throws FileNotFoundException {
        GameState.PLAYERS.addAll(DialogController.loadPlayers().stream().toList());
        System.setProperty(
                "org.slf4j.simpleLogger.log.io.obswebsocket.community.client",
                "warn"
        );
        KeyboardListener.getInstance().registerListener();
        // Lives, Levels, Shurikens
        showStats();

        // Player data
        setPlayerData(GameState.PLAYERS.get(0), p1Flag, p1Name);
        setPlayerData(GameState.PLAYERS.get(1), p2Flag, p2Name);
        setPlayerData(GameState.PLAYERS.get(2), p3Flag, p3Name);
        setPlayerData(GameState.PLAYERS.get(3), p4Flag, p4Name);

        panes[0] = p1Cards;
        panes[1] = p2Cards;
        panes[2] = p3Cards;
        panes[3] = p4Cards;

        avatars[0] = p1Avatar;
        avatars[1] = p2Avatar;
        avatars[2] = p3Avatar;
        avatars[3] = p4Avatar;

        dialogRef = dialog;
        dialogCardRef = dialogCard;
    }

    public void setPlayerData(Player player, ImageView flag, Label name) {
        try (FileInputStream input = new FileInputStream(GuiApp.class.getResource("flags/" + player.flag + ".png").getFile())) {
            flag.setImage(new Image(input));
            name.setText(player.name);
        } catch (FileNotFoundException ex) {
            System.getLogger(GuiApp.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } catch (IOException ex) {
            System.getLogger(GuiApp.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public static void refreshCards(int playerIndex) {
        Platform.runLater(() -> {
            panes[playerIndex].getChildren().clear();
            Iterator<CardLabel> iterator = CARDS[playerIndex].inOrder().iterator();
            int i = 0;
            while (iterator.hasNext()) {
                panes[playerIndex].add(iterator.next(), i++, 0);
            }
        });
    }

    public static CardLabel addCard(String toAdd, int playerIndex) {
        CardLabel cardLabel = new CardLabel(Integer.valueOf(toAdd));
        CARDS[playerIndex].add(cardLabel);
        refreshCards(playerIndex);
        return cardLabel;

    }

    public static void removeCard(String toRemove, int playerIndex) {
        CARDS[playerIndex].remove(new CardLabel(Integer.valueOf(toRemove)));
        refreshCards(playerIndex);
    }

    public static void setBorder(CardNode selectedCard, Border border) {
        if (selectedCard.labels.size() >= 1) {
            selectedCard.labels.getFirst().setBorder(border);
        }
    }

    public static void setDialog(boolean visible, String number) {
        Platform.runLater(() -> {
            dialogCardRef.setText(number);
            dialogRef.setVisible(visible);
        });
    }

    public static void setAccent(Integer playerIndex, Accent accent) {
        final String suffix = accent == Accent.NONE ? "" : ("_" + accent).toLowerCase();
        final String fileName = "avatars/avatar_" + (1 + playerIndex) + suffix + ".png";
        final URL resource = GuiApp.class.getResource(fileName);
        System.out.println(fileName + "\t" + resource);
        if (resource == null) {
            return;
        }
        try (FileInputStream fis = new FileInputStream(resource.getFile())) {
            avatars[playerIndex].setImage(new Image(fis));
        } catch (Exception ex) {
        }
    }

    public static CardLabel mockCard(int playerIndex) {
        final CardLabel cardLabel = new CardLabel((Integer) null);
        Platform.runLater(() -> {
            panes[playerIndex].add(cardLabel, panes[playerIndex].getChildren().size(), 0);
        });
        return cardLabel;
    }

    public static void removeMock(int playerIndex) {
        refreshCards(playerIndex);
    }

    public enum Accent {
        NONE,
        SELECTED,
        HOVER
    }

}
