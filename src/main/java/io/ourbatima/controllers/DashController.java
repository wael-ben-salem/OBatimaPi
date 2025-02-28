package io.ourbatima.controllers;

import io.ourbatima.core.Context;
import io.ourbatima.core.controls.*;
import io.ourbatima.core.controls.icon.IconContainer;
import io.ourbatima.core.controls.icon.Icons;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.NotificationCell;
import io.ourbatima.core.model.SearchViewBox;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import io.ourbatima.core.view.layout.Bar;
import io.ourbatima.core.view.layout.BoxUser;
import io.ourbatima.core.view.layout.DialogContainer;
import io.ourbatima.core.view.layout.creators.ScheduleListCreator;
import io.ourbatima.core.view.layout.creators.ScheduleListItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
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
    private GridPane gridTiles;
    @FXML
    private GridPane footer;
    @FXML
    private StackedAreaChart<Number, Number> graphic;

    private Utilisateur currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Creating the Area chart
        graphic.setTitle("Sales by Region");
        Utilisateur currentUser = SessionManager.getUtilisateur();
        if(currentUser == null) {
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
                Arrays.asList("10", "20", "30", "40", "50", "60", "70" )));

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
//        footer.getChildren().addAll(createDonut(), barChart,  scheduleList.getRoot(), curvedChart);
        footer.getChildren().addAll(createDonut(), barChart, scheduleList, curvedChart);

        GridPane.setConstraints(footer.getChildren().get(0), 0, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(barChart, 1, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(scheduleList, 0, 1, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(curvedChart, 1, 1, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);


    }


    @Override
    public void onInit(Context context) {



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
        System.out.println("je suis dans dash "+currentUser.getRole());
// Ajouter après l'initialisation de currentUser dans onInit()
        if(currentUser != null) {

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
        title.setPadding(new Insets(0,0,0,5));
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

        context.layout().bar().addInLeft( title );
        HBox.setMargin(title, new Insets(0,0,0,5));

        GNBadge notification = new GNBadge(Icons.NOTIFICATIONS);
        notification.setNumberOfNotifications(2);
        notification.getStyleClass().add("bd-danger");
//            notification.setColorCircle(Color.web(Colors.AQUA.toString()));
        GNBadge sms = new GNBadge(Icons.SMS);
        sms.setNumberOfNotifications(39);
        sms.getStyleClass().add("bd-info");
//            sms.setColorCircle(Color.web(Colors.GRAPEFRUIT.toString()));

        BoxUser boxUser = new BoxUser(
                currentUser.getNom() + " " + currentUser.getPrenom(),

                context.getResource("style/img/me_avatar.jpeg").toExternalForm());

//        boxUser.setPadding(new Insets(0,2,10,2));
        Separator separator =  new Separator(Orientation.VERTICAL);
        HBox.setMargin(boxUser, new Insets(0,0,0,10));

         context.layout().bar().addInRight(rightBar);


         rightBar.getChildren().addAll(btnSearch, sms, notification);

        HBox.setMargin(notification, new Insets(0, 15, 0,5));

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
                    } else if (newValue.doubleValue() < 1400){
                        Grid.inLine(gridTiles);
                        Grid.change(footer, 2);
                    } else {
                        Grid.inLine(gridTiles);
                        Grid.inLine(footer);
                    }
                });
    }
    private void setBackgroundColorBasedOnRole(String role, Region region) {
        if (region == null || role == null) return;

        switch (role) {
            case "Artisan":
                region.setStyle("-fx-background-color: #FFD700;"); // Jaune
                break;
            case "Constructeur":
                region.setStyle("-fx-background-color: #808080;"); // Gris
                break;
            case "GestionnaireStock":
                region.setStyle("-fx-background-color: #ADD8E6;"); // Bleu clair
                break;
            case "Client":
                region.setStyle("-fx-background-color: #90EE90;"); // Vert clair
                break;
            case "Admin":
                region.setStyle("-fx-background-color: #000000;"); // Noir
                break;
            default:
                region.setStyle("-fx-background-color: white;"); // Par défaut
                break;
        }
    }
        private VBox createDialogNotification() {
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("Notifications");
        title.getStyleClass().addAll("h5", "text-bold");
        Hyperlink btn = new Hyperlink("Mark as read");
        btn.setGraphic(new IconContainer(Icons.DONE_ALL));
        btn.setPadding(new Insets(10));
        btn.getStyleClass().addAll("text-bold","transparent", "text-info", "no-border");

        GridPane header = new GridPane();
        header.getChildren().addAll(title, btn);
        GridPane.setConstraints(title, 0,0,1,1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
        GridPane.setConstraints(btn, 1,0,1,1, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);

//        ListView<NotificationCell> listView = new ListView<>();
//        listView.setCellFactory(new NotificationListFactory());

        VBox vBox = createNotifications(
                new NotificationCell(
                        true,
                        "Your Password has been changed successfully.",
                        new GNIconButton(Icons.BADGE),
                        LocalDateTime.now()
                ),
                new NotificationCell(
                        false,
                        "Thank you for booking a meeting with us.",
                        new GNAvatar(new Image(context.getResource("style/avatars/man1@50.png").toExternalForm()), 20),
                        LocalDateTime.now()
                ),
                new NotificationCell(
                        false,
                        "Great News! We are launching our 5th fund: DLE Senior Living.",
                        new GNAvatar(new Image(context.getResource("style/avatars/man2@50.png").toExternalForm()), 20),
                        LocalDateTime.now()
                )
        );


//        listView.setPrefHeight(3 * 45);
//        listView.setStyle("-fx-fixed-cell-size : 100px;");
//

        Hyperlink btnAll = new Hyperlink("View All Notifications");
        btnAll.setPadding(new Insets(10));
        btnAll.getStyleClass().addAll("text-bold", "transparent", "no-border", "text-info");

        root.getChildren().setAll(header, vBox, btnAll);
        return root;
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

    private VBox createNotifications(NotificationCell... cells) {
        VBox box = new VBox();
        for (NotificationCell item : cells) {

            ToggleButton toggleButton = new ToggleButton();
            toggleButton.setMaxWidth(Double.MAX_VALUE);
            toggleButton.getStyleClass().addAll("btn-flat", "transparent");

            Text text = new Text(item.text());
            TextFlow textFlow = new TextFlow(text);
            text.getStyleClass().addAll("text-12", "text-bold");
            String pattern = "dd MMM yyyy HH:mm:ss";
            Text time = new Text(item.time().format(DateTimeFormatter.ofPattern(pattern, Locale.US)));
            GridPane grid = new GridPane();
            Node icon = item.icon();
            icon.setStyle("-fx-fill : white; -fx-text-fill: white; -text-color : white;");
            Circle circle = new Circle();
            circle.setRadius(5);
            if (item.read()) {
                circle.setStyle("-fx-fill : -info;");
            } else {
                circle.setStyle("-fx-fill : white;");
            }
            grid.getChildren().setAll(circle, textFlow, time, icon);
//            grid.setGridLinesVisible(true);
            GridPane.setConstraints(circle, 0,0,1,2, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
            GridPane.setConstraints(textFlow, 1,0,1,1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
            GridPane.setConstraints(time, 1,1,1,1, HPos.LEFT, VPos.TOP, Priority.ALWAYS, Priority.ALWAYS);
            GridPane.setConstraints(icon, 2,0,1,2, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS);
            grid.setHgap(10);
            toggleButton.setGraphic(grid);
            box.getChildren().addAll(toggleButton, new Separator());
        }

        return box;
    }
}
