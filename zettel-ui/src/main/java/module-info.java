module zettel.ui {

    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;

    opens com.andreaseisele.zettel.zettelui.controller to javafx.fxml;

    exports com.andreaseisele.zettel.zettelui;
    exports com.andreaseisele.zettel.zettelui.controller;
}