package io.ourbatima.controllers.EtapeProjet;

import io.ourbatima.core.Dao.EtapeProjet.EtapeProjetDAO;
import io.ourbatima.core.Dao.Projet.ProjetDAO;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.model.EtapeProjet;
import io.ourbatima.core.model.Projet;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AjoutEtapeProjet extends ActionView {

    @FXML
    private TextField nomEtapeTextField;

    @FXML
    private TextField descripTextField;

    @FXML
    private TextField dateDebutTextField;

    @FXML
    private TextField DateFinTextField;

    @FXML
    private TextField montantTextField;

    @FXML
    private TextField nomProjetTextField;

    @FXML
    private Button ajouterButton;

    private final EtapeProjetDAO etapeProjetDAO = new EtapeProjetDAO();
    private final ProjetDAO projetDAO = new ProjetDAO();

    @FXML
    public void initialize() {
        ajouterButton.setOnAction(event -> ajouterEtapeProjet());
    }

    @FXML
    private void ajouterEtapeProjet() {
        try {
            String nomEtape = nomEtapeTextField.getText().trim();
            String description = descripTextField.getText().trim();
            String nomProjet = nomProjetTextField.getText().trim();

            if (nomEtape.isEmpty() || description.isEmpty() || nomProjet.isEmpty()) {
                showAlert("Champs obligatoires", "Veuillez remplir tous les champs requis.");
                return;
            }

            if (nomEtape.length() < 3 || nomEtape.length() > 100) {
                showAlert("Nom de l'étape invalide", "Le nom de l'étape doit contenir entre 3 et 100 caractères.");
                return;
            }

            if (!nomEtape.matches("^[a-zA-ZÀ-ÿ\\s\\-_0-9]+$")) {
                showAlert("Nom invalide", "Le nom ne doit contenir que des lettres, espaces, tirets, underscores et chiffres.");
                return;
            }


            BigDecimal montant = montantTextField.getText().isEmpty() ? BigDecimal.ZERO : new BigDecimal(montantTextField.getText());
            if (montant.compareTo(BigDecimal.ZERO) < 0) {
                showAlert("Montant invalide", "Le montant ne peut pas être négatif.");
                return;
            }


            int idProjet = projetDAO.getIdByNom(nomProjet);
            if (idProjet == -1) {
                showAlert("Projet non trouvé", "Le projet saisi n'existe pas !");
                return;
            }

            Date dateDebut = parseDate(dateDebutTextField.getText());
            Date dateFin = parseDate(DateFinTextField.getText());

            if (dateDebut == null) {
                showAlert("Format de date incorrect", "Veuillez entrer une date de début au format YYYY-MM-DD.");
                return;
            }

            if (dateFin == null) {
                showAlert("Format de date incorrect", "Veuillez entrer une date de fin au format YYYY-MM-DD.");
                return;
            }

            if (dateDebut.after(dateFin)) {
                showAlert("Erreur de date", "La date de début doit être avant la date de fin.");
                return;
            }

            EtapeProjet etapeProjet = new EtapeProjet(0, idProjet, nomEtape, description, dateDebut, dateFin, "En Attente", montant, null);
            etapeProjetDAO.addEtapeProjet(etapeProjet);

            showAlert("Succès", "Étape du projet ajoutée avec succès !");
            clearFields();

        } catch (NumberFormatException e) {
            showAlert("Format incorrect", "Veuillez entrer un montant valide.");
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur s'est produite: " + e.getMessage());
        }
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            LocalDate localDate = LocalDate.parse(dateStr);
            return Date.valueOf(localDate);
        } catch (DateTimeParseException e) {
            showAlert("Format de date incorrect", "Veuillez entrer une date au format YYYY-MM-DD.");
            return null;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nomEtapeTextField.clear();
        descripTextField.clear();
        dateDebutTextField.clear();
        DateFinTextField.clear();
        montantTextField.clear();
        nomProjetTextField.clear();
    }
}
