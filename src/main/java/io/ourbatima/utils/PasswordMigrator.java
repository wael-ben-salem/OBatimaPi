package io.ourbatima.utils;

import io.ourbatima.core.Dao.Utilisateur.UtilisateurDAO;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PasswordMigrator {

    private final UtilisateurDAO utilisateurDAO;

    public PasswordMigrator() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public void migrateAllPasswords() {
        System.out.println("Début de la migration des mots de passe...");

        try {
            // Vérifier si la migration a déjà été faite
            Path lockFile = Paths.get("password_migration.lock");
            if (Files.exists(lockFile)) {
                System.out.println("Migration déjà effectuée (lock file présent)");
                return;
            }

            List<Utilisateur> allUsers = utilisateurDAO.getAllUsers();
            int migrated = 0;

            for (Utilisateur user : allUsers) {
                String currentPassword = user.getMotDePasse();

                // Vérifier si le mot de passe n'est pas déjà au format BCrypt
                if (currentPassword == null || !currentPassword.startsWith("$2")) {
                    try {
                        // Hasher avec BCrypt (coût 12 pour correspondre à Symfony)
                        String newHash = BCrypt.hashpw(currentPassword, BCrypt.gensalt(12));

                        // Mettre à jour en base
                        boolean success = utilisateurDAO.updateUserPassword(user.getEmail(), newHash);

                        if (success) {
                            migrated++;
                            System.out.println("Migré: " + user.getEmail());
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur pour " + user.getEmail() + ": " + e.getMessage());
                    }
                }
            }

            // Créer le fichier lock après migration réussie
            Files.createFile(lockFile);
            System.out.println("Migration terminée. " + migrated + "/" + allUsers.size() + " mots de passe migrés.");

        } catch (Exception e) {
            System.err.println("ERREUR GRAVE lors de la migration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new PasswordMigrator().migrateAllPasswords();
    }
}