package io.ourbatima.controllers;

import io.ourbatima.core.Context;
import io.ourbatima.core.Dao.Utilisateur.EquipeDAO;
import io.ourbatima.core.Dao.Utilisateur.NotificationDAO;
import io.ourbatima.core.Launcher;
import io.ourbatima.core.controls.CurvedChart;
import io.ourbatima.core.controls.DonutChart;
import io.ourbatima.core.controls.GNBadge;
import io.ourbatima.core.controls.GNIconButton;
import io.ourbatima.core.controls.icon.IconContainer;
import io.ourbatima.core.controls.icon.Icons;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.SearchViewBox;
import io.ourbatima.core.model.Utilisateur.Equipe;
import io.ourbatima.core.model.Utilisateur.Notification;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import io.ourbatima.core.view.layout.Bar;
import io.ourbatima.core.view.layout.BoxUser;
import io.ourbatima.core.view.layout.DialogContainer;
import io.ourbatima.core.view.layout.creators.ScheduleListCreator;
import io.ourbatima.core.view.layout.creators.ScheduleListItem;
import javafx.animation.*;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Gleidson Neves da Silveira | gleidisonmt@gmail.com
 * Version 0.0.1
 * Create on  23/04/2023
 */
@SuppressWarnings("unchecked")
public final class DashController extends ActionView {


    @FXML
    private Label lblUserName;
    @FXML
    private StackPane root;

    @FXML
    private Button videoCallButton;
    @FXML
    private StackPane localVideo;
    @FXML
    private StackPane remoteVideo;
    @FXML
    private ListView<String> messageList;
    @FXML
    private TextField messageInput;


    private GNBadge sms; // Badge pour les messages
    private GNBadge notification; // Déclaration comme variable de classe

    private ObservableList<Notification> notifications = FXCollections.observableArrayList();

    @FXML
    private GridPane gridTiles;
    @FXML
    private GridPane footer;
    @FXML
    private Button sendButton;

    @FXML
    private StackedAreaChart<Number, Number> graphic;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Creating the Area chart
        graphic.setTitle("Sales by Region");
        Utilisateur currentUser = SessionManager.getUtilisateur();
        if (currentUser == null) {
            System.err.println("Aucun utilisateur connecté !");
            return;
        }
        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        series1.setName(currentUser.getNom() + " " + currentUser.getPrenom());


        series1.getData().add(new XYChart.Data<>(10.5, 100.0));
        series1.getData().add(new XYChart.Data<>(18d, 70d));
        series1.getData().add(new XYChart.Data<>(10d, 21d));
        series1.getData().add(new XYChart.Data<>(42d, 90d));
        series1.getData().add(new XYChart.Data<>(45d, 110d));
        series1.getData().add(new XYChart.Data<>(57d, 90d));
        series1.getData().add(new XYChart.Data<>(59d, 86d));
        series1.getData().add(new XYChart.Data<>(86d, 20d));
        series1.getData().add(new XYChart.Data<>(97d, 30d));
        series1.getData().add(new XYChart.Data<>(99d, 110d));

        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        series2.setName("East");

        series2.getData().add(new XYChart.Data<>(11d, 110d));
        series2.getData().add(new XYChart.Data<>(11.5d, 120d));
        series2.getData().add(new XYChart.Data<>(19d, 110d));
        series2.getData().add(new XYChart.Data<>(32d, 90d));
        series2.getData().add(new XYChart.Data<>(48d, 140d));
        series2.getData().add(new XYChart.Data<>(49d, 104d));
        series2.getData().add(new XYChart.Data<>(77d, 50d));
        series2.getData().add(new XYChart.Data<>(79d, 140d));
        series2.getData().add(new XYChart.Data<>(90d, 120d));
        series2.getData().add(new XYChart.Data<>(100d, 90d));

        XYChart.Series<Number, Number> series3 = new XYChart.Series<>();
        series3.setName("South");

        series3.getData().add(new XYChart.Data<>(5d, 15d));
        series3.getData().add(new XYChart.Data<>(20d, 110d));
        series3.getData().add(new XYChart.Data<>(13d, 230d));
        series3.getData().add(new XYChart.Data<>(27d, 180d));
        series3.getData().add(new XYChart.Data<>(42d, 160d));
        series3.getData().add(new XYChart.Data<>(49d, 100d));
        series3.getData().add(new XYChart.Data<>(53d, 150d));
        series3.getData().add(new XYChart.Data<>(58d, 200d));
        series3.getData().add(new XYChart.Data<>(70d, 190d));
        series3.getData().add(new XYChart.Data<>(94d, 160d));

        //Setting the data to area chart

//        graphic.getData().
        graphic.getData().setAll(series1, series2, series3);

        Node scheduleList = new ScheduleListCreator()
                .title("Schedule")
                .items(
                        new ScheduleListItem(
                                4,
                                "Software Enginer",
                                "10000 steps",
                                "Gleidson",
                                LocalTime.of(12, 45)
                        ),
                        new ScheduleListItem(
                                20,
                                "UI/UX Shopping",
                                "10 of 45 chapters",
                                "Gleidson",
                                LocalTime.of(12, 45)
                        ),
                        new ScheduleListItem(
                                35,
                                "Mobile Analytics",
                                "10 of 23 chapters",
                                "Gleidson",
                                LocalTime.of(12, 45),
                                event -> System.out.println("Click action trigged!")

                        )
                )
                .build().getRoot();

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(
                Arrays.asList("10", "20", "30", "40", "50", "60", "70")));

        NumberAxis yAxis = new NumberAxis(0, 1000, 100);
        yAxis.setLabel("Population in Millions");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.getStyleClass().addAll("border-box", "border-1");
        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.getData().add(new XYChart.Data<>("20", 40));
        s.getData().add(new XYChart.Data<>("30", 300));
        s.getData().add(new XYChart.Data<>("40", 500));
        s.getData().add(new XYChart.Data<>("50", 798));
        s.setName("North");

        XYChart.Series<String, Number> b = new XYChart.Series<>();
        b.getData().add(new XYChart.Data<>("20", 146));
        b.getData().add(new XYChart.Data<>("30", 456));
        b.getData().add(new XYChart.Data<>("40", 234));
        b.getData().add(new XYChart.Data<>("50", 609));
        b.setName("South");

        XYChart.Series<String, Number> c = new XYChart.Series<>();
        c.getData().add(new XYChart.Data<>("40", 200));
        c.getData().add(new XYChart.Data<>("60", 280));
        c.getData().add(new XYChart.Data<>("40", 900));
        c.getData().add(new XYChart.Data<>("40", 700));
        c.setName("East");
        barChart.getData().addAll(s, b, c);

        CurvedChart<Number, Number> curvedChart = new CurvedChart<>(
                new NumberAxis(),
                new NumberAxis()
        );

        final XYChart.Series<Number, Number> series22 = new XYChart.Series<>();

        series22.getData().addAll(
                new XYChart.Data<>(0, 20D),
                new XYChart.Data<>(1, 40D),
                new XYChart.Data<>(2, 50D),
                new XYChart.Data<>(3, 30D),
                new XYChart.Data<>(4, 80D),
                new XYChart.Data<>(5, 10D),
                new XYChart.Data<>(6, 50D));

        curvedChart.getData().add(series22);
        footer.getChildren().addAll(createDonut(), barChart, scheduleList, curvedChart);

        GridPane.setConstraints(footer.getChildren().get(0), 0, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(barChart, 1, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(scheduleList, 0, 1, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(curvedChart, 1, 1, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        loadNotifications();



        currentUser = SessionManager.getUtilisateur();
        if (currentUser != null) {
            loadNotifications();
            setupRealTimeUpdates();
            updateSmsBadge();

        }


    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void loadNotifications() {
        try {
            if (SessionManager.getUtilisateur() != null) {
                List<Notification> latestNotifications =
                        new MessagingService().getUserNotifications(
                                SessionManager.getUtilisateur().getId()
                        );
                notifications.setAll(latestNotifications);
                updateNotificationBadge(); // Mettre à jour le badge
            }
        } catch (SQLException e) {
            showErrorAlert("Erreur de chargement des notifications");
        }
    }
    private void showErrorAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }







    private void updateNotifications() {
        try {
            List<Notification> newNotifications =
                    new MessagingService().getUserNotifications(
                            SessionManager.getUtilisateur().getId()
                    );
            notifications.setAll(newNotifications);
            updateNotificationBadge();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateNotificationBadge() {
        try {
            int unreadCount = new NotificationDAO().getUnreadCount(SessionManager.getUtilisateur().getId());
            System.out.println("andke manndek" + unreadCount);
            Platform.runLater(() -> {
                notification.setNumberOfNotifications(unreadCount);

                // Animation du badge
                if (unreadCount > 0) {
                    RotateTransition rt = new RotateTransition(Duration.millis(200), notification);
                    rt.setByAngle(20);
                    rt.setCycleCount(2);
                    rt.setAutoReverse(true);
                    rt.play();
                }
            });
        } catch (SQLException e) {
            showErrorAlert("Erreur de mise à jour des notifications");
        }
    }

    @Override
    public void onInit(Context context) {


        String css = getClass().getResource("/styles/notif.css").toExternalForm();
        root.getStylesheets().add(css);
        super.onInit(context);
        Bar bar = new Bar();
        context.layout().setBar(bar);
        bar.getStyleClass().addAll("border-light-gray-2", "border-b-1");
        if (SessionManager.getUtilisateur() == null) {
            System.err.println("Aucun utilisateur connecté !");
            return;
        }
        Utilisateur currentUser = SessionManager.getUtilisateur();
        String userRole = String.valueOf(currentUser.getRole());
        System.out.println("je suis dans dash " + currentUser.getRole());
// Ajouter après l'initialisation de currentUser dans onInit()
        if (currentUser != null) {

            setBackgroundColorBasedOnRole(userRole, bar);
        } else {
            root.setStyle("-fx-background-color: white;");
        }

        HBox rightBar = new HBox();
        rightBar.setAlignment(Pos.CENTER_RIGHT);
        rightBar.setSpacing(10);
        rightBar.setPadding(new Insets(0, 10, 0, 10));

        Label title = new Label("OUR BATIMA");
        title.setFont(Font.font("Roboto", 36));
        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        shadow.setColor(Color.GRAY);
        title.setEffect(shadow);
        title.setPadding(new Insets(0, 0, 0, 5));
        title.setTextFill(Color.web("#FFD700")); // Doré
        StackPane root = new StackPane(title);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);"); // Fond dégradé bleu foncé

        title.getStyleClass().addAll("title-text", "title", "text-14");
        title.setWrapText(true);

//        GNTextBox textSearch = new GNTextBox("");
//        textSearch.setId("tf-search");
//        textSearch.setPrefHeight(30);
//        textSearch.setPrefWidth(300);
//        textSearch.setPromptText("Search");
//        textSearch.setOnMouseClicked(event -> {
//            System.out.println("test 01");
//        });

        GNIconButton btnSearch = new GNIconButton(Icons.SEARCH);
        btnSearch.getStyleClass().add("btn-flat");
        btnSearch.setStyle("-fx-cursor: hand;");


        btnSearch.setOnMouseClicked(event -> {
            SearchViewBox searchViewBox = new SearchViewBox(context);
            context.wrapper()
                    .content(
                            new DialogContainer(searchViewBox)
                                    .style("-fx-background-radius: 5px;")
                                    .size(800, 400)
                    )
                    .onShown(e -> searchViewBox.focus())
                    .show();
        });


//        textSearch.setIcon(Icons.SEARCH);
//        HBox.setMargin(textSearch, new Insets(2,10, 2, 10));
//        new SearchViewBox(context, textSearch, context.searchItems());

        context.layout().bar().addInLeft(title);
        HBox.setMargin(title, new Insets(0, 0, 0, 5));

        notification = new GNBadge(Icons.NOTIFICATIONS);
        notification.getStyleClass().add("bd-danger");
//            notification.setColorCircle(Color.web(Colors.AQUA.toString()));
        sms = new GNBadge(Icons.SMS);
        sms.setNumberOfNotifications(39);
        sms.getStyleClass().add("bd-info");
//            sms.setColorCircle(Color.web(Colors.GRAPEFRUIT.toString()));

        BoxUser boxUser;
        try {
            String imagePath = context.getResource("/images/default.png").toExternalForm();
            boxUser = new BoxUser(currentUser.getNom() + " " + currentUser.getPrenom(), imagePath);
        } catch (Exception e) {
            // Fallback si l'image n'est pas trouvée
            String defaultImage = getClass().getResource("/images/default.png").toExternalForm();
            boxUser = new BoxUser(currentUser.getNom() + " " + currentUser.getPrenom(), defaultImage);

            // Log d'avertissement
            System.out.println("Image par défaut utilisée : " + defaultImage);
        }

//        boxUser.setPadding(new Insets(0,2,10,2));
        Separator separator = new Separator(Orientation.VERTICAL);
        HBox.setMargin(boxUser, new Insets(0, 0, 0, 10));

        context.layout().bar().addInRight(rightBar);


        rightBar.getChildren().addAll(btnSearch, sms, notification);

        HBox.setMargin(notification, new Insets(0, 15, 0, 5));

        VBox b = createDialogNotification();

        notification.setOnMouseClicked(event ->
                context.flow()
                        .content(
                                new DialogContainer(b)
                                        .size(400, 280)
                        )
//                    .background(Wrapper.WrapperBackgroundType.GRAY)
                        .show(Pos.BOTTOM_CENTER, notification));


        root.widthProperty()
                .addListener((observable, oldValue, newValue) -> {

                    gridTiles.getColumnConstraints().clear();
                    gridTiles.getRowConstraints().clear();
//                gridTiles.setHgap(10);


                    if (newValue.doubleValue() < 537) {
                        Grid.change(gridTiles, 1);
                    } else if (newValue.doubleValue() < 810) {
                        Grid.change(gridTiles, 2);
                        Grid.change(footer, 1);
                    } else if (newValue.doubleValue() < 1400) {
                        Grid.inLine(gridTiles);
                        Grid.change(footer, 2);
                    } else {
                        Grid.inLine(gridTiles);
                        Grid.inLine(footer);
                    }
                });
        sms.setOnMouseClicked(event -> handleVideoCall());
        notification.setOnMouseClicked(event -> showNotifications());

    }


    private void setBackgroundColorBasedOnRole(String role, Region region) {
    if (region == null || role == null) return;

    switch (role) {
        case "Client":
            region.setStyle("-fx-background-color: #ffffff;"); // Blanc
            break;
        case "Artisan":
        case "Constructeur":
        case "GestionnaireStock":
        case "Admin":
        default:
            region.setStyle("-fx-background-color: #000000;"); // Noir
            break;
    }
}

    private VBox createDialogNotification() {
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setPrefSize(400, 400); // Taille fixe

        Text title = new Text("Notifications");
        title.getStyleClass().addAll("h5", "text-bold");
        Hyperlink btn = new Hyperlink("Mark as read");
        btn.setGraphic(new IconContainer(Icons.DONE_ALL));
        btn.setPadding(new Insets(10));
        btn.getStyleClass().addAll("text-bold", "transparent", "text-info", "no-border");

        btn.setOnAction(event -> {
            try {
                Utilisateur currentUser = SessionManager.getUtilisateur();
                new NotificationDAO().markAllAsRead(currentUser.getId());
                loadNotifications(); // Recharger les notifications
                updateNotificationBadge(); // Mettre à jour le badge

                // Rafraîchir l'affichage
                VBox updatedNotifications = createNotifications(notifications.toArray(new Notification[0]));
                ScrollPane scrollPane = (ScrollPane) root.lookup("#notificationScrollPane");
                if (scrollPane != null) {
                    scrollPane.setContent(updatedNotifications);
                }
            } catch (SQLException e) {
                showErrorAlert("Erreur lors de la mise à jour des notifications");
            }
        });

        GridPane header = new GridPane();
        header.getChildren().addAll(title, btn);
        GridPane.setConstraints(title, 0, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(btn, 1, 0, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

        VBox vBox = createNotifications(notifications.toArray(new Notification[0]));
        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setId("notificationScrollPane"); // Ajouter un ID pour le retrouver plus tard
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250); // Hauteur fixe
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Hyperlink btnAll = new Hyperlink("View All Notifications");
        btnAll.setPadding(new Insets(10));
        btnAll.getStyleClass().addAll("text-bold", "transparent", "no-border", "text-info");

        btnAll.setOnAction(event -> showAllNotificationsDialog());

        root.getChildren().setAll(header, scrollPane, btnAll);
        return root;
    }
    private void showAllNotificationsDialog() {
        VBox notificationsContent = createNotifications(notifications.toArray(new Notification[0]));
        ScrollPane scrollPane = new ScrollPane(notificationsContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        Dialog<Void> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().setPrefSize(500, 450);
        dialog.setTitle("Historique des Notifications");
        dialog.getDialogPane().getStyleClass().add("notification-dialog");

        // Ajouter un style CSS personnalisé
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/notif.css").toExternalForm()
        );

        // Bouton de fermeture
        ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        dialog.showAndWait();
    }

    private DonutChart createDonut() {
        DonutChart donutChart = new DonutChart();

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        data.add(new PieChart.Data("left", 30));
        data.add(new PieChart.Data("top", 20));
        data.add(new PieChart.Data("bottom", 10));
        data.add(new PieChart.Data("right", 40));

        donutChart.setTitle("Processors");
        donutChart.setAnimated(true);
        donutChart.setLabelsVisible(true);
        donutChart.setLabelLineLength(10);
        donutChart.setMinHeight(400);
        donutChart.setData(data);
        return donutChart;
    }
    private VBox createNotifications(Notification... notifications) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #f8f9fa;");

        for (Notification notification : notifications) {
            HBox item = new HBox(10);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setPadding(new Insets(10));
            item.setStyle("-fx-background-color: white; -fx-background-radius: 5;");
            item.setEffect(new DropShadow(3, Color.gray(0.3)));

            // Indicateur visuel
            Circle indicator = new Circle(5);
            indicator.setFill(notification.isRead() ? Color.GRAY : Color.web("#4CAF50"));

            VBox content = new VBox(3);
            Label message = new Label(notification.getMessage());
            message.setStyle("-fx-font-weight: " + (notification.isRead() ? "normal" : "bold") + ";");
            message.setWrapText(true);

            Label time = new Label(notification.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM HH:mm")));
            time.setStyle("-fx-text-fill: #666; -fx-font-size: 0.9em;");

            content.getChildren().addAll(message, time);

            // Bouton d'action
            if ("CONVERSATION".equals(notification.getType())) {
                Button actionBtn = new Button("Rejoindre");
                actionBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                item.getChildren().addAll(indicator, content, actionBtn);
            } else if ("MEETING".equals(notification.getType())) {
                // Ajouter un bouton ou un texte spécifique pour les notifications de type "MEETING"
                Button joinBtn = new Button("Rejoindre la réunion");
                joinBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                item.getChildren().addAll(indicator, content, joinBtn);
            } else {
                item.getChildren().addAll(indicator, content);
            }

            // Animation pour nouvelles notifications
            if (!notification.isRead()) {
                ScaleTransition st = new ScaleTransition(Duration.millis(300), indicator);
                st.setFromX(1);
                st.setFromY(1);
                st.setToX(1.5);
                st.setToY(1.5);
                st.setAutoReverse(true);
                st.setCycleCount(2);
                st.play();
            }

            box.getChildren().add(item);

            item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 10;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-background-color: white; -fx-background-radius: 10;"));
        }

        return box;
    }
    private HBox createNotificationItem(Notification notification) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);

        Circle indicator = new Circle(5,
                notification.isRead() ? Color.GRAY : Color.GREEN);

        Label message = new Label(notification.getMessage());
        message.setWrapText(true);

        if ("CONVERSATION".equals(notification.getType())) {
            Button openBtn = new Button("Ouvrir");
            item.getChildren().addAll(indicator, message, openBtn);
        } else {
            item.getChildren().addAll(indicator, message);
        }

        return item;
    }

    private void showNotifications() {
        VBox notificationsPanel = createDialogNotification();

        context.flow()
                .content(new DialogContainer(notificationsPanel).size(400, 280)) // Configuration du contenu
                .show(Pos.BOTTOM_CENTER, notification); // Appel de show() sur le flux
    }


    private void startJitsiMeeting(Equipe team) {
        String roomName = URLEncoder.encode(team.getNom(), StandardCharsets.UTF_8);
        String jitsiUrl = "https://meet.jit.si/" + roomName;

        try {
            HostServices hostServices = Launcher.getHostServicesInstance();
            hostServices.showDocument(jitsiUrl);
        } catch (Exception e) {
            showErrorAlert("Erreur de lancement de la réunion");
        }
    }
    private void notifyTeamMembers(Equipe team, Utilisateur currentUser) {
        try {
            List<Utilisateur> members = new EquipeDAO().getTeamMembers(team.getId());
            String message = currentUser.getNom() + " a rejoint la réunion de l'équipe " + team.getNom();

            MessagingService messagingService = new MessagingService();
            for (Utilisateur member : members) {
                if (member.getId() != currentUser.getId()) {
                    // Créer une notification
                    Notification notification = new Notification(
                            member.getId(), // recipientId
                            "Nouveau participant au meet", // title
                            message, // message
                            "MEETING", // type
                            false, // isRead
                            LocalDateTime.now() // createdAt
                    );

                    // Envoyer la notification via le service de messagerie
                    messagingService.sendUserNotification(
                            member.getId(),
                            "Nouveau participant au meet",
                            message
                    );


                    // Ajouter la notification à la liste
                    notifications.add(notification);

                    // Afficher une alerte immédiate
                    showNewNotificationAlert(notification);
                    System.out.println("Notification créée : " + notification.getMessage());

                }
            }

            // Mettre à jour l'interface utilisateur
            Platform.runLater(() -> {
                VBox updatedNotifications = createNotifications(notifications.toArray(new Notification[0]));
                ScrollPane scrollPane = (ScrollPane) root.lookup("#notificationScrollPane"); // Assurez-vous d'avoir un ID sur le ScrollPane
                if (scrollPane != null) {
                    scrollPane.setContent(updatedNotifications);
                }
            });

        } catch (SQLException e) {
            showErrorAlert("Erreur de notification des membres");
            e.printStackTrace();
        }
    }

    private void setupRealTimeUpdates() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> {
                    try {
                        checkNewRatings();

                        List<Notification> newNotifications = new MessagingService()
                                .getUserNotifications(SessionManager.getUtilisateur().getId());

                        // Détecter les nouvelles notifications non lues
                        newNotifications.stream()
                                .filter(n -> n.getCreatedAt().isAfter(LocalDateTime.now().minusSeconds(5)))
                                .forEach(this::showNewNotificationAlert);

                        notifications.setAll(newNotifications);
                        updateNotificationBadge();
                        updateSmsBadge(); // Nouvelle méthode pour SMS

                    } catch (SQLException ex) {
                        showErrorAlert("Erreur de mise à jour");
                    }
                }
                ));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void checkNewRatings() {
        try {
            List<Notification> ratingNotifications = new NotificationDAO()
                    .getUnreadNotificationsByType(SessionManager.getUtilisateur().getId(), "RATING");

            ratingNotifications.forEach(notification -> {
                showNewNotificationAlert(notification);
                try {
                    new NotificationDAO().markAsRead(notification.getId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (SQLException e) {
            System.err.println("Erreur de vérification des évaluations: " + e.getMessage());
        }
    }

    private void updateSmsBadge() {
        try {
            int conversationCount = new EquipeDAO()
                    .getTeamsByUserId(SessionManager.getUtilisateur().getId())
                    .size();

            Platform.runLater(() -> {
                sms.setNumberOfNotifications(conversationCount);
                // Animation
                ScaleTransition st = new ScaleTransition(Duration.millis(200), sms);
                st.setFromX(1);
                st.setFromY(1);
                st.setToX(1.2);
                st.setToY(1.2);
                st.setAutoReverse(true);
                st.setCycleCount(2);
                st.play();
            });
        } catch (SQLException e) {
            showErrorAlert("Erreur de chargement des conversations");
        }
    }


    private void handleVideoCall() {
        try {
            Utilisateur currentUser = SessionManager.getUtilisateur();
            List<Equipe> teams = new EquipeDAO().getTeamsByUserId(currentUser.getId());

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Rejoindre une réunion");
            dialog.getDialogPane().setPrefSize(400, 300);

            ListView<Equipe> listView = new ListView<>(FXCollections.observableArrayList(teams));
            listView.setCellFactory(param -> new ListCell<Equipe>() {
                @Override
                protected void updateItem(Equipe team, boolean empty) {
                    super.updateItem(team, empty);

                    if (empty || team == null) {
                        setGraphic(null);
                        setText(null);
                        return;
                    }

                    try {
                        HBox box = new HBox(10);
                        box.setAlignment(Pos.CENTER_LEFT);
                        box.setPadding(new Insets(10));

                        // Gestion sécurisée de l'image
                        ImageView icon = new ImageView();
                        try {
                            String imagePath = context.getResource("images/meet.png").toExternalForm();
                            icon.setImage(new Image(imagePath));
                        } catch (Exception e) {
                            // Fallback si l'image n'est pas trouvée
                            String defaultImage = getClass().getResource("/images/default-meet.png").toExternalForm();
                            icon.setImage(new Image(defaultImage));
                            System.out.println("Image de secours utilisée pour : " + team.getNom());
                        }
                        icon.setFitWidth(32);
                        icon.setFitHeight(32);

                        VBox textBox = new VBox(5);
                        Label name = new Label(team.getNom());
                        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
                        Label members = new Label(team.getMembres().size() + " membres");
                        members.setStyle("-fx-text-fill: #666;");

                        textBox.getChildren().addAll(name, members);
                        box.getChildren().addAll(icon, textBox);
                        setGraphic(box);

                    } catch (Exception e) {
                        // Fallback minimaliste en cas d'erreur
                        setText(team.getNom() + " (" + team.getMembres().size() + " membres)");
                        System.err.println("Erreur de rendu de l'équipe : " + team.getNom());
                        e.printStackTrace();
                    }
                }
            });
            listView.setOnMouseClicked(event -> {
                Equipe selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    startJitsiMeeting(selected);

                    // Envoyer une notification aux membres de l'équipe
                    String notificationMessage = currentUser.getNom() + " a rejoint la conversation de l'équipe " + selected.getNom() + ". Il vous attend pour communiquer.";
                    MessagingService messagingService = new MessagingService();
                    try {
                        messagingService.sendTeamNotification(selected, notificationMessage);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    // Ajouter la notification à la liste des notifications
                    Notification notification = new Notification(
                            currentUser.getId(), // recipientId (ici, l'utilisateur courant)
                            "Nouveau participant au meet", // title
                            notificationMessage, // message
                            "MEETING", // type
                            false, // isRead
                            LocalDateTime.now() // createdAt
                    );
                    notifications.add(notification);

                    // Mettre à jour l'interface utilisateur
                    Platform.runLater(() -> {
                        VBox updatedNotifications = createNotifications(notifications.toArray(new Notification[0]));
                        ScrollPane scrollPane = (ScrollPane) root.lookup("#notificationScrollPane");
                        if (scrollPane != null) {
                            scrollPane.setContent(updatedNotifications);
                        }
                    });

                    dialog.close();
                }
            });

            VBox content = new VBox(10);
            content.setPadding(new Insets(10));
            content.getChildren().add(new Label("Sélectionnez une équipe :"));
            content.getChildren().add(listView);

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.showAndWait();
        } catch (SQLException e) {
            showErrorAlert("Erreur de chargement des équipes");
        }
    }
    // Modifier la méthode existante pour le bouton FXML
    @FXML
    private void handleVideoCallButton(ActionEvent event) {
        handleVideoCall();
    }

    private void showNewNotificationAlert(Notification notification) {
        Platform.runLater(() -> {
            try {
                // Création du conteneur principal
                HBox alertBox = new HBox(10);
                alertBox.setAlignment(Pos.CENTER_LEFT);
                alertBox.setPadding(new Insets(15, 20, 15, 15));
                alertBox.setStyle("-fx-background-color: #4CAF50; "
                        + "-fx-background-radius: 15px; "
                        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 1);");

                // Icône de notification
                ImageView icon = new ImageView();
                try {
                    Image notifyIcon = new Image(getClass().getResourceAsStream("/images/notification-bell.png"));
                    icon.setImage(notifyIcon);
                } catch (Exception e) {
                    // Fallback si l'image n'est pas trouvée
                    icon.setImage(new Image(getClass().getResourceAsStream("/images/default-notification.png")));
                    System.err.println("Icône de notification non trouvée, utilisation du fallback");
                }
                icon.setFitWidth(24);
                icon.setFitHeight(24);

                // Contenu textuel
                VBox textContainer = new VBox(3);
                Label titleLabel = new Label("Nouvelle notification");
                titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 14px;");

                Label contentLabel = new Label(notification.getMessage());
                contentLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
                contentLabel.setWrapText(true);
                contentLabel.setMaxWidth(300);

                textContainer.getChildren().addAll(titleLabel, contentLabel);

                // Bouton de fermeture
                Button closeButton = new Button("×");
                closeButton.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-background-color: transparent;");
                closeButton.setOnAction(e -> ((StackPane) alertBox.getParent()).getChildren().remove(alertBox));

                alertBox.getChildren().addAll(icon, textContainer, closeButton);

                // Conteneur de positionnement
                StackPane container = new StackPane(alertBox);
                container.setAlignment(Pos.TOP_CENTER);
                container.setPadding(new Insets(20));
                container.setPickOnBounds(false); // Permet les clics à travers

                // Animation d'entrée
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), container);
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);

                // Animation de sortie
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), container);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);

                // Temporisation automatique
                PauseTransition delay = new PauseTransition(Duration.seconds(5));
                delay.setOnFinished(e -> fadeOut.play());

                // Gestion de la hiérarchie
                root.getChildren().add(container);

                // Démarrage des animations
                fadeIn.play();
                delay.play();

                // Suppression après animation
                fadeOut.setOnFinished(e -> root.getChildren().remove(container));

            } catch (Exception e) {
                System.err.println("Erreur d'affichage de la notification : " + e.getMessage());
                // Fallback basique
                Label errorLabel = new Label("Nouvelle notification : " + notification.getMessage());
                errorLabel.setStyle("-fx-text-fill: white; -fx-background-color: #4CAF50; -fx-padding: 10px;");
                StackPane.setAlignment(errorLabel, Pos.TOP_CENTER);
                StackPane.setMargin(errorLabel, new Insets(20));

                root.getChildren().add(errorLabel);

                new Timeline(new KeyFrame(Duration.seconds(5),
                        ef -> root.getChildren().remove(errorLabel))).play();
            }
        });
    }

}