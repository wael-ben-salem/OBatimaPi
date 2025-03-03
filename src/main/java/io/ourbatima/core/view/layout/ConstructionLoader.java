package io.ourbatima.core.view.layout;

import io.ourbatima.core.interfaces.Loader;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ConstructionLoader extends StackPane implements Loader {

        private static final Color LOADER_COLOR = Color.rgb(0, 90, 140);

        public ConstructionLoader(String text) {
            setCache(true); // Activation du cache GPU
            setCacheHint(CacheHint.SPEED);

            // 1. Élément visuel minimaliste
            Group geometricLoader = createGeometricLoader();

            // 2. Texte simplifié
            Text loadingText = createMinimalText(text);

            getChildren().addAll(geometricLoader, loadingText);
        }

        private Group createGeometricLoader() {
            // Structure géométrique légère
            Circle base = new Circle(40);
            base.setFill(Color.TRANSPARENT);
            base.setStroke(LOADER_COLOR);
            base.setStrokeWidth(3);

            // Animation de rotation
            RotateTransition rt = new RotateTransition(Duration.seconds(2), base);
            rt.setFromAngle(0);
            rt.setToAngle(360);
            rt.setCycleCount(Animation.INDEFINITE);
            rt.setInterpolator(Interpolator.LINEAR);
            rt.play();

            return new Group(base, createProgressIndicator());
        }

        private Node createProgressIndicator() {
            // Arc de progression minimal
            Arc progressArc = new Arc();
            progressArc.setRadiusX(45);
            progressArc.setRadiusY(45);
            progressArc.setStartAngle(90);
            progressArc.setLength(0);
            progressArc.setStroke(LOADER_COLOR);
            progressArc.setStrokeWidth(4);
            progressArc.setFill(Color.TRANSPARENT);

            // Animation de progression
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(progressArc.lengthProperty(), 0)),
                    new KeyFrame(Duration.seconds(1.5), new KeyValue(progressArc.lengthProperty(), 360))
            );
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

            return progressArc;
        }

        private Text createMinimalText(String text) {
            Text txt = new Text(text);
            txt.setFont(Font.font("Arial", 14));
            txt.setFill(LOADER_COLOR);
            txt.setTranslateY(60);

            // Animation de pulsation légère
            ScaleTransition st = new ScaleTransition(Duration.seconds(1), txt);
            st.setFromX(1);
            st.setToX(1.05);
            st.setAutoReverse(true);
            st.setCycleCount(Animation.INDEFINITE);
            st.play();

            return txt;
        }


    @Override
    public void updateTitle(String title) {

    }

    @Override
    public void updateLegend(String legend) {

    }

    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }
}