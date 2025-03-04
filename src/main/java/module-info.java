module io.ourbatima {

    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.web;
    requires transitive javafx.media;
    exports io.ourbatima.controllers;

    requires javafx.swing;
    requires com.google.gson;
    requires google.api.client;
    requires org.yaml.snakeyaml;
    requires org.jetbrains.annotations;
    requires animatefx;
    exports io.ourbatima.core.view.layout to javafx.fxml; // Ajout crucial

//    requires io.ourbatima.gncarousel;

    requires GNAvatarView;

    requires org.kordamp.ikonli.material;
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;

    requires fr.brouillard.oss.cssfx;
    opens io.ourbatima.controllers.projet to javafx.fxml;

//    requires io.ourbatima.gncontrols;
    requires scenicView;
    requires jbcrypt;
    requires com.google.api.client.auth;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.client.extensions.jetty.auth;
    requires org.eclipse.jetty.server;
    requires java.mail;
    requires jdk.jsobject;
    requires json.smart;
    requires opencv;
    requires org.apache.commons.codec;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires java.sql;


    opens io.ourbatima to javafx.fxml;
    opens io.ourbatima.controllers to javafx.fxml;
    opens io.ourbatima.core.controls.icon to javafx.fxml;
    exports io.ourbatima;
    opens io.ourbatima.views.controls to javafx.base;
    exports io.ourbatima.core.view;
    opens io.ourbatima.core.controls to javafx.fxml;
    opens io.ourbatima.controllers.Utilisateur to javafx.fxml;
    opens io.ourbatima.controllers.Equipe to javafx.fxml;
    opens io.ourbatima.controllers.terrain to javafx.fxml;

    exports io.ourbatima.core.model;
    opens io.ourbatima.core.model to javafx.fxml;
    opens io.ourbatima.core.controls.skin to javafx.fxml;
    opens io.ourbatima.core.picture_selector to javafx.fxml;
    exports io.ourbatima.core.model.Utilisateur;
    opens io.ourbatima.core.model.Utilisateur to javafx.fxml;
    exports io.ourbatima.controllers.EtapeProjet;
    exports io.ourbatima.controllers.projet; // Export your other packages
    opens io.ourbatima.controllers.EtapeProjet to javafx.fxml;
    exports io.ourbatima.controllers.terrain; // Add this line
    exports io.ourbatima.controllers.FinanceControllers; // Export your controller package
    opens io.ourbatima.controllers.FinanceControllers to javafx.fxml;
    opens io.ourbatima.core.model.financeModel to javafx.base, javafx.fxml;
    exports io.agora.media;
    opens io.agora.media to javafx.fxml;


}