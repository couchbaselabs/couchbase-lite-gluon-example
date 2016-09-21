package com.couchbaselabs.views;

import com.couchbaselabs.CouchbaseSingleton;
import com.couchbaselabs.Todo;
import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.layout.layer.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SecondaryPresenter {

    private CouchbaseSingleton couchbase;

    @FXML
    private View secondary;

    @FXML
    private TextField fxTitle;

    @FXML
    private TextArea fxDescription;

    public void initialize() {
        this.couchbase = CouchbaseSingleton.getInstance();
        secondary.setShowTransitionFactory(BounceInRightTransition::new);
        
        secondary.getLayers().add(new FloatingActionButton(MaterialDesignIcon.SAVE.text,
            e -> save()
        ));
        
        secondary.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setTitleText("Couchbase Todo - Create");
            }
        });
    }

    private void save() {
        if(!fxTitle.getText().equals("") && !fxDescription.getText().equals("")) {
            couchbase.save(new Todo(fxTitle.getText(), fxDescription.getText()));
            fxTitle.setText("");
            fxDescription.setText("");
            MobileApplication.getInstance().switchToPreviousView();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Both a title and description are required for this example.");
            alert.showAndWait();
        }
    }

}
