package io.ourbatima.core.impl;

import io.ourbatima.core.Context;
import io.ourbatima.core.impl.layout.Flow;
import io.ourbatima.core.interfaces.ActionView;
import io.ourbatima.core.interfaces.Routes;
import io.ourbatima.core.model.Utilisateur.Utilisateur;
import io.ourbatima.core.view.layout.SnackBar;
import io.ourbatima.core.view.layout.Wrapper;
import io.ourbatima.core.model.SearchItem;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author Gleidson Neves da Silveira | gleidisonmt@gmail.com
 * Version 0.0.1
 * Create on  23/04/2023
 */
public class IContext implements Context {

    private final Routes routes;
    private Utilisateur currentUser;

    private final IRoot root;
    private SnackBar snackBar;
    private final HostServices hostServices;

    private final ObservableList<SearchItem> searchItems;

    public IContext(IRoot root, HostServices hostServices) {
        this.root = root;
        this.hostServices = hostServices;
        this.routes = new IRoutes(root, this);
        searchItems = FXCollections.observableArrayList();
    }

    @Override
    public URL getResource(String loc) {
        return Objects.requireNonNull(
                getClass().getResource(
                        "/ourbatima/" + loc));
    }

    @Override
    public Layout layout() {
        return root.getBody().getLayout();
    }

    @Override
    public void setLayout(Layout layout) {
        root.getBody().setContent(layout);
    }

    @Override
    public Routes routes() {
        return routes;
    }

    private Flow flow;
    @Override
    public Flow flow() {
        if (flow == null) flow = new Flow(root);
        return flow;
    }

    @Override
    public Stage stage() {
        return stage;
    }

    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public ObservableList<SearchItem> searchItems() {
        return searchItems;
    }

    @Override
    public Logger logger() {
        return Logger.getLogger("app");
    }

    @Override
    public Wrapper wrapper() {
        return root.getWrapper();
    }

    @Override
    public ActionView controllerOf(String view) {
        return this.routes.getView(view).getController();
    }

    @Override
    public Object getProperty(String key) {
        return null;
    }

    @Override
    public void setProperty(String key, Object value) {

    }

    @Override
    public void navigateTo(String viewName) {

    }

    @Override
    public void openLink(String uri) {
        hostServices.showDocument(uri);
    }

    @Override
    public SnackBar createSnackBar() {
        if (snackBar == null) snackBar = new SnackBar(root);
        return snackBar;
    }
    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
        // Optionnel: Sauvegarder dans les préférences
    }

    public Utilisateur getCurrentUser() {
        return currentUser;
    }
}
