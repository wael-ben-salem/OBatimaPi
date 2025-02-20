package io.ourbatima.core.services;

import io.ourbatima.core.Context;
import io.ourbatima.core.interfaces.Loader;
import io.ourbatima.core.view.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author Gleidson Neves da Silveira | gleidisonmt@gmail.com
 * Version 0.0.1
 * Create on  02/04/2023
 */
public class LoadViews extends Task<View> {

    private ViewMap data;
    private final StringBuilder builder = new StringBuilder();
    private final Context context;
    private final Loader loader;

    public LoadViews(Context context, Loader loader) {
        this.context = context;
        this.loader = loader;

        InputStream inputStream = getClass().getResourceAsStream("/ourbatima/views.yml");
        Yaml yaml = new Yaml(new Constructor(ViewMap.class));
        data = yaml.load(inputStream);
    }

    @Override
    protected View call() throws Exception {

        for (ViewComposer viewComposer : data.getViews()) {
            Thread.sleep(100);


            Platform.runLater(() -> {
                loader.updateLegend("Loading view... " + viewComposer.getName());
                loadView(viewComposer);
            });

            Thread.sleep(1000);
        }
        return null;
    }

    private void loadView(ViewComposer view) {
    FXMLLoader loader = new FXMLLoader();
    StringBuilder pathBuilder = new StringBuilder("/ourbatima/views");
    
    try {
        // Construction du chemin
        if (view.getFolder() != null) {
            pathBuilder.append("/").append(view.getFolder());
        }
        if (view.getFxml() != null) {
            pathBuilder.append("/").append(view.getFxml());
        }

        URL location = getClass().getResource(pathBuilder.toString());
        
        if (location == null) {
            throw new IOException("FXML introuvable : " + pathBuilder);
        }

        // Chargement sécurisé
        loader.setLocation(location);
        loader.setCharset(StandardCharsets.UTF_8);
        Parent root = loader.load();

        // Validation du contrôleur
        if (loader.getController() == null) {
            throw new IllegalStateException("Aucun contrôleur défini pour " + view.getName());
        }

        context.routes().put(new FXMLView(view, loader));

    } catch (IOException e) {
        System.err.println("Erreur de chargement FXML [" + view.getName() + "]");
        e.printStackTrace();
    } catch (IllegalStateException e) {
        System.err.println("Problème de contrôleur : " + e.getMessage());
    }
}
}