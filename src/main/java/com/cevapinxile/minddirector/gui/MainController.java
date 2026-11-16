package com.cevapinxile.minddirector.gui;

import com.cevapinxile.minddirector.model.GameState;
import com.cevapinxile.minddirector.model.Player;
import com.sun.javafx.scene.control.LabeledText;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MainController {
        
    @FXML
    private void changeStats(MouseEvent event) throws Exception {
        // Button was clicked, do something...
        Object uncasted = event.getTarget();
        if(event.getTarget() instanceof LabeledText labeledText){
            uncasted = labeledText.getParent();            
        }
        final String[] name = ((Button) uncasted).getId().split("(?=\\p{Upper})");
        Field field = GameState.class.getField(name[0]);
        field.set(null, (Object) Math.max(0, (Integer) field.get(null) + (name[1].equals("Minus") ? -1 : 1)));
        showStats();
    }
    
    private void showStats(){
        livesText.setText(GameState.lives+"");
        levelText.setText(GameState.level+"");
        shurikensText.setText(GameState.shurikens+"");
    }
    
    @FXML
    private Label livesText, levelText, shurikensText, p1Name, p2Name, p3Name, p4Name;
    
    @FXML
    private ImageView p1Flag, p2Flag, p3Flag, p4Flag;
    

    public void initialize() throws FileNotFoundException {/*
        System.setProperty(
                "org.slf4j.simpleLogger.log.io.obswebsocket.community.client",
                "warn"
        );
        KeyboardListener.getInstance().registerListener();*/
        
        
        // Lives, Levels, Shurikens
        showStats();
        
        // Player data
        setPlayerData(GameState.PLAYERS.get(0), p1Flag, p1Name);
        setPlayerData(GameState.PLAYERS.get(1), p2Flag, p2Name);
        setPlayerData(GameState.PLAYERS.get(2), p3Flag, p3Name);
        setPlayerData(GameState.PLAYERS.get(3), p4Flag, p4Name);
    }
    
    public void setPlayerData(Player player, ImageView flag, Label name){
           try (FileInputStream input = new FileInputStream(GuiApp.class.getResource("flags/"+player.flag+".png").getFile())) {
                flag.setImage(new Image(input));
                name.setText(player.name);
            } catch (FileNotFoundException ex) {
                System.getLogger(GuiApp.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            } catch (IOException ex) {
                System.getLogger(GuiApp.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
    }
}
