package com.couchbaselabs;

import com.couchbase.lite.*;
import com.couchbase.lite.replicator.Replication;
import javafxports.android.FXActivity;
//import com.couchbase.lite.android.AndroidContext;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CouchbaseSingleton {

    private Manager manager;
    private Database database;
    private Replication pushReplication;
    private Replication pullReplication;

    private static CouchbaseSingleton instance = null;

    private CouchbaseSingleton() {
        try {
            //this.manager = new Manager(new JavaContext("data"), Manager.DEFAULT_OPTIONS);
            //FXActivity test = FXActivity.getInstance();
            this.manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
            this.database = this.manager.getDatabase("fx-project");
            View todoView = database.getView("todos");
            todoView.setMap(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    emitter.emit(document.get("_id"), document);
                }
            }, "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CouchbaseSingleton getInstance() {
        System.out.println("GETTING INSTANCE");
        if(instance == null) {
            System.out.println("CREATING NEW COUCHBASE INSTANCE");
            instance = new CouchbaseSingleton();
        }
        return instance;
    }

    public Database getDatabase() {
        return this.database;
    }

    public void startReplication(URL gateway, boolean continuous) {
        this.pushReplication = this.database.createPushReplication(gateway);
        this.pullReplication = this.database.createPullReplication(gateway);
        this.pushReplication.setContinuous(continuous);
        this.pullReplication.setContinuous(continuous);
        this.pushReplication.start();
        this.pullReplication.start();
    }

    public void stopReplication() {
        this.pushReplication.stop();
        this.pullReplication.stop();
    }

    public Todo save(Todo todo) {
        Map<String, Object> properties = new HashMap<String, Object>();
        Document document = this.database.createDocument();
        properties.put("type", "todo");
        properties.put("title", todo.getTitle());
        properties.put("description", todo.getDescription());
        try {
            todo.setDocumentId(document.putProperties(properties).getDocument().getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return todo;
    }

    public ArrayList<Todo> query() {
        ArrayList<Todo> results = new ArrayList<Todo>();
        try {
            View todoView = this.database.getView("todos");
            Query query = todoView.createQuery();
            QueryEnumerator result = query.run();
            Document document = null;
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                document = row.getDocument();
                results.add(new Todo(document.getId(), (String) document.getProperty("title"), (String) document.getProperty("description")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public void test() {
        System.out.println("This is a test");
    }

}