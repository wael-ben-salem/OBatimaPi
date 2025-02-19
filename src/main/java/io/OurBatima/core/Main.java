package io.ourbatima.core;

import io.ourbatima.core.impl.Layout;
import io.ourbatima.core.interfaces.Loader;
import io.ourbatima.core.services.LoadViews;
import io.ourbatima.core.view.View;
import io.ourbatima.core.view.layout.LoadCircle;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.image.Image;

public class Main extends Launcher {

    @Override
    public void build(Context context) {
        System.out.println("🚀 Starting application...");

        Layout layout = new Layout(context);
        context.setLayout(layout);

        Loader loadCircle = new LoadCircle("Starting..", "");
        Task<View> loadViews = new LoadViews(context, loadCircle);

        System.out.println("📂 Loading views...");
        Thread tLoadViews = new Thread(loadViews);
        tLoadViews.setDaemon(true);
        tLoadViews.start();

        layout.setContent((Node) loadCircle);

        loadViews.setOnSucceeded(event -> {
            System.out.println("✅ Views loaded successfully!");
            layout.setContent(null);

            View loginView = context.routes().getView("login");
            if (loginView != null) {
                System.out.println("🔑 Navigating to login view...");
                layout.setContent(loginView.getRoot());
            } else {
                System.out.println("❌ ERROR: Login view not found!");
            }
        });

        icons.add(new Image(context.getResource("style/img/logo_64.png").toExternalForm(), 128, 128, true, true));
    }
}
