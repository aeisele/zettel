module zettel.ui {

    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;

    opens com.andreaseisele.zettel.zettelui.controller to javafx.fxml;

    exports com.andreaseisele.zettel.zettelui;
    exports com.andreaseisele.zettel.zettelui.controller;
}