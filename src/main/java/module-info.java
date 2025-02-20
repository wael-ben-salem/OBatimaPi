
module io.ourbatima {

    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.web;
    requires transitive javafx.media;
    exports io.ourbatima.controllers;


    requires java.logging;
    requires javafx.swing;
    requires com.google.gson;
    requires google.api.client;
    requires org.yaml.snakeyaml;
    requires org.jetbrains.annotations;
    requires animatefx;
//    requires io.ourbatima.gncarousel;

    requires GNAvatarView;

    requires org.kordamp.ikonli.material;
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;

    requires fr.brouillard.oss.cssfx;


//    requires io.ourbatima.gncontrols;
    requires scenicView;
    requires java.sql;
    requires jbcrypt;
    requires com.google.api.client.auth;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.client.extensions.jetty.auth;
    requires org.eclipse.jetty.server;
    requires java.mail;
    requires jdk.jsobject;
    requires json.smart;


    opens io.ourbatima to javafx.fxml;

    opens io.ourbatima.controllers to javafx.fxml;
    opens  io.ourbatima.core.controls.icon to javafx.fxml;
    exports io.ourbatima;
    opens io.ourbatima.views.controls to javafx.base;
    exports io.ourbatima.core.view;
    opens io.ourbatima.core.controls to javafx.fxml;
    opens io.ourbatima.controllers.Utilisateur to javafx.fxml;
    opens io.ourbatima.controllers.Equipe to javafx.fxml;

    exports io.ourbatima.core.model;
    opens io.ourbatima.core.model to javafx.fxml;
    opens io.ourbatima.core.controls.skin to javafx.fxml;
    opens io.ourbatima.core.picture_selector to javafx.fxml;
    exports io.ourbatima.core.model.Utilisateur;
    opens io.ourbatima.core.model.Utilisateur to javafx.fxml;

}