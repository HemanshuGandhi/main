package com.notably.view;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * A view for the status bar that is displayed at the footer of the application.
 */
public class StatusBarFooter extends ViewPart<Region> {

    private static final String FXML = "StatusBarFooter.fxml";

    @FXML
    private Label saveLocationStatus;

    public StatusBarFooter(Path saveLocation) {
        super(FXML);
        setDummySaveLocationStatus();
    }

    private void setSaveLocationStatus(Path saveLocation) {
        saveLocationStatus.setText(Paths.get(".").resolve(saveLocation).toString());
    }

    private void setDummySaveLocationStatus() {
        saveLocationStatus.setText("Notably v1.2");
    }
}
