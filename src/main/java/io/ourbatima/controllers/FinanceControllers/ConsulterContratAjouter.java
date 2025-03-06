package io.ourbatima.controllers.FinanceControllers;

import io.ourbatima.controllers.SessionManager;
import io.ourbatima.core.Dao.FinanceService.ContratServise;
import io.ourbatima.core.model.Projet;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import io.ourbatima.core.model.financeModel.Contrat;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import java.io.File;
import java.sql.*;

import static io.ourbatima.core.Dao.DatabaseConnection.getConnection;
import static io.ourbatima.core.model.Utilisateur.Utilisateur.Role.Client;

public class ConsulterContratAjouter {
    public ImageView image;
    public Text datesinclt;
    private String img;
    public Text delai;
    public Text nmclt;
    public ImageView images;
    public Text dates;
    public Text nmclt1;
    public Text adresse;
    public Text tell;
    public Text emailll;
    public Button Signier;
    public VBox rootd;
    private String imagepath;
    private Contrat cont;
    @FXML
    private Text descriptionproj;
    private Projet projet;
    private Utilisateur utilisateur;
    private final ContratServise cs;
    @FXML
    private Button saveButton;

    @FXML
    private Text prixDeTravaux;
    @FXML
    private VBox root;
    public ConsulterContratAjouter() {
        this.cs = new ContratServise(); // Initialize the service
    }
public void onInit(){
        Utilisateur u = SessionManager.getUtilisateur();
        if (u.getRole()!= Client){
            this.rootd.getChildren().remove(Signier);

        }

}
    public void setdataCOntrat(Contrat ajouterContrats) {
        this.cont = ajouterContrats;
        loadData(); // Load data after setting the Contrat object
    }

    private void loadData() {
        if (cont == null) {
            System.err.println("Contrat object is not set. Call setdataCOntrat() first.");
            return;
        }
if (cont.getSignatureclient()!=null){
    rootd.getChildren().remove(Signier);
    if (cont.getSignatureclient() != null && !cont.getSignatureclient().isEmpty()) {
        File imageFile = new File(cont.getSignatureclient());
        if (imageFile.exists()) {
            Image imagea = new Image(imageFile.toURI().toString());
            image.setImage(imagea);
        } else {
            System.err.println("Image file not found: " + cont.getSignatureclient());
        }
    } else {
        System.err.println("Image path is not set.");
    }

}
        try {



            String id_client = cs.getClientNOMEtidbyidcontrat(cont.getIdProjet()).split("-")[0];
            utilisateur = cs.getclientbyid(Integer.parseInt(id_client));
            projet = cs.getProjetbyid(cont.getIdProjet());
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid client ID format: " + e.getMessage());
        }

        // Populate UI elements
        String desc = String.format(
                "Le présent contrat a pour objet la réalisation des travaux de construction suivants : %s a un Type d'architecture %s et le projet de type %s.",
                projet.getNomProjet(), projet.getStyleArch(), projet.getType()
        );
        String sinclt=String.format("Date Signature :"+cont.getDatesignatureClient());


        String prixtra = String.format(
                "Le montant total des travaux est fixé à %.2f DT, hors taxes.",
                cont.getMontantTotal()
        );
        String del = String.format(
                "Les travaux débuteront le %s et devront être achevés au plus tard le %s.",
                cont.getDateDebut(), cont.getDateFin()
        );
        String nmq = String.format("Nom : %s", utilisateur.getNom());
String telll=String.format("Téléphone : %s",utilisateur.getTelephone());
String adr=String.format("Adresse : %s",utilisateur.getAdresse());
String mall=String.format("Email : %s",utilisateur.getEmail());
emailll.setText(mall);
datesinclt.setText(sinclt);
adresse.setText(adr);


tell.setText(telll);
        descriptionproj.setText(desc);
        prixDeTravaux.setText(prixtra);
        delai.setText(del);
        nmclt.setText(nmq);
        nmclt1.setText(nmq);

        imagepath = cont.isSignatureElectronique();
        loadImage();

        String dato = String.format("Date : %s", cont.getDateSignature());
        dates.setText(dato);
    }

    private void loadImage() {
        if (imagepath != null && !imagepath.isEmpty()) {
            File imageFile = new File(imagepath);
            if (imageFile.exists()) {
                try {
                    Image image = new Image(imageFile.toURI().toString());
                    images.setImage(image);
                } catch (Exception e) {
                    System.err.println("Failed to load image: " + e.getMessage());
                }
            } else {
                System.err.println("Image file not found: " + imagepath);
            }
        } else {
            System.err.println("Image path is not set.");
        }
    }

    // The root VBox in your FXML

    @FXML
    private void dowload(ActionEvent event) {
        root.getChildren().remove(saveButton);
        root.getChildren().remove(Signier);
        // Step 1: Take a snapshot of the VBox
        WritableImage snapshot = root.snapshot(new SnapshotParameters(), null);
        root.getChildren().add(saveButton);

        // Step 2: Convert the snapshot to a BufferedImage
        java.awt.image.BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

        // Step 3: Create a PDF document
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle((float) root.getWidth(), (float) root.getHeight()));
            document.addPage(page);

            // Step 4: Add the image to the PDF
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(
                    document,
                    toByteArray(bufferedImage, "png"),
                    "contract"
            );

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pdImage, (float) 0, (float) 0, (float) root.getWidth(), (float) root.getHeight());
            }

            // Step 5: Save the PDF to a file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                document.save(file);
                System.out.println("PDF saved to: " + file.getAbsolutePath());
            }
            if (img !=null && SessionManager.getUtilisateur().getRole()==Client){
                try (Connection conn = getConnection()){
                    String sql = "UPDATE contrat SET signatureclient = ?, DatesignatureClient = ? WHERE id_contrat = ?";

                    PreparedStatement pstmt = conn.prepareStatement(sql);


                        // Set the parameters
                        pstmt.setString(1, img); // Set the client signature
                        pstmt.setDate(2, new java.sql.Date(System.currentTimeMillis())); // Set the date signature
                        pstmt.setInt(3, cont.getIdContrat()); // Set the contrat ID
                    int rs =pstmt.executeUpdate();





                    } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }


            } catch (IOException e) {
            e.printStackTrace();
        }


        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();


    }

    // Helper method to convert BufferedImage to byte array
    private byte[] toByteArray(java.awt.image.BufferedImage image, String format) throws IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }

    public void signier(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourbatima/views/pages/Finance_vews/updatesignature.fxml"));
            Parent root = loader.load();
            updatesignature updatesignatur = loader.getController();
            updatesignatur.setAjouterContratsController(this);


            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recharger la liste des utilisateurs après création

        } catch (IOException e) {
            e.printStackTrace();

        }



    }

    public void setimagePath(String imagepath) {
        this.img=imagepath ;
        loadImg();    }



    private void loadImg() {
        if (img != null && !img.isEmpty()) {
            File imageFile = new File(img);
            if (imageFile.exists()) {
                Image imagea = new Image(imageFile.toURI().toString());
                image.setImage(imagea);
            } else {
                System.err.println("Image file not found: " + img);
            }
        } else {
            System.err.println("Image path is not set.");
        }
    }





    }


