package io.ourbatima.controllers.projet;

import io.ourbatima.core.Dao.Projet.ProjetDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.EtapeProjet;
import io.ourbatima.core.model.Projet;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;



public class AfficherProjet extends ActionView {

    private final ProjetDAO projetDAO = new ProjetDAO();
    @FXML private TableView<Projet> projetTable;
    @FXML private TableColumn<Projet, String> colProjet;
    @FXML private TableColumn<Projet, String> colClient;
    @FXML private TableColumn<Projet, String> colEquipe;
    @FXML private TableColumn<Projet, BigDecimal> colBudget;
    @FXML private TableColumn<Projet, String> colType;
    @FXML private TableColumn<Projet, String> colStyleArch;
    @FXML private TableColumn<Projet, String> colEmplacement;
    @FXML private TableColumn<Projet, String> colEtapes;
    @FXML private TableColumn<Projet, String> colEtat;
    @FXML private TableColumn<Projet, Timestamp> colDateCreation;
    @FXML private TableColumn<Projet, String> colActions;


    private ObservableList<Projet> projetData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("AfficherProjet Controller Initialized");
        setupTable();
        loadProjets();
    }

    private void setupTable() {
        colProjet.setCellValueFactory(new PropertyValueFactory<>("nomProjet"));

        colClient.setCellValueFactory(cellData -> {
            Projet projet = cellData.getValue();
            return new SimpleStringProperty(projet.getEmailClient());
        });

        colEquipe.setCellValueFactory(cellData -> {
            Projet projet = cellData.getValue();
            return new SimpleStringProperty(projet.getNomEquipe());
        });

        colBudget.setCellValueFactory(new PropertyValueFactory<>("budget"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStyleArch.setCellValueFactory(new PropertyValueFactory<>("styleArch"));

        colEmplacement.setCellValueFactory(cellData -> {
            Projet projet = cellData.getValue();
            return new SimpleStringProperty(projet.getEmplacement());
        });

        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));
        colDateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        colEtapes.setCellValueFactory(cellData -> {
            List<EtapeProjet> etapes = cellData.getValue().getEtapes();
            String etapesString = (etapes != null) ?
                    etapes.stream().map(EtapeProjet::getNomEtape).collect(Collectors.joining(", "))
                    : "No Steps";
            return new SimpleStringProperty(etapesString);
        });

        colActions.setCellFactory(new Callback<TableColumn<Projet, String>, TableCell<Projet, String>>() {
            @Override
            public TableCell<Projet, String> call(TableColumn<Projet, String> param) {
                return new TableCell<Projet, String>() {
                    private final Button updateButton = new Button("Modifier");
                    private final Button deleteButton = new Button("Supprimer");

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {

                            updateButton.setOnAction(event -> {
                                Projet projet = getTableView().getItems().get(getIndex());
                                updateProjet(projet);
                            });

                            deleteButton.setOnAction(event -> {
                                Projet projet = getTableView().getItems().get(getIndex());
                                deleteProjet(projet.getId_projet());
                            });

                            HBox hBox = new HBox(updateButton, deleteButton);
                            setGraphic(hBox);
                            setText(null);
                        }
                    }
                };
            }
        });


    }

    @FXML
    void loadProjets() {
        try {
            List<Projet> projets = projetDAO.getAllProjets();
            System.out.println("Fetched Projects: " + projets);
            projetData.setAll(projets);
            projetTable.setItems(null);
            projetTable.layout();  // Force JavaFX to refresh
            projetTable.setItems(projetData);
            projetTable.refresh();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Échec du chargement des projets : " + e.getMessage());
            alert.setTitle("Error");
            alert.showAndWait();
        }
    }

    private void updateProjet(Projet projet) {

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Mettre à jour le projet");
        dialog.setHeaderText("Modifier les détails du projet");

        GridPane grid = new GridPane();
        dialog.getDialogPane().setContent(grid);

        TextField nomProjetField = new TextField(projet.getNomProjet());
        TextField budgetField = new TextField(projet.getBudget().toString());
        ComboBox<String> typeComboBox = new ComboBox<>(FXCollections.observableArrayList("Type1", "Type2", "Type3"));
        typeComboBox.setValue(projet.getType());

        TextField styleArchField = new TextField(projet.getStyleArch());
        TextField etatField = new TextField(projet.getEtat());

        grid.add(new Label("Nom Project :"), 0, 0);
        grid.add(nomProjetField, 1, 0);
        grid.add(new Label("Budget:"), 0, 1);
        grid.add(budgetField, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeComboBox, 1, 2);
        grid.add(new Label("Style d'architecture:"), 0, 3);
        grid.add(styleArchField, 1, 3);
        grid.add(new Label("État :"), 0, 4);
        grid.add(etatField, 1, 4);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setOnHidden(event -> {
            if (dialog.getResult() == ButtonType.OK) {
                Projet updatedProjet = new Projet(
                        projet.getId_projet(),
                        nomProjetField.getText(),
                        projet.getId_equipe(),
                        projet.getId_client(),
                        projet.getEtapes(),
                        new BigDecimal(budgetField.getText()),
                        projet.getTerrain(),
                        typeComboBox.getValue(),
                        styleArchField.getText(),
                        etatField.getText(),
                        projet.getDateCreation()
                );

                projetDAO.updateProjet(updatedProjet);
                loadProjets();
                projetTable.refresh();
            }
        });

        dialog.showAndWait();
    }


    private void deleteProjet(int projetId) {
        projetDAO.deleteProjet(projetId);
        projetData.removeIf(p -> p.getId_projet() == projetId);
        projetTable.setItems(projetData);
    }

    @FXML
    private void handleReload() {
        System.out.println("🔄 Reload button clicked!");
        loadProjets();
    }


}