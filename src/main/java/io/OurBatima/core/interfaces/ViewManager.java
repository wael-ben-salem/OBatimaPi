package io.OurBatima.core.interfaces;

import io.OurBatima.core.view.View;

/**
 * @author Gleidson Neves da Silveira | gleidisonmt@gmail.com
 * Version 0.0.1
 * Create on  02/04/2023
 */
public interface ViewManager {

    void add(View View);

    View get(String name);

}
