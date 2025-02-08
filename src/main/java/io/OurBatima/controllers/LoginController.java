package io.OurBatima.controllers;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import io.OurBatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.OurBatima.core.impl.Layout;
import io.OurBatima.core.interfaces.ActionView;
import io.OurBatima.core.interfaces.Loader;
import io.OurBatima.core.model.Utilisateur;
import io.OurBatima.core.services.LoadViews;
import io.OurBatima.core.view.View;
import io.OurBatima.core.view.layout.LoadCircle;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class LoginController extends ActionView  implements ProfileCompletionController.OnSaveListener{
    private Stage popupStage; // Nouvelle fenêtre

    @FXML private WebView webView;
    private WebEngine webEngine;

    @Override
    public void onSaveSuccess(Utilisateur updatedUser) {
        refreshMainNavigation();

    }

    private static class GoogleAuthConfig {
        static final String CLIENT_ID = "1056028895782-ibvotqkd1vg0pb4brccd4eq7tq4h5qrp.apps.googleusercontent.com";
        static final String CLIENT_SECRET = "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";
        static final List<String> SCOPES = List.of(
                "openid",
                "https://www.googleapis.com/auth/userinfo.email", // Scope explicite pour l'email
                "https://www.googleapis.com/auth/userinfo.profile" // Scope explicite pour le nom
        );
        static final String REDIRECT_URI = "http://localhost:8080"; // Corrigé
    }

    // Classe pour stocker les infos utilisateur Google
    public static class GoogleUserInfo {
        private String sub;

        private String name; // Nom complet
        @SerializedName("given_name")
        private String givenName; // camelCase pour Java
        @SerializedName("family_name")
        private String familyName; // camelCase pour Java
        private String picture;
        private String email;
        public String getFullName() { return name; }
        // Setters pour corrections manuelles
        public void setEmail(String email) { this.email = email; }


        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getGivenName() { return givenName; } // Accorde avec le nom du champ
        public String getFamilyName() { return familyName; }
    }
    private Utilisateur createUserFromGoogleInfo(GoogleUserInfo info) {
        if (info.getEmail() == null) { // Blocage strict si email manquant
            throw new IllegalArgumentException("EMAIL GOOGLE OBLIGATOIRE NON FOURNI");
        }

        String nom = info.getFamilyName() != null
                ? info.getFamilyName()
                : (info.getFullName() != null ? info.getFullName() : "Nom");
        String prenom = info.getGivenName() != null
                ? info.getGivenName()
                : (info.getFullName() != null ? info.getFullName() : "Prénom");
        return new Utilisateur(0, nom, prenom, info.getEmail(), "", "", "", "activé", true, "USER");

    }

    private void showLoadingIndicator(boolean show) {
        Platform.runLater(() -> {
            if(show) {
                LoadCircle loader = new LoadCircle("Connexion en cours...");
                mainPane.getChildren().add(loader);
            } else {
                mainPane.getChildren().removeIf(node -> node instanceof LoadCircle);
            }
        });
    }

    @FXML
    private void handleGoogleLogin() {
        System.out.println("URL de redirection configurée : " + GoogleAuthConfig.REDIRECT_URI);
        System.out.println("Client ID : " + GoogleAuthConfig.CLIENT_ID);
        showLoadingIndicator(true);

        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Connexion Google");
        // Créer le WebView
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Configurer la taille
        webView.setPrefSize(1000, 1000);

        // Gérer la redirection
        webEngine.locationProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.startsWith(GoogleAuthConfig.REDIRECT_URI)) {
                handleOAuthResponse(newVal);
                popupStage.close(); // Fermer la popup après connexion
            }
        });


        String authUrl = new GoogleAuthorizationCodeRequestUrl(
                GoogleAuthConfig.CLIENT_ID,
                GoogleAuthConfig.REDIRECT_URI,
                GoogleAuthConfig.SCOPES)
                .setAccessType("offline")
                .set("prompt", "consent") // ✅ Paramètre personnalisé
                .build();

        webEngine.load(authUrl);
        // Afficher la popup
        Scene scene = new Scene(new StackPane(webView));
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void handleOAuthResponse(String url) {
        try {
            // 1. Extraction du code d'autorisation
            String code = URLDecoder.decode(url.split("code=")[1].split("&")[0], StandardCharsets.UTF_8);

            // 2. Échange du code contre un token
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    GoogleAuthConfig.CLIENT_ID,
                    GoogleAuthConfig.CLIENT_SECRET,
                    code,
                    GoogleAuthConfig.REDIRECT_URI
            ).execute();

            // Configuration COMPLÈTE du Credential
            Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .setTransport(new NetHttpTransport())
                    .setJsonFactory(new GsonFactory())
                    .setClientAuthentication(new ClientParametersAuthentication(
                            GoogleAuthConfig.CLIENT_ID,
                            GoogleAuthConfig.CLIENT_SECRET
                    ))
                    .setTokenServerUrl(new GenericUrl("https://oauth2.googleapis.com/token"))
                    .build()
                    .setFromTokenResponse(tokenResponse);


            // 4. Récupération des infos utilisateur
            GoogleUserInfo userInfo = fetchGoogleUserInfo(credential, tokenResponse.getIdToken());
            processGoogleUser(userInfo);

        } catch (IOException e) {
            showError("Erreur Google API : " + e.getMessage());
            e.printStackTrace();
        }
    }    // Méthode utilitaire pour afficher les erreurs
    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            showLoadingIndicator(false);
        });
    }
    private String extractEmailFromJwt(String idToken) {
        try {
            // Découper le JWT en 3 parties
            String[] jwtParts = idToken.split("\\.");
            if (jwtParts.length != 3) {
                throw new IllegalArgumentException("JWT invalide");
            }

            // Décoder la partie payload (Base64 URL-safe)
            byte[] payloadBytes = Base64.getUrlDecoder().decode(jwtParts[1]);
            String payload = new String(payloadBytes, StandardCharsets.UTF_8);

            // Extraire l'email depuis le JSON
            JsonObject jsonPayload = JsonParser.parseString(payload).getAsJsonObject();
            return jsonPayload.get("email").getAsString();

        } catch (Exception e) {
            System.err.println("Erreur JWT : " + e.getMessage());
            return null;
        }
    }

    private GoogleUserInfo fetchGoogleUserInfo(Credential credential, String idToken) throws IOException {
        // 1. Créer la requête avec le parser JSON et le credential
        HttpRequestFactory requestFactory = credential.getTransport().createRequestFactory(
                new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) throws IOException {
                        credential.initialize(request); // ✅ Initialisation explicite du credential
                        request.setParser(new JsonObjectParser(new GsonFactory()));
                    }
                }
        );

        // 2. Construire la requête
        HttpRequest request = requestFactory.buildGetRequest(
                new GenericUrl("https://www.googleapis.com/oauth2/v3/userinfo")
        );

        // 3. Journaliser les en-têtes pour vérification
        System.out.println("Headers de la requête: " + request.getHeaders().getAuthorization());

        // 4. Exécuter et parser
        HttpResponse response = request.execute();
        GoogleUserInfo userInfo = response.parseAs(GoogleUserInfo.class);

        // 5. Fallback JWT si nécessaire
        if (userInfo.getEmail() == null) {
            userInfo.setEmail(extractEmailFromJwt(idToken));
        }

        return userInfo;
    }
    private void processGoogleUser(GoogleUserInfo userInfo) {
        try {
            Utilisateur utilisateur = utilisateurDAO.getUserByEmail(userInfo.getEmail());

            if (utilisateur == null) {
                // Création utilisateur sans mot de passe
                utilisateur = createUserFromGoogleInfo(userInfo);
                utilisateur.setMotDePasse(""); // Marqueur spécial
                utilisateurDAO.saveUser(utilisateur);

                // Redirection vers complétion profil
                redirectToProfileCompletion(utilisateur);
            } else if (utilisateur.getMotDePasse().isEmpty()) {
                // Cas où l'utilisateur existe mais n'a pas complété son profil
                redirectToProfileCompletion(utilisateur);
            } else {
                // Connexion directe
                navigateToDashboard();
            }
        } catch (Exception e) {
            showError("Erreur : " + e.getMessage());
        }
    }
    private void navigateToDashboard() {
        Layout layout = new Layout(context); // Assurez-vous que Layout est bien initialisé selon votre architecture
        context.setLayout(layout);

        Loader loadCircle = new LoadCircle("Starting..", "");
        Task<View> loadViews = new LoadViews(context, loadCircle); // Tâche de chargement de la vue

        Thread tLoadViews = new Thread(loadViews);
        tLoadViews.setDaemon(true);
        tLoadViews.start();

        layout.setContent((Node) loadCircle);

        loadViews.setOnSucceeded(event -> {
            layout.setNav(context.routes().getView("drawer"));
            context.routes().nav("dash");
        });
    }

    private void redirectToProfileCompletion(Utilisateur user) {
        try {
            URL fxmlUrl = getClass().getResource("/OurBatima/views/pages/ProfileCompletion.fxml");
            if (fxmlUrl == null) {
                throw new IllegalStateException("Fichier FXML introuvable !");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            ProfileCompletionController controller = loader.getController();
            controller.setContext(context);
            controller.setUser(user);

            // Création d'une popup modale
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Bloque l'interaction avec la fenêtre parente
            popupStage.initOwner(mainPane.getScene().getWindow()); // Définit la fenêtre parente
            popupStage.setScene(new Scene(root));
            popupStage.setTitle("Compléter votre profil");

            // Optionnel : Personnaliser le style de la popup
            // popupStage.initStyle(StageStyle.UTILITY);

            // Fermeture propre lors de la complétion
            controller.setOnCompletionSuccess(() ->{

                popupStage.close();
                SessionManager.setUtilisateur(controller.getUpdatedUser()); // Si nécessaire
                refreshMainNavigation();
            });


            popupStage.showAndWait(); // Affiche et attend la fermeture

        } catch (IOException | IllegalStateException e) {
            showError("ERREUR FATALE : " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Références FXML existantes
    @FXML private StackPane mainPane;
    @FXML private ImageView logoView;
    @FXML private VBox loginContent;
    @FXML private TextField username;

    @FXML private PasswordField password;
    @FXML private ToggleButton togglePassword;
    @FXML private Label errorLabel;

    // Nouveaux éléments pour la connexion sociale et fonctionnalités additionnelles
    @FXML private Button btn_google;
    @FXML private Button btn_microsoft;
    @FXML private Button btn_createAccount;  // Déjà présent dans le pied de page
    @FXML private CheckBox rememberMe;
    @FXML private Label forgotPassword;

    // États et configurations existants
    private int loginAttempts = 0;
    private Timeline lockTimer;
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    private ResourceBundle bundle;

    private final Pattern USERNAME_PATTERN = Pattern.compile("^\\w{5,}$");
    private final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");

    @Override
    public void onEnter() {
        initializeComponents();
        setupAnimations();
        setupFocusEffects();
        setupPasswordToggle();
        setupInputValidation();
        initializeWebView();
        webEngine = webView.getEngine();



        // Nouveaux ajouts
        setupSocialLogins();
        applyEntryAnimation();
    }
    public void initialize() { // Méthode standard JavaFX
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        btn_google.setOnAction(event -> handleGoogleLogin());

    }


    /**
     * Initialisation des composants (exemple : paramètres de langue).
     */
    private void initializeComponents() {
        bundle = ResourceBundle.getBundle("messages_fr");
        username.setPromptText(bundle.getString("username.label"));
        password.setPromptText(bundle.getString("password.label"));
        errorLabel.setText("");
    }
    private void initializeWebView() {
        webEngine.locationProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.startsWith("http://localhost:8080/?code=")) {
                handleOAuthResponse(newVal);
                webView.setVisible(false); // Masquer après la redirection
            }
        });
    }

    /**
     * Animation en fond du formulaire.
     */
    private void setupAnimations() {
        Timeline backgroundAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(loginContent.styleProperty(),
                                "-fx-background-color: linear-gradient(to bottom right, rgba(255,255,255,0.6), rgba(245,245,245,0.5));")),
                new KeyFrame(Duration.seconds(5),
                        new KeyValue(loginContent.styleProperty(),
                                "-fx-background-color: linear-gradient(to top left, rgba(255,255,255,0.6), rgba(245,245,245,0.5));"))
        );
        backgroundAnimation.setCycleCount(Animation.INDEFINITE);
        backgroundAnimation.play();
    }

    /**
     * Ajout d'effets visuels lors de la focalisation sur les champs.
     */
    private void setupFocusEffects() {
        Consumer<TextField> focusEffect = field -> {
            DropShadow glow = new DropShadow(10, Color.DODGERBLUE);
            glow.setInput(new Glow(0.1));

            field.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if(newVal) {
                    field.setEffect(glow);
                } else {
                    field.setEffect(null);
                }
            });
        };

        focusEffect.accept(username);
        focusEffect.accept(password);
    }

    /**
     * Mise en place du toggle permettant d'afficher/masquer le mot de passe.
     */
    private void setupPasswordToggle() {
        TextField passwordVisible = new TextField();
        passwordVisible.setManaged(false);
        passwordVisible.setVisible(false);

        // Synchronisation entre PasswordField et TextField visible
        Bindings.bindBidirectional(password.textProperty(), passwordVisible.textProperty());

        togglePassword.selectedProperty().addListener((obs, old, val) -> {
            password.setManaged(!val);
            password.setVisible(!val);
            passwordVisible.setManaged(val);
            passwordVisible.setVisible(val);
        });

        // Ajout du champ visible dans le même conteneur que le PasswordField
        ((HBox) password.getParent()).getChildren().add(passwordVisible);
    }

    /**
     * Vérification du format des entrées utilisateur.
     */
    private void setupInputValidation() {
        username.textProperty().addListener((obs, old, val) ->
                updateValidationState(username, USERNAME_PATTERN.matcher(val).matches()));
        password.textProperty().addListener((obs, old, val) ->
                updateValidationState(password, PASSWORD_PATTERN.matcher(val).matches()));
    }

    /**
     * Mise à jour de l'état de validation via des pseudo-classes CSS.
     */
    private void updateValidationState(Control field, boolean isValid) {
        field.pseudoClassStateChanged(PseudoClass.getPseudoClass("invalid"), !isValid);
        field.pseudoClassStateChanged(PseudoClass.getPseudoClass("valid"), isValid);
    }

    /**
     * Gestion de la connexion classique.
     */
    @FXML
    private void login() {
        if(loginAttempts >= 3) {
            showLockWarning();
            return;
        }

        if(validateCredentials()) {
            analyticsTrack("connexion_classique");
            // Simulation d'un appel à un service d'authentification
            simulateServiceCall(() -> {
                System.out.println("Connexion réussie pour l'utilisateur : " + username.getText());
                errorLabel.setText(""); // efface les messages d'erreur
                // Par exemple, réinitialisation de la vue ou redirection
                context.routes().reset();
            });
        } else {
            handleFailedLogin();
        }
    }


    /**
     * Vérifie que les champs respectent le format attendu.
     */
    private boolean validateCredentials() {
        boolean validUsername = USERNAME_PATTERN.matcher(username.getText()).matches();
        boolean validPassword = PASSWORD_PATTERN.matcher(password.getText()).matches();

        if(!validUsername) {
            errorLabel.setText(bundle.getString("error.username_invalid"));
        } else if(!validPassword) {
            errorLabel.setText(bundle.getString("error.password_invalid"));
        }
        return validUsername && validPassword;
    }

    /**
     * Gère les tentatives de connexion infructueuses.
     */
    private void handleFailedLogin() {
        loginAttempts++;
        errorLabel.setText(bundle.getString("error.attempts") + (3 - loginAttempts));
        animateShake();
        if(loginAttempts >= 3) showLockWarning();
    }

    /**
     * Animation de secousse en cas d'erreur.
     */
    private void animateShake() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(70), loginContent);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.play();
    }

    /**
     * Affiche un message d'erreur et verrouille la connexion temporairement.
     */
    private void showLockWarning() {
        errorLabel.setText(bundle.getString("error.lock"));
        lockTimer = new Timeline(
                new KeyFrame(Duration.minutes(5), e -> {
                    loginAttempts = 0;
                    errorLabel.setText("");
                })
        );
        lockTimer.play();
    }

    // ----------------- Méthodes d'interaction hover -----------------

    @FXML
    void handleHover(MouseEvent event) {
        createFadeTransition(1.0);
        applyLogoEffect(new DropShadow(15, Color.GOLD));
        scaleNode(logoView, 1.1);
    }

    @FXML
    void handleUnhover(MouseEvent event) {
        createFadeTransition(0.8);
        applyLogoEffect(new Glow(0.3));
        scaleNode(logoView, 1.0);
    }

    private FadeTransition createFadeTransition(double toValue) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), mainPane);
        ft.setToValue(toValue);
        ft.setInterpolator(Interpolator.EASE_OUT);
        ft.play();
        return ft;
    }

    private void applyLogoEffect(Effect effect) {
        logoView.setEffect(effect);
    }

    private void scaleNode(Node node, double scale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setToX(scale);
        st.setToY(scale);
        st.play();
    }

    // ----------------- Nouveaux ajouts -----------------

    /**
     * Configure les gestionnaires d'évènements pour la connexion sociale.
     */
    private void setupSocialLogins() {
        if(btn_google != null) {
            btn_google.setOnAction(e -> {
                analyticsTrack("connexion_google");
                handleGoogleLogin();
            });
        }
        if(btn_microsoft != null) {
            btn_microsoft.setOnAction(e -> {
                analyticsTrack("connexion_microsoft");
                handleMicrosoftLogin();
            });
        }
    }

    /**
     * Traitement de la connexion via Google.
     */


    /**
     * Traitement de la connexion via Microsoft.
     */
    private void handleMicrosoftLogin() {
        System.out.println("Connexion via Microsoft initiée.");
        simulateServiceCall(() -> {
            System.out.println("Connexion via Microsoft réussie.");
            errorLabel.setText("");
            context.routes().reset();
        });
    }
    // Nouvelle méthode dans le main controller
    private void refreshMainNavigation() {
        // Recréer le layout principal si nécessaire
        Layout layout = new Layout(context);
        context.setLayout(layout);

        Loader loadCircle = new LoadCircle("Starting..", "");
        Task<View> loadViews = new LoadViews(context, loadCircle); // Load View task


        Thread tLoadViews = new Thread(loadViews);
        tLoadViews.setDaemon(true);
        tLoadViews.start();

        layout.setContent((Node) loadCircle);

        loadViews.setOnSucceeded(event -> {
            layout.setNav(context.routes().getView("drawer"));
            context.routes().nav("dash");
        });
    }
    private void applyEntryAnimation() {
        FadeTransition ft = new FadeTransition(Duration.millis(800), loginContent);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    /**
     * Simulation d'un appel à un service asynchrone (ex. authentification).
     */
    private void simulateServiceCall(Runnable onSuccess) {
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(event -> onSuccess.run());
        pause.play();
    }

    /**
     * Simule le suivi analytics d'un événement.
     * @param event L'événement à tracer.
     */
    private void analyticsTrack(String event) {
        System.out.println("Analytics event: " + event);
    }

    /**
     * Traitement du clic sur le lien « Mot de passe oublié ? ».
     */
    @FXML
    private void handleForgotPassword() {
        System.out.println("Lien 'Mot de passe oublié' activé.");
        // Ici vous pouvez afficher un dialogue ou rediriger vers une vue dédiée à la récupération
        // Par exemple : context.routes().navigate("ForgotPasswordView");
        // Pour cet exemple, on simule l'ouverture d'un dialogue simple :
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Récupération de mot de passe");
        alert.setHeaderText(null);
        alert.setContentText("Cette fonctionnalité permettra de récupérer votre mot de passe.");
        alert.showAndWait();
    }
    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String motDePasse = passwordField.getText();

        Utilisateur utilisateur = utilisateurDAO.verifierIdentifiants(email, motDePasse);

        if (utilisateur != null) {
            // Stocker l'utilisateur dans la session
            SessionManager.setUtilisateur(utilisateur);

            Layout layout = new Layout(context);
            context.setLayout(layout);

            Loader loadCircle = new LoadCircle("Starting..", "");
            Task<View> loadViews = new LoadViews(context, loadCircle); // Load View task

            Thread tLoadViews = new Thread(loadViews);
            tLoadViews.setDaemon(true);
            tLoadViews.start();

            layout.setContent((Node) loadCircle);

            loadViews.setOnSucceeded(event -> {
                layout.setNav(context.routes().getView("drawer"));
                context.routes().nav("dash");
            });
        } else {
            // Afficher un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de connexion");
            alert.setHeaderText("Identifiants incorrects");
            alert.setContentText("Veuillez vérifier votre email et votre mot de passe.");
            alert.showAndWait();
        }
    }




    public void goToRegister(javafx.event.ActionEvent actionEvent) {
        try {
            context.routes().setView("register"); // Navigation vers la vue 'register' définie dans views.yml
            System.out.println("Navigation vers la page d'inscription réussie !");
        } catch (Exception e) {
            System.err.println("Erreur lors de la navigation vers la page d'inscription : " + e.getMessage());
            e.printStackTrace();
        }
    }
}