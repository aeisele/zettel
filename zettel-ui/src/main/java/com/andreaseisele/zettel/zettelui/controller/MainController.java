package com.andreaseisele.zettel.zettelui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    private Label messageLabel;

    public void loadMessage(ActionEvent actionEvent) {
        logger.debug("action event {}", actionEvent);
        this.messageLabel.setText(LocalDateTime.now() + "\t" + "Hello World");
    }

}
