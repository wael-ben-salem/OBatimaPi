package io.ourbatima.core.Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/ourbatimapi";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);  // Nouvelle connexion à chaque appel
        } catch (ClassNotFoundException e) {
            System.err.println("Le driver MySQL n'a pas été trouvé : " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
            System.err.println("URL: " + URL);
            System.err.println("Utilisateur: " + USER);
        }
        return null;
    }
}
