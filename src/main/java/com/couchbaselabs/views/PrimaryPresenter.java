package com.couchbaselabs.views;

import com.couchbaselabs.CouchbaseSingleton;
import com.couchbaselabs.Todo;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbaselabs.gluon;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class PrimaryPresenter {

    private CouchbaseSingleton couchbase;

    @FXML
    private View primary;

    @FXML
    private ListView fxListView;

    @FXML
    private Label label;

    public void initialize() {
        try {
            this.couchbase = CouchbaseSingleton.getInstance();
            fxListView.getItems().addAll(this.couchbase.query());
            this.couchbase.getDatabase().addChangeListener(new Database.ChangeListener() {
                @Override
                public void changed(Database.ChangeEvent event) {
                    for(int i = 0; i < event.getChanges().size(); i++) {
                        final Document retrievedDocument = couchbase.getDatabase().getDocument(event.getChanges().get(i).getDocumentId());
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                int documentIndex = indexOfByDocumentId(retrievedDocument.getId(), fxListView.getItems());
                                if (retrievedDocument.isDeleted()) {
                                    if (documentIndex > -1) {
                                        fxListView.getItems().remove(documentIndex);
                                    }
                                } else {
                                    if (documentIndex == -1) {
                                        fxListView.getItems().add(new Todo(retrievedDocument.getId(), (String) retrievedDocument.getProperty("title"), (String) retrievedDocument.getProperty("description")));
                                    } else {
                                        fxListView.getItems().remove(documentIndex);
                                        fxListView.getItems().add(new Todo(retrievedDocument.getId(), (String) retrievedDocument.getProperty("title"), (String) retrievedDocument.getProperty("description")));
                                    }
                                }
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        fxListView.setCellFactory(new Callback<ListView<Todo>, ListCell<Todo>>() {
            @Override
            public ListCell<Todo> call(ListView<Todo> p) {
                ListCell<Todo> cell = new ListCell<Todo>() {
                    @Override
                    protected void updateItem(Todo t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getTitle());
                        }
                    }
                };
                return cell;
            }
        });
        primary.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setTitleText("Couchbase Todo - List");
                appBar.getActionItems().add(MaterialDesignIcon.ADD.button(e ->
                        MobileApplication.getInstance().switchView(gluon.SECONDARY_VIEW)
                ));
            }
        });
    }

    private int indexOfByDocumentId(String needle, ObservableList<Todo> haystack) {
        int result = -1;
        for(int i = 0; i < haystack.size(); i++) {
            if(haystack.get(i).getDocumentId().equals(needle)) {
                result = i;
                break;
            }
        }
        return result;
    }
    
}
