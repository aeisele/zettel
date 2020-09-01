package com.andreaseisele.zettel.zettelui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class MainController {

    private static final Logger logger = LogManager.getLogger();

    @FXML
    private Label messageLabel;

    public void loadMessage(ActionEvent actionEvent) {
        logger.debug("action event {}", actionEvent);
        this.messageLabel.setText(LocalDateTime.now() + "\t" + "Hello World");
    }

}
