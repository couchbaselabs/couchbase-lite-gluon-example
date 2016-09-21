package com.couchbaselabs;

import com.couchbaselabs.views.PrimaryView;
import com.couchbaselabs.views.SecondaryView;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;
import java.net.*;


public class gluon extends MobileApplication {

    public static final String PRIMARY_VIEW = HOME_VIEW;
    public static final String SECONDARY_VIEW = "Secondary View";
    public CouchbaseSingleton couchbase;
    
    @Override
    public void init() {
        addViewFactory(PRIMARY_VIEW, () -> new PrimaryView(PRIMARY_VIEW).getView());
        addViewFactory(SECONDARY_VIEW, () -> new SecondaryView(SECONDARY_VIEW).getView());
    }

    @Override
    public void postInit(Scene scene) {
        Swatch.BLUE.assignTo(scene);

        scene.getStylesheets().add(gluon.class.getResource("style.css").toExternalForm());
        try {
            this.couchbase = CouchbaseSingleton.getInstance();
            this.couchbase.startReplication(new URL("http://localhost:4984/fx-example/"), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
