package io.ourbatima.core;

import io.ourbatima.core.impl.Layout;
import io.ourbatima.core.interfaces.Loader;
import io.ourbatima.core.services.LoadViews;
import io.ourbatima.core.view.View;
import io.ourbatima.core.view.layout.ConstructionLoader;
import io.ourbatima.core.view.layout.LoadCircle;
import io.ourbatima.core.view.layout.TruckHelmetLoader;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.sql.Connection;

import static io.ourbatima.core.Dao.DatabaseConnection.getConnection;
import org.opencv.core.Core;


public class Main extends Launcher {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Override
    public void build(Context context) {
        Layout layout = new Layout(context);
        context.setLayout(layout);

        // Conteneur principal
        StackPane mainPane = new StackPane();
        mainPane.setStyle("-fx-background-color: transparent;");

        // Configuration du background avec opacité
        ImageView backgroundImageView = new ImageView(
                new Image(getClass().getResource("/images/back.png").toExternalForm())
        );
        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.setSmooth(true);
        backgroundImageView.setOpacity(0.5);
        backgroundImageView.setFitWidth(1500);

        // Création du Loader personnalisé : animation de grue
        Loader customLoader = new TruckHelmetLoader("OurBatima..");

        // Conteneur de contenu
        StackPane contentPane = new StackPane();
        contentPane.getChildren().addAll(
                backgroundImageView,
                (Node) customLoader // Cast autorisé car Loader implémente Node
        );

        layout.setContent(contentPane);

        // Chargement des vues
        Task<View> loadViews = new LoadViews(context, customLoader);

        context.setLayout(layout);

        Loader loadCircle = new LoadCircle("Starting..", "");

        Thread tLoadViews = new Thread(loadViews);
        tLoadViews.setDaemon(true);
        tLoadViews.start();

        loadViews.setOnSucceeded(event -> {
            Platform.runLater(() -> layout.setContent(context.routes().getView("login").getRoot()));



            View loginView = context.routes().getView("login");
            layout.setContent(loginView.getRoot());
            System.out.println("OpenCV version: " + Core.VERSION);



        });
    }

    private Loader createCraneLoader(String text) {
        return new ConstructionLoader(text);
    }
}
