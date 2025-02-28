package io.ourbatima.core.view.layout;


import io.ourbatima.core.interfaces.Loader;
import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Loader personnalisé combinant un camion de chantier en mouvement et un casque de chantier en rotation.
 */
public class TruckHelmetLoader extends StackPane implements Loader {

    public TruckHelmetLoader(String text) {
        // --- Animation du camion ---
        // Création des formes du camion :
        Rectangle truckCab = new Rectangle(60, 30);
        truckCab.setFill(Color.DARKBLUE);

        Rectangle truckTrailer = new Rectangle(80, 30);
        truckTrailer.setFill(Color.BLUE);
        truckTrailer.setTranslateX(70);  // Positionné à droite de la cabine

        // Création des roues
        Circle wheel1 = new Circle(10);
        wheel1.setFill(Color.BLACK);
        wheel1.setTranslateX(20);
        wheel1.setTranslateY(25);

        Circle wheel2 = new Circle(10);
        wheel2.setFill(Color.BLACK);
        wheel2.setTranslateX(50);
        wheel2.setTranslateY(25);

        Circle wheel3 = new Circle(10);
        wheel3.setFill(Color.BLACK);
        wheel3.setTranslateX(100);
        wheel3.setTranslateY(25);

        Circle wheel4 = new Circle(10);
        wheel4.setFill(Color.BLACK);
        wheel4.setTranslateX(130);
        wheel4.setTranslateY(25);

        Group truck = new Group(truckCab, truckTrailer, wheel1, wheel2, wheel3, wheel4);
        // Position initiale du camion hors écran à gauche
        truck.setTranslateX(-200);

        // Animation de translation : le camion se déplace de gauche à droite puis revient
        TranslateTransition truckTransition = new TranslateTransition(Duration.seconds(4), truck);
        truckTransition.setFromX(-200);
        truckTransition.setToX(200); // ajustez en fonction de la largeur de votre écran
        truckTransition.setCycleCount(Animation.INDEFINITE);
        truckTransition.setAutoReverse(true);
        truckTransition.play();

        // --- Animation du casque ---
        // Création d'une forme simplifiée du casque (ici un arc représentant le devant du casque)
        Arc helmet = new Arc(0, 0, 20, 20, 0, 180);
        helmet.setFill(Color.ORANGE);
        helmet.setType(ArcType.ROUND);

        // Animation de rotation pour le casque
        RotateTransition helmetRotation = new RotateTransition(Duration.seconds(2), helmet);
        helmetRotation.setByAngle(360);
        helmetRotation.setCycleCount(Animation.INDEFINITE);
        helmetRotation.play();

        // --- Organisation des éléments ---
        // On positionne le camion et le casque dans un même conteneur.
        // Le casque est placé légèrement au-dessus du camion pour rappeler l'univers de la sécurité.
        StackPane loaderPane = new StackPane();
        loaderPane.setAlignment(Pos.CENTER);
        loaderPane.getChildren().addAll(truck, helmet);

        // Ajustement de la position du casque par rapport au camion
        helmet.setTranslateY(-50);

        // Texte d'indication de chargement
        Text loadingText = new Text(text);
        loadingText.setFill(Color.DARKGREEN);
        loadingText.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        // Mise en page verticale de l'animation et du texte
        VBox vbox = new VBox(20, loaderPane, loadingText);
        vbox.setAlignment(Pos.CENTER);

        this.getChildren().add(vbox);
    }

    @Override
    public void updateTitle(String title) {

    }

    @Override
    public void updateLegend(String legend) {

    }
}
