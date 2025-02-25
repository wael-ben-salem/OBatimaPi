package io.ourbatima.controllers.FinanceControllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;

import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CreateSignature {
    private AjouterContrats ajouterContratsController;

    // Method to set the AjouterContrats controller
    public void setAjouterContratsController(AjouterContrats controller) {
        this.ajouterContratsController = controller;
    }// Declare instance

    @FXML
    private Canvas canvas;
    private GraphicsContext gc;


        @FXML
        public void initialize() {
            gc = canvas.getGraphicsContext2D();

            // Enable drawing on the canvas
            canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
                gc.fillOval(event.getX(), event.getY(), 3, 3); // Draw small circles
            });

        // Set up mouse event handlers for drawing
        setupDrawing();
    }

    private void setupDrawing() {
        canvas.setOnMousePressed(e -> {
            System.out.println("Mouse pressed at: " + e.getX() + ", " + e.getY());
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.setOnMouseDragged(e -> {
            System.out.println("Mouse dragged to: " + e.getX() + ", " + e.getY());
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.setOnMouseReleased(e -> {
            System.out.println("Mouse released at: " + e.getX() + ", " + e.getY());
            gc.closePath();
        });
    }

    public String saveSignature() {
        // Define the directory where signatures will be saved
        File signatureDir = new File("images"); // Change this path as needed
        if (!signatureDir.exists()) {
            // Create the directory if it doesn't exist
            boolean dirCreated = signatureDir.mkdirs();
            if (!dirCreated) {
                System.err.println("Failed to create the directory for signatures.");
                return null;
            }
        }

        // Generate a unique filename for the signature
        String signatureName = "signature_" + System.currentTimeMillis() + ".png"; // Example: signature_1697049600000.png
        File signatureFile = new File(signatureDir, signatureName);

        // Create a WritableImage to take a snapshot of the Canvas
        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);

        // Convert the WritableImage to a BufferedImage
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        // Save the BufferedImage as a PNG file
        try {
            boolean saved = ImageIO.write(bufferedImage, "png", signatureFile);
            if (saved) {
                System.out.println("Signature saved to: " + signatureFile.getPath().replace("\\", "/"));
                return signatureFile.getPath().replace("\\", "/"); // Return the file path for database storage
            } else {
                System.err.println("Failed to save the signature.");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void onSaveButtonClicked() throws IOException {
        String imagepath = saveSignature();
        ajouterContratsController.setimagePath(imagepath);
        ((Stage) canvas.getScene().getWindow()).close();
    }

    @FXML
    public void inisialser(ActionEvent event) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

    }
@FXML
    public void cancel(ActionEvent event) {
    ((Stage) canvas.getScene().getWindow()).close();


}
}