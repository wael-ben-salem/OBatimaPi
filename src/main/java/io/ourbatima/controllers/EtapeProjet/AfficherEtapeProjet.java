package io.ourbatima.controllers.EtapeProjet;

import io.ourbatima.controllers.projet.UpdateProjet;
import io.ourbatima.core.Dao.EtapeProjet.EtapeProjetDAO;
import io.ourbatima.core.Dao.Projet.ProjetDAO;
import io.ourbatima.core.Dao.Rapport.RapportDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.interfaces.Initializable;
import io.ourbatima.core.model.EtapeProjet;
import io.ourbatima.core.model.Projet;
import io.ourbatima.core.model.Rapport;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AfficherEtapeProjet extends ActionView implements Initializable {

    private final EtapeProjetDAO etapeProjetDAO = new EtapeProjetDAO();
    private final ProjetDAO projetDAO = new ProjetDAO();
    private final RapportDAO rapportDAO = new RapportDAO();

    @FXML private VBox etapeProjetContainer;
    @FXML private Button updateButton, deleteButton, searchButton;
    @FXML private ListView<String> listNomEtapes;
    @FXML private ListView<String> suggestionsProjetList;
    @FXML private TextArea etapeProjetDetails;
    @FXML private TextField searchField;

    @Override
    public void initialize() {
        Platform.runLater(() -> {
            System.out.println("✅ AfficherEtapeProjet Controller Initialized");
            System.out.println("🔍 listNomEtapes: " + listNomEtapes);
            System.out.println("📝 etapeProjetDetails: " + etapeProjetDetails);
            listNomEtapes.getItems().clear();
            loadEtapeList();
            setupClickListener();
            setupSearchListener();
            setupSuggestionClickListener();
            searchField.setOnMouseClicked(event -> {
                etapeProjetDetails.clear();
            });
        });

        listNomEtapes.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: 'Arial Rounded MT Bold'; -fx-font-size: 12px; -fx-font-weight: bold;");
                }
            }
        });
    }

    @FXML
    private void handleReload() {
        System.out.println("🔄 Reload button clicked!");
        Platform.runLater(() -> {
            listNomEtapes.getItems().clear();
            loadEtapeList();
            initialize();
        });
    }



    private void loadEtapeList() {
        List<EtapeProjet> etapes = etapeProjetDAO.getAllEtapeProjets();

        if (etapes.isEmpty()) {
            showAlert("Aucune étape", "Aucune étape de projet n'a été trouvée dans la base de données.");
        } else {
            List<String> etapeNames = etapes.stream()
                    .map(EtapeProjet::getNomEtape)
                    .collect(Collectors.toList());

            Platform.runLater(() -> {
                listNomEtapes.getItems().clear();
                listNomEtapes.getItems().setAll(etapeNames);
                if (!etapes.isEmpty()) {
                    showEtapeDetails(etapes.get(0));
                }
            });
        }
    }

    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEtapeList(newValue);
            showSuggestions(newValue);
        });
    }

    private void showSuggestions(String query) {
        if (query.isEmpty()) {
            suggestionsProjetList.setVisible(false); // Hide suggestions if the input is empty
            suggestionsProjetList.getItems().clear();
            return;
        }


        List<Projet> projets = projetDAO.getAllProjets(); // Assuming you have a method to get all projects
        List<String> filteredProjets = projets.stream()
                .filter(projet -> projet.getNomProjet().toLowerCase().contains(query.toLowerCase()))
                .map(Projet::getNomProjet)
                .collect(Collectors.toList());

        if (filteredProjets.isEmpty()) {
            suggestionsProjetList.setVisible(false); // Hide suggestions if no matches found
        } else {
            suggestionsProjetList.getItems().setAll(filteredProjets);
            suggestionsProjetList.setVisible(true); // Show suggestions if matches found
        }
    }
    @FXML
    private void handleSuggestionSelection() {
        String selectedSuggestion = suggestionsProjetList.getSelectionModel().getSelectedItem();
        if (selectedSuggestion != null) {
            searchField.setText(selectedSuggestion);
            suggestionsProjetList.setVisible(false);
            filterEtapeList(selectedSuggestion);
        }
    }
    private void setupSuggestionClickListener() {
        suggestionsProjetList.setOnMouseClicked(event -> handleSuggestionSelection());
    }


    private void filterEtapeList(String query) {
        List<EtapeProjet> etapes = etapeProjetDAO.getAllEtapeProjets();
        List<String> filteredEtapes = etapes.stream()
                .filter(etape -> {
                    Projet projet = projetDAO.getProjetById(etape.getId_projet());
                    return projet != null && projet.getNomProjet().toLowerCase().contains(query.toLowerCase());
                })
                .map(EtapeProjet::getNomEtape)
                .collect(Collectors.toList());

        Platform.runLater(() -> {
            listNomEtapes.getItems().clear();
            listNomEtapes.getItems().setAll(filteredEtapes);
        });
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            loadEtapeList();
        } else {
            List<EtapeProjet> filteredEtapes = etapeProjetDAO.getAllEtapeProjets().stream()
                    .filter(etape -> etape.getNomEtape().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
            updateListView(filteredEtapes);
            if (!filteredEtapes.isEmpty()) {
                showEtapeDetails(filteredEtapes.get(0));
            }
        }
    }

    private void updateListView(List<EtapeProjet> etapes) {
        List<String> etapeNames = etapes.stream()
                .map(EtapeProjet::getNomEtape)
                .collect(Collectors.toList());

        Platform.runLater(() -> {
            listNomEtapes.getItems().clear();
            listNomEtapes.getItems().setAll(etapeNames);
        });
    }



    private void setupClickListener() {
        listNomEtapes.setOnMouseClicked(event -> {
            String selectedEtape = listNomEtapes.getSelectionModel().getSelectedItem();
            System.out.println("📌 Clicked on: " + selectedEtape);

            if (selectedEtape != null) {
                EtapeProjet etape = etapeProjetDAO.getEtapeProjetByNom(selectedEtape);
                System.out.println("🔎 Retrieved EtapeProjet: " + etape);

                if (etape == null) {
                    System.out.println("⚠️ No matching Etape found in database!");
                } else {
                    System.out.println("✅ Found EtapeProjet: " + etape.getNomEtape());
                    showEtapeDetails(etape);
                }
            } else {
                System.out.println("❌ No item selected!");
            }
        });
    }


    private void showEtapeDetails(EtapeProjet etape) {
        if (etape == null) {
            System.out.println("⚠️ Cannot display details: etape is null!");
            etapeProjetDetails.setText("❌ Aucune donnée trouvée.");
            return;
        }

        System.out.println("📋 Displaying details for: " + etape.getNomEtape());

        String nomProjet = "Aucun projet associé";
        if (etape.getId_projet() > 0) {
            Projet projet = projetDAO.getProjetById(etape.getId_projet());
            if (projet != null) {
                nomProjet = projet.getNomProjet();
            } else {
                System.out.println("⚠️ No project found for ID: " + etape.getId_projet());
            }
        }

        String titre = "Aucun rapport associé";
        if (etape.getId_rapport() != null) {
            Rapport rapport = rapportDAO.getRapportById(etape.getId_rapport());
            if (rapport != null) {
                titre = rapport.getTitre();
            } else {
                System.out.println("⚠️ No report found for ID: " + etape.getId_rapport());
            }
        }

        String details = "📝  Nom Étape: " + etape.getNomEtape() + "\n" +"\n" +
                "📖  Description: " + etape.getDescription() + "\n" +"\n" +
                "📅  Date Début: " + etape.getDateDebut() + "\n" +"\n" +
                "📅  Date Fin: " + etape.getDateFin() + "\n" +"\n" +
                "📊  Statut: " + etape.getStatut() + "\n" +"\n" +
                "💰  Montant: " + etape.getMontant() + "\n" +"\n" +
                "🏢  Projet Associé: " + nomProjet + "\n" +"\n" +
                "📄  Rapport: " + titre;

        System.out.println("Details to display: " + details);
        etapeProjetDetails.setText(details);
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleUpdate() {
        String selectedEtape = listNomEtapes.getSelectionModel().getSelectedItem();
        if (selectedEtape != null) {
            System.out.println("✏️ Updating: " + selectedEtape);

            // Retrieve the selected EtapeProjet object
            EtapeProjet etape = etapeProjetDAO.getEtapeProjetByNom(selectedEtape);
            if (etape != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/EtapeProjet/updateEtapeProjet.fxml")); // Ensure this path is correct
                    Parent root = loader.load();

                    UpdateEtapeProjet updateEtapeProjetController = loader.getController();
                    updateEtapeProjetController.initializeWithEtapeProjet(etape);


                    Stage stage = new Stage();
                    stage.setTitle("Modifier Étape Projet");
                    stage.setScene(new Scene(root));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Échec du chargement de la fenêtre de mise à jour.");
                }
            } else {
                showAlert("Erreur", "Étape projet non trouvée.");
            }
        } else {
            showAlert("Aucune étape sélectionnée", "Veuillez sélectionner une étape à mettre à jour.");
        }
    }


    @FXML
    private void handleDelete() {
        String selectedEtape = listNomEtapes.getSelectionModel().getSelectedItem();
        if (selectedEtape != null) {
            System.out.println("🗑️ Suppression: " + selectedEtape);

            EtapeProjet etape = etapeProjetDAO.getEtapeProjetByNom(selectedEtape);
            if (etape != null) {
                // Afficher la boîte de dialogue de confirmation
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Confirmer la suppression");
                confirmationAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette étape ?");
                confirmationAlert.setContentText("Cette action est irréversible.");

                // Attendre la réponse de l'utilisateur
                Optional<ButtonType> result = confirmationAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Procéder à la suppression si confirmée
                    etapeProjetDAO.deleteEtapeProjet(etape.getId_etapeProjet());
                    loadEtapeList(); // Rafraîchir la liste
                    showAlert("Succès", "L'étape projet a été supprimée avec succès.");
                } else {
                    // Si l'utilisateur annule, afficher le message d'annulation
                    showAlert("Suppression annulée", "Aucune étape projet n'a été supprimée.");
                }
            } else {
                showAlert("Erreur de suppression", "Étape projet non trouvée pour la suppression.");
            }
        } else {
            showAlert("Erreur de sélection", "Aucune étape sélectionnée pour la suppression.");
        }
    }

}