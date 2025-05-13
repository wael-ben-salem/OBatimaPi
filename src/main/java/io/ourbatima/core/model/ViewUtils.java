package io.ourbatima.core.model;

import io.ourbatima.core.Context;
import io.ourbatima.core.interfaces.Initializable;
import io.ourbatima.core.view.View;

public class ViewUtils {
    public static View loadView(Context context, String viewName) {
        View view = context.routes().getView(viewName);

        // Call initialize if the controller implements Initializable
        if (view.getController() instanceof Initializable) {
            Initializable controller = (Initializable) view.getController();
            controller.initialize();
        }

        return view;
    }
}