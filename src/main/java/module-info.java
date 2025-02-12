
module io.OurBatima {

    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.web;
    requires transitive javafx.media;
    exports io.OurBatima.controllers;


    requires java.logging;
    requires javafx.swing;
    requires com.google.gson;
    requires google.api.client;
    requires org.yaml.snakeyaml;
    requires org.jetbrains.annotations;
    requires animatefx;
//    requires io.OurBatima.gncarousel;

    requires GNAvatarView;

    requires org.kordamp.ikonli.material;
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;

    requires fr.brouillard.oss.cssfx;


//    requires io.OurBatima.gncontrols;
    requires scenicView;
    requires java.sql;
    requires jbcrypt;
    requires com.google.api.client.auth;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.client.extensions.jetty.auth;
    requires org.eclipse.jetty.server;
    requires java.mail;

    opens io.OurBatima to javafx.fxml;

    opens io.OurBatima.controllers to javafx.fxml;
    opens  io.OurBatima.core.controls.icon to javafx.fxml;
    exports io.OurBatima;
    opens io.OurBatima.views.controls to javafx.base;
    exports io.OurBatima.core.view;
    opens io.OurBatima.core.controls to javafx.fxml;
    exports io.OurBatima.core.model;
    opens io.OurBatima.core.model to javafx.fxml;
    opens io.OurBatima.core.controls.skin to javafx.fxml;
    opens io.OurBatima.core.picture_selector to javafx.fxml;
    exports io.OurBatima.core.model.Utilisateur;
    opens io.OurBatima.core.model.Utilisateur to javafx.fxml;

}