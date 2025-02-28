package io.ourbatima.core.view.layout;

import io.ourbatima.core.interfaces.Loader;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ConstructionLoader extends StackPane implements  Loader {

    public ConstructionLoader(String text) {
        // Création de la tour de la grue
        Rectangle tower = new Rectangle(20, 100);
        tower.setFill(Color.DARKGRAY);

        // Création de la poutre horizontale
        Rectangle beam = new Rectangle(100, 10);
        beam.setFill(Color.GRAY);
        // Position de la poutre : à droite de la tour
        beam.setTranslateX(tower.getWidth());
        beam.setTranslateY(10);

        // Création du câble partant de l'extrémité de la poutre
        Line cable = new Line();
        double cableStartX = beam.getTranslateX() + beam.getWidth();
        double cableStartY = beam.getTranslateY();
        cable.setStartX(cableStartX);
        cable.setStartY(cableStartY);
        double cableLength = 50;
        cable.setEndX(cableStartX);
        cable.setEndY(cableStartY + cableLength);
        cable.setStroke(Color.BLACK);
        cable.setStrokeWidth(2);

        // Création du crochet (représenté par un petit rectangle)
        Rectangle hook = new Rectangle(20, 20);
        hook.setFill(Color.YELLOW);
        // Position initiale du crochet : à la fin du câble, centré
        hook.setTranslateX(cableStartX - hook.getWidth() / 2);
        hook.setTranslateY(cableStartY + cableLength);

        // Regroupement des éléments de la grue
        Group crane = new Group(tower, beam, cable, hook);

        // Texte de chargement
        Text loadingText = new Text(text);
        loadingText.setFill(Color.DARKBLUE);
        loadingText.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Organisation verticale du crane et du texte
        VBox vbox = new VBox(20, crane, loadingText);
        vbox.setAlignment(Pos.CENTER);
        this.getChildren().add(vbox);

        // Animation : le crochet se déplace verticalement pour simuler la montée et la descente d'une charge
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(hook.translateYProperty(), cableStartY + cableLength)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(hook.translateYProperty(), cableStartY + cableLength - 30)),
                new KeyFrame(Duration.seconds(3), new KeyValue(hook.translateYProperty(), cableStartY + cableLength))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @Override
    public void updateTitle(String title) {

    }

    @Override
    public void updateLegend(String legend) {

    }
}
