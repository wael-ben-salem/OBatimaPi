package io.ourbatima.controllers.projet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class EstimationWindowController {
    @FXML
    private TextArea estimationTextArea;
    @FXML
    private void initialize() {
        estimationTextArea.setText("Loading estimation...");
    }

    public void setEstimation(String jsonResponse) {
        // Parse JSON and extract the text
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        String estimationText = jsonObject.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();
        estimationText = estimationText.replaceAll("\\*", "");
        estimationTextArea.setText(estimationText);
    }
}
