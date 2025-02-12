package io.OurBatima.core;

import io.OurBatima.core.impl.Layout;
import io.OurBatima.core.interfaces.Loader;
import io.OurBatima.core.services.LoadViews;
import io.OurBatima.core.view.View;
import io.OurBatima.core.view.layout.LoadCircle;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * @author Gleidson Neves da Silveira | gleidisonmt@gmail.com
 * Create on  22/04/2023
 */
public class Main extends Launcher {

    @Override
    public void build(Context context) {

        Layout layout = new Layout(context);
        context.setLayout(layout);

        Loader loadCircle = new LoadCircle("Starting..", "");
        Task<View> loadViews = new LoadViews(context, loadCircle); // Load View task

        Thread tLoadViews = new Thread(loadViews);
        tLoadViews.setDaemon(true);
        tLoadViews.start();

        layout.setContent((Node) loadCircle);

        loadViews.setOnSucceeded(event -> {
            layout.setContent(null);

            View loginView = context.routes().getView("login");
            layout.setContent(loginView.getRoot());


        });

        icons.add(new Image(context.getResource("style/img/logo_64.png").toExternalForm(), 128, 128, true, true));

//        layout.setContent((Node) loadCircle);
    }

}