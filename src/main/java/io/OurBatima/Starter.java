

package io.OurBatima;

import io.OurBatima.core.Dao.DatabaseConnection;
import io.OurBatima.core.Dao.Projet.ProjetDAO;
import io.OurBatima.core.Main;
import io.OurBatima.core.model.Projet;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;

import static io.OurBatima.core.Dao.DatabaseConnection.getConnection;


public class Starter extends Main {

    public static void main(String[] args) {

        launch(args);
    }

}
