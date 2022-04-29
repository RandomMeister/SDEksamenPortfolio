package com.company;

import javafx.application.Application; //Tilføjer lecturer,corses, timesloth osv.
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene; //Skaber selve vinduet
import javafx.scene.control.*; //til nodes
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox; //Holder styr på vores "childrens"
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;







public class Main extends Application {

    private final Instruction instruct = new Instruction(); //kan ikke ændres pga final, kan ikke tilgås fra andre classes pga private

    public final Controller cont = new Controller(instruct, this); //



    // Launch the application
    public void start(Stage stage)
    {
        // Set title for the stage
        stage.setTitle("creating combo box ");

        String courses[] = { "Essential Computing", "Interactive Digital Design", "Software Development" };

        ComboBox combobox = new ComboBox(FXCollections.observableArrayList(courses));

        // Label to display the selected menu
        Label selected = new Label("default course selected");

        // Create action event
        // Hvorfor nederstående er halt grået ud ved jeg ikke
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e)
                    {
                        selected.setText(combobox.getValue() + " selected");
                    }
                };

        // Set on action
        combobox.setOnAction(event);

        // Create a tile pane
        TilePane tile_pane = new TilePane(combobox, selected);

        // Create a scene
        Scene scene = new Scene(tile_pane, 1000, 800);

        // Set the scene
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String args[])
    {
        // Launch the application
        launch(args);
    }
}



class Controller {

    Instruction instruct;

    Main view;

    Controller(Instruction instruct, Main view) {
        this.instruct = instruct; this.view = view;
    }
}



class Instruction {

    Database db = new Database();

    Instruction() { }

    ArrayList<String> get() {
        return db.query("select field(2) from list(1) order by field(1);","field(2)");
    }
}




class Database {

    Connection connect = null;

    Database() {
        if (connect == null) open();
    }

    public void open() {
        try {
            //String url = "jdbc:sqlite:listdb.db";
            String url = "jdbc:sqlite:tcmdb.db";
            connect = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("cannot open");
            if (connect != null) close();
        }

        //Vi tjekker lige om den forbinder til vores program, vores TCMDB
        System.out.println("Connected to Database");
        System.out.println("");

    }

    public void close() {
        try {
            if (connect != null) connect.close();
        } catch (SQLException e) {
            System.out.println("cannot close");
        }
        connect = null;
    }

    public ArrayList<String> query(String query, String field) {
        ArrayList<String> res = new ArrayList<>();

        if(connect == null)open();

        if(connect == null) {
            System.out.println("No connection");return res;
        }

        Statement stmt = null;

        try {
            stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String name = rs.getString(field);
                res.add(name);
            }
        } catch (SQLException e ) {
            System.out.println("Error in statement " + query + " " + field);
        }
        try {
            if (stmt != null) { stmt.close(); }
        } catch (SQLException e ) {
            System.out.println("Error in statement " + query + " " + field);
        }
        return res;
    }



    //!!!
    //HERFRA AF ER DET UKENDT KODE!!!
    //!!
    public void cmd(String sql) {

        if(connect == null)open();

        if(connect == null) {
            System.out.println("No connection");return;
        }

        Statement stmt = null;
        try {
            stmt = connect.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e ) {
            System.out.println("Error in statement " + sql);
        }

        try {
            if (stmt != null) { stmt.close(); }
        } catch (SQLException e ) {
            System.out.println("Error in statement " + sql);
        }
    }
//!!!
//OPAF AF ER DET UKENDT KODE!!!
//!!
}