package com.cevapinxile.minddirector.gui;

import java.io.IOException;
import javafx.fxml.FXML;

public class MainController {

    @FXML
    private void switchToSecondary() throws IOException {
        GuiApp.setRoot("secondary");
    }
}
