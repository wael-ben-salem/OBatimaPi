package io.ourbatima.controllers.FinanceControllers;

import io.ourbatima.core.Dao.FinanceService.ContratServise;
import io.ourbatima.core.model.Projet;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import io.ourbatima.core.model.financeModel.Contrat;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
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
import java.sql.SQLException;

public class ConsulterContratAjouter {
    public Text delai;
    public Text nmclt;
    public ImageView images;
    public Text dates;
    public Text nmclt1;
    public Text adresse;
    public Text tell;
    public Text emailll;
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

    public void setdataCOntrat(Contrat ajouterContrats) {
        this.cont = ajouterContrats;
        loadData(); // Load data after setting the Contrat object
    }

    private void loadData() {
        if (cont == null) {
            System.err.println("Contrat object is not set. Call setdataCOntrat() first.");
            return;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to convert BufferedImage to byte array
    private byte[] toByteArray(java.awt.image.BufferedImage image, String format) throws IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }
}

