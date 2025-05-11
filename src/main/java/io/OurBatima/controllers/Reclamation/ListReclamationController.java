package io.OurBatima.controllers.Reclamation;


import io.OurBatima.core.Dao.Reclamation.ReclamationDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Reclamation;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

/*public class ListReclamationController extends ActionView {

    private final ReclamationDAO reclamationDAO = new ReclamationDAO();

    @FXML private TableView<Reclamation> reclamationTable;
    @FXML private TableColumn<Reclamation, Integer> colId;
    @FXML private TableColumn<Reclamation, String> colDescription;
    @FXML private TableColumn<Reclamation, String> colStatut;
    @FXML private TableColumn<Reclamation, Timestamp> colDate;
    @FXML private TableColumn<Reclamation, String> colActions;


    private ObservableList<Reclamation> reclamationData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("AfficherReclamation Controller Initialized");
        setupTable();
        loadReclamations();

      /*  searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleSearchInput();
            if (newValue.isEmpty()) {
                suggestionsList.setVisible(false);
            }
        });

        suggestionsList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedSuggestion = suggestionsList.getSelectionModel().getSelectedItem();
                searchField.setText(selectedSuggestion);
                suggestionsList.setVisible(false);
                handleSearch();
            }
        });
    }

    @FXML
   /* private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        List<Reclamation> filteredList = reclamationData.stream()
                .filter(reclamation -> reclamation.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        reclamationTable.setItems(FXCollections.observableArrayList(filteredList));
    }

    @FXML
    private void handleSearchInput() {
        String searchText = searchField.getText().toLowerCase();
        List<String> suggestions = reclamationData.stream()
                .map(Reclamation::getDescription)
                .filter(desc -> desc.toLowerCase().startsWith(searchText))
                .collect(Collectors.toList());

        if (!suggestions.isEmpty()) {
            suggestionsList.getItems().setAll(suggestions);
            suggestionsList.setVisible(true);
        } else {
            suggestionsList.setVisible(false);
        }
    }
*/
  /*  private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        colActions.setCellFactory(new Callback<TableColumn<Reclamation, String>, TableCell<Reclamation, String>>() {
            @Override
            public TableCell<Reclamation, String> call(TableColumn<Reclamation, String> param) {
                return new TableCell<Reclamation, String>() {
                    private final Button updateButton = new Button("Modifier");
                    private final Button deleteButton = new Button("Supprimer");
                    {
                        updateButton.setStyle("-fx-background-color: #b39427; -fx-text-fill: white;");
                        deleteButton.setStyle("-fx-background-color: #4c3f0a; -fx-text-fill: white;");
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            updateButton.setOnAction(event -> {
                                Reclamation reclamation = getTableView().getItems().get(getIndex());
                               // openUpdateReclamationWindow(reclamation);
                            });

                            deleteButton.setOnAction(event -> {
                                Reclamation reclamation = getTableView().getItems().get(getIndex());
                                deleteReclamation(reclamation.getId());
                            });

                            HBox hBox = new HBox(10, updateButton, deleteButton);
                            setGraphic(hBox);
                            setText(null);
                        }
                    }
                };
            }
        });
    }

   /* private void openUpdateReclamationWindow(Reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/Reclamation/UpdateReclamation.fxml"));
            Parent root = loader.load();
            UpdateReclamation controller = loader.getController();
            controller.initData(reclamation, this);

            Stage stage = new Stage();
            stage.setTitle("Update Reclamation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir la fenêtre de modification.");
            alert.showAndWait();
        }
    }*/
/*
    @FXML
    public void loadReclamations() {
        try {
            List<Reclamation> reclamations = reclamationDAO.getAllReclamations();
            System.out.println("Fetched Reclamations: " + reclamations);
            reclamationData.setAll(reclamations);

            reclamationTable.layout();
            reclamationTable.setItems(reclamationData);
            reclamationTable.refresh();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Échec du chargement des réclamations : " + e.getMessage());
            alert.setTitle("Error");
            alert.showAndWait();
        }
    }*/
/*
    private void deleteReclamation(int reclamationId) {
        reclamationDAO.deleteReclamation(reclamationId);
        reclamationData.removeIf(r -> r.getId() == reclamationId);
        reclamationTable.setItems(reclamationData);
    }

    @FXML
    private void handleReload() {
        System.out.println("🔄 Reload button clicked!");
        loadReclamations();
        initialize();
   }
}*/


import io.OurBatima.core.Dao.Reclamation.ReclamationDAO;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.model.Reclamation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.util.List;


import static java.awt.SystemColor.text;

public class ListReclamationController extends ActionView {

    @FXML private GridPane reclamationGrid;
    private final ReclamationDAO reclamationDAO = new ReclamationDAO();

    @FXML
    public void initialize() {
        System.out.println("ListReclamationController Initialized");
        loadReclamations();
    }


    @FXML
    private void loadReclamations() {
        List<Reclamation> reclamations = reclamationDAO.getAllReclamations();

        // Clear existing data rows (keep header row 0)
        reclamationGrid.getChildren().removeIf(node -> {
            Integer rowIndex = GridPane.getRowIndex(node);
            return rowIndex != null && rowIndex >= 1;
        });

        // Add new data rows
        int rowIndex = 1;
        for (Reclamation reclamation : reclamations) {
            // ID (non-editable)
            TextField idField = createTextField(String.valueOf(reclamation.getId()));
            idField.setEditable(false);

            // Description (editable)
            TextField descriptionField = createTextField(reclamation.getDescription());

            // Statut (editable)
            TextField statutField = createTextField(reclamation.getStatut());

            // Date (non-editable)
            TextField dateField = createTextField(reclamation.getDate().toString());
            dateField.setEditable(false);

           /* ComboBox<Integer> utilisateurComboBox = new ComboBox<>();
            utilisateurComboBox.getItems().addAll(getAllUtilisateurIds()); // Method to fetch all user IDs
            utilisateurComboBox.setValue(reclamation.getUtilisateurId().toString()); // Set the current user ID
            utilisateurComboBox.setDisable(true); // Make it non-editable*/

            TextField UtilisateurIdField = createTextField(String.valueOf(reclamation.getUtilisateurId()));
            dateField.setEditable(false);


            // Save Button
            Button saveButton = new Button("update");
            saveButton.setOnAction(e -> openUpdatePopup(reclamation.getId()));



            // Delete Button
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
            deleteButton.setOnAction(event -> {
                reclamationDAO.deleteReclamation(reclamation.getId());
                System.out.println("Reclamation deleted successfully: " + reclamation.getId());
                loadReclamations();
            });

            // Add components to the GridPane
            reclamationGrid.addRow(rowIndex, idField, descriptionField, statutField, dateField,UtilisateurIdField , saveButton, deleteButton);
            rowIndex++;
        }
    }

    private void openUpdatePopup(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OurBatima/views/pages/Reclamation/UpdateReclamation.fxml"));
            Parent root = loader.load();

            UpdateReclamationController updateReclamationController = loader.getController();
            updateReclamationController.setReclamationId(id); // Pass the reclamation ID to the controller

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier le Reclamation");
            stage.setScene(new Scene(root));

            // Add a listener to refresh the list when the popup is closed
            stage.setOnHidden(event -> loadReclamations());

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error opening update popup: " + e.getMessage());
        }
    }


    @FXML
    private void exportToPdf() {
        List<Reclamation> reclamations = reclamationDAO.getAllReclamations();
        if (reclamations.isEmpty()) {
            System.out.println("Aucune réclamation à exporter.");
            return;
        }

        // Ouvrir une boîte de dialogue pour choisir l'emplacement du fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.xlsx"));
        File file = fileChooser.showSaveDialog(null);

        if (file == null) {
            System.out.println("Aucun fichier sélectionné.");
            return;
        }

        String filePath = file.getAbsolutePath();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

            // Position initiale pour écrire le texte
            float margin = 50;
            float yPosition = 700; // Commence en haut de la page

            // Titre du document
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Liste des Réclamations");
            contentStream.endText();
            yPosition -= 20;

            // Parcourir la liste des réclamations
            for (Reclamation reclamation : reclamations) {
                // Ajouter l'ID de la réclamation
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("ID: " + reclamation.getId());
                contentStream.endText();
                yPosition -= 15;

                // Ajouter la description
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Description: " + reclamation.getDescription());
                contentStream.endText();
                yPosition -= 15;

                // Ajouter le statut
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Statut: " + reclamation.getStatut());
                contentStream.endText();
                yPosition -= 15;

                // Ajouter la date
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Date: " + reclamation.getDate().toString());
                contentStream.endText();
                yPosition -= 20; // Espacement entre les réclamations

                // Vérifier si on dépasse la page
                if (yPosition < margin) {
                    contentStream.close(); // Fermer le contentStream actuel
                    page = new PDPage(); // Créer une nouvelle page
                    document.addPage(page); // Ajouter la nouvelle page au document
                    contentStream = new PDPageContentStream(document, page); // Créer un nouveau contentStream
                    yPosition = 700; // Réinitialiser la position Y

                    // Réécrire le titre sur la nouvelle page
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText("Liste des Réclamations (suite)");
                    contentStream.endText();
                    yPosition -= 20;
                }
            }

            // Fermer le dernier contentStream
            contentStream.close();

            // Sauvegarder le document
            document.save(file);
            System.out.println("PDF créé avec succès : " + file.getAbsolutePath());

            // Vérifier si le fichier existe
            if (file.exists()) {
                System.out.println("Le fichier a été créé avec succès : " + file.getAbsolutePath());
            } else {
                System.out.println("Le fichier n'a pas été créé.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la création du PDF : " + e.getMessage());
        }
    }
    private TextField createTextField(String text) {
        TextField textField = new TextField(text);
        textField.setStyle("-fx-padding: 5; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        return textField;
    }

}
