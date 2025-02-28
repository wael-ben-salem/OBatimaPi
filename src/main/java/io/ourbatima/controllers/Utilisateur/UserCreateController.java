package io.ourbatima.controllers.Utilisateur;

import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.model.Utilisateur.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserCreateController {
    @FXML private TextField nomField;
    @FXML
    private TextField prenomField;

    @FXML
    private PasswordField motDePasseField;

    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private TextField adresseField;
    @FXML private ComboBox<Utilisateur.Role> roleComboBox;

    @FXML private ComboBox<Artisan.Specialite> SpecialiteBox;

    @FXML private VBox specificDetails;
    @FXML private TextField specialiteField;
    @FXML private TextField salaireField;
    private UtilisateurDAO userDao = new UtilisateurDAO();

    @FXML
    public void initialize() {
        // Remplir la ComboBox avec les rôles disponibles
        roleComboBox.getItems().setAll(Utilisateur.Role.values());
        SpecialiteBox.getItems().setAll(Artisan.Specialite.values());

        roleComboBox.valueProperty().addListener((obs, oldRole, newRole) -> updateSpecificDetails(newRole));

        specificDetails.setVisible(false);
        SpecialiteBox.setVisible(false);
        specialiteField.setVisible(false);
    }

    private void updateSpecificDetails(Utilisateur.Role role) {
        if (role == Utilisateur.Role.Artisan) {
            specificDetails.setVisible(true);
            SpecialiteBox.setVisible(true);
            specialiteField.setVisible(false);
        } else if (role == Utilisateur.Role.Constructeur) {
            specificDetails.setVisible(true);
            SpecialiteBox.setVisible(false);
            specialiteField.setVisible(true);
        } else {
            specificDetails.setVisible(false);
        }
    }

    @FXML
    private void createUser() {
        // Récupération des champs
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String telephone = telephoneField.getText();
        String adresse = adresseField.getText();
        String motDePasse = motDePasseField.getText();
        Utilisateur.Role role = roleComboBox.getValue();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || telephone.isEmpty() || adresse.isEmpty() || motDePasse.isEmpty() || role == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        Utilisateur newUser = null;
        double salaire;



        if (role == Utilisateur.Role.Artisan) {
            salaire = Double.parseDouble(salaireField.getText());

            Artisan.Specialite artisanSpecialite = SpecialiteBox.getValue();
            if (salaire < 0) {
                showAlert("Erreur", "Le salaire ne peut pas être négatif.");
                return;
            }


            if (artisanSpecialite == null) {
                showAlert("Erreur", "Veuillez sélectionner une spécialité.");
                return;
            }

            Artisan artisan = new Artisan(0, nom, prenom, email, motDePasse, telephone, adresse, Utilisateur.Statut.en_attente, true, role, artisanSpecialite, salaire);
            newUser = artisan;

        } else if (role == Utilisateur.Role.Constructeur) {
            salaire = Double.parseDouble(salaireField.getText());

            String specialite = specialiteField.getText();
            if (salaire < 0) {
                showAlert("Erreur", "Le salaire ne peut pas être négatif.");
                return;
            }
            if (specialite.isEmpty()) {
                showAlert("Erreur", "Veuillez entrer une spécialité.");
                return;
            }

            Constructeur constructeur = new Constructeur(0, nom, prenom, email, motDePasse, telephone, adresse, Utilisateur.Statut.en_attente, true, role, 0, specialite, salaire);
            newUser = constructeur;

        } else if (role == Utilisateur.Role.GestionnaireStock) {
            GestionnaireDeStock gestionnaireDeStock = new GestionnaireDeStock(0, nom, prenom, email, motDePasse, telephone, adresse, Utilisateur.Statut.en_attente, true, role, 0);
            newUser = gestionnaireDeStock;

        }
           else if (role == Utilisateur.Role.Client) {
            Client client = new Client(0, nom, prenom, email, motDePasse, telephone, adresse, Utilisateur.Statut.en_attente, true, role,0);
            newUser = client;

        }else{
            Utilisateur utilisateur = new Utilisateur(0, nom, prenom, email, motDePasse, telephone, adresse, Utilisateur.Statut.en_attente, true, role);
            newUser = utilisateur;
        }

        // Sauvegarde en base de données
        boolean success = userDao.saveUser(newUser);

        if (success) {
            showAlert("Succès", "Utilisateur créé avec succès.");
            close();
        } else {
            showAlert("Erreur", "Échec de la création de l'utilisateur.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void close() {
        ((Stage) nomField.getScene().getWindow()).close();
    }
}
