package io.ourbatima.core.impl;

import io.ourbatima.controllers.Utilisateur.UserListController;
import io.ourbatima.core.Context;
import io.ourbatima.core.exceptions.NavigationException;
import io.ourbatima.core.interfaces.Routes;
import io.ourbatima.core.view.View;
import io.ourbatima.core.views.ErrorPage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Map;

/**
 * @author Gleidson Neves da Silveira | gleidisonmt@gmail.com
 * Version 0.0.1
 * Create on  02/04/2023
 */
public class IRoutes implements Routes {

    private final IViewManager manager = new IViewManager();
    private String currentRouteName; // <-- Nouvelle propriété

    private final IRoot root;
    private final Context context;

    private StringProperty title = new SimpleStringProperty();

    public IRoutes(IRoot root, Context context) {
        this.root = root;
        this.context = context;
    }

    private Layout layout;

    public void reset() {
        root.getBody().setContent(root.getBody().getLayout());
    }



    @Override
    public Routes setView(String _view) {
//        View view = doActions(manager.get(_view));
        if (_view != null) {
            View view = manager.get(_view);
            if (valid(view)) {
//                root.getBody().setContent(View.getRoot());
                root.getBody().setContent(view);
            }
        }
//        doActions(view);
//        root.getLayout().setBody(view.getRoot());
        return this;
    }

    @Override
    public void putAndGo(View view) {
        if(valid(view)) {
            manager.add(view);
            doOnInit(view);
            root.getBody().getLayout().setContent(view.getRoot());
            doActions(view);
            title.set(changeTitle(view.getName()));
        }
        root.getBody().getLayout().setRight(null);
    }

    public Routes put(View View) {
        if(valid(View)) {
            doOnInit(View);
            manager.add(View);
        }
//        this.addView(View);
        return this;
    }

    @Override
    public View getView(String view) {
        return manager.get(view);
    }

    @Override
    public Routes nav(String key) {
        this.currentRouteName = key; // <-- Stocker le nom de la route

        View view = manager.get(key);

        if (view == null) {
//            throw new NavigationException(context, "0", "View not found.");
            ErrorPage errorPage = new ErrorPage(key);
            root.getBody().getLayout().setContent(errorPage);
            title.set("Error");
        } else {
            doActions(view);
            root.getBody().getLayout().setContent(view.getRoot());

            title.set(changeTitle(key));
            // Injecter le contexte via le setter
            if (view.getController() != null) {
                view.getController().onInit(context); // <-- Appel direct
            }
        }


        root.getBody().getLayout().setRight(null);

        return this;
    }

    private String changeTitle(String title) {

        String[] arr = title.split("[^a-z]");
        StringBuilder titleBuilder = new StringBuilder();

        for (String e : arr) {
            titleBuilder.append(e.substring(0, 1).toUpperCase())
                    .append(e.substring(1)).append(" ");
        }
        title = titleBuilder.toString();

        return title;
    }

    @Override
    public StringProperty title() {
        return title;
    }

    private boolean valid(View View) {
        return View != null;
    }

    private void doActions(View View) {
        if(View.getController() != null) {
            View.getController().onEnter();
        }
    }

    private void doOnInit(View View) {
        if(valid(View)) {
            if(View.getController() != null) {
                View.getController().onInit(context);
            }
        }
    }
    // Nouvelle méthode pour récupérer le nom de la route
    public String getCurrentRouteName() {
        return currentRouteName;
    }
}
