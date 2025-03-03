    package io.ourbatima.controllers;

    import org.opencv.core.*;
    import org.opencv.imgcodecs.Imgcodecs;
    import org.opencv.imgproc.Imgproc;
    import org.opencv.objdetect.CascadeClassifier;
    import org.opencv.videoio.VideoCapture;
    import javax.imageio.ImageIO;
    import java.awt.image.BufferedImage;
    import java.io.ByteArrayInputStream;
    import java.io.File;
    import java.io.IOException;
    import java.io.InputStream;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.nio.file.StandardCopyOption;

    public class FaceAuthenticator {


        static {
            try {
                // Charger la DLL depuis les ressources
                InputStream dllStream = FaceAuthenticator.class.getResourceAsStream("/libs/opencv_java455.dll");
                File tempDll = File.createTempFile("opencv_", ".dll");
                Files.copy(dllStream, tempDll.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.load(tempDll.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException("Erreur de chargement OpenCV", e);
            }
        }
        public static byte[] captureFace() {

            VideoCapture capture = new VideoCapture(0);

            Mat frame = new Mat();
            byte[] faceData = null;

            try {
                // 1. Vérifier l'accès à la caméra
                if (!capture.isOpened() || !capture.read(frame)) {
                    System.err.println("Erreur: Aucun flux vidéo détecté");
                    return null;
                }

                // 2. Conversion en niveaux de gris
                Mat grayFrame = new Mat();
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                // 3. Chargement du classificateur Haar
                InputStream xmlStream = FaceAuthenticator.class.getResourceAsStream("/libs/haarcascade_frontalface_alt.xml");
                File tempXml = File.createTempFile("haarcascade_", ".xml");
                Files.copy(xmlStream, tempXml.toPath(), StandardCopyOption.REPLACE_EXISTING);

                CascadeClassifier detector = new CascadeClassifier(tempXml.getAbsolutePath());
                if (detector.empty()) throw new IOException("Classificateur non chargé");

                // 4. Détection faciale
                MatOfRect faces = new MatOfRect();
                detector.detectMultiScale(
                        grayFrame, faces,
                        1.1, 3, 0,
                        new Size(100, 100),
                        new Size(500, 500)
                );

                // 5. Extraction du visage
                if (!faces.empty()) {
                    Rect firstFace = faces.toArray()[0];
                    Mat faceRegion = new Mat(grayFrame, firstFace);

                    // Redimensionnement et encodage
                    Mat resized = new Mat();
                    Imgproc.resize(faceRegion, resized, new Size(150, 150));

                    MatOfByte buffer = new MatOfByte();
                    if (Imgcodecs.imencode(".jpg", resized, buffer)) {
                        faceData = buffer.toArray();
                        Files.write(Paths.get("debug_capture.jpg"), faceData);

                    }
                }


            } catch (Exception e) {
                System.err.println("Erreur capture: " + e.getMessage());
            } finally {
                if (capture.isOpened()) {
                    capture.release();
                    capture = null; // Aide le GC
                    System.gc(); // Force le nettoyage mémoire (Windows spécifique)

                }
            }
            return faceData;
        }
        public static boolean compareFaces(byte[] face1, byte[] face2) {
            if (face1 == null || face2 == null || face1.length == 0 || face2.length == 0) {
                System.err.println("Erreur : Données faciales invalides (null ou vides)");
                return false;
            }
            // Convertir les byte[] en Mat pour OpenCV
            Mat mat1 = decodeImage(face1);
            Mat mat2 = decodeImage(face2);
            if (mat1.empty() || mat2.empty()) {
                System.err.println("Erreur : Échec du décodage des images");
                return false;
            }
            // Redimensionner les images pour uniformiser la taille
            Size size = new Size(100, 100);
            try {
                Imgproc.resize(mat1, mat1, size);
                Imgproc.resize(mat2, mat2, size);
            } catch (Exception e) {
                System.err.println("Erreur de redimensionnement : " + e.getMessage());
                return false;
            }
            Imgproc.resize(mat1, mat1, size);
            Imgproc.resize(mat2, mat2, size);

            // Calculer la différence absolue entre les deux images
            Mat diff = new Mat();
            Core.absdiff(mat1, mat2, diff);

            // Calculer la moyenne des différences
            Scalar meanDiff = Core.mean(diff);
            double similarityThreshold = 80.0; // Ajustez ce seuil selon vos besoins

            return meanDiff.val[0] < similarityThreshold;

        }
        private static Mat decodeImage(byte[] imageData) {
            MatOfByte matOfByte = new MatOfByte(imageData);
            try {
                return Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_GRAYSCALE);
            } catch (CvException e) {
                System.err.println("Erreur de décodage OpenCV : " + e.getMessage());
                return new Mat(); // Mat vide
            }
        }

        private static double calculateDifference(byte[] face1, byte[] face2) {
            // Implémentation basique (à améliorer avec des algorithmes plus avancés)
            int diff = 0;
            for (int i = 0; i < Math.min(face1.length, face2.length); i++) {
                diff += Math.abs(face1[i] - face2[i]);
            }
            return diff / (double) Math.max(face1.length, face2.length);
        }
    }