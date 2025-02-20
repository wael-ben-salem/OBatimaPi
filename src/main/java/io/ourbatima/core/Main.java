package io.ourbatima.core;


import io.ourbatima.core.impl.Layout;
import io.ourbatima.core.interfaces.Loader;
import io.ourbatima.core.services.LoadViews;
import io.ourbatima.core.view.View;
import io.ourbatima.core.view.layout.LoadCircle;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.sql.Connection;

import static io.ourbatima.core.Dao.DatabaseConnection.getConnection;

public class Main extends Launcher {


    @Override
    public void build(Context context) {

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
            context.routes().nav("addstock");

            layout.setContent(null);

            View loginView = context.routes().getView("login");
            layout.setContent(loginView.getRoot());



        });

        icons.add(new Image(context.getResource("style/img/logo_64.png").toExternalForm(), 128, 128, true, true));

//        layout.setContent((Node) loadCircle);
    }

}