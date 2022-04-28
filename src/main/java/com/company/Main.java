package com.company;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

    private final Instruction instruct = new Instruction();

    public final Controller con = new Controller(instruct, this);

    private final TextField textField = new TextField();

    private final TextArea area = new TextArea();

    ComboBox<String> lecturer = new ComboBox<>();

    ComboBox<String> courses = new ComboBox<>();

    ComboBox<String> rooms = new ComboBox<>();

    ComboBox<String> timeslot = new ComboBox<>();

    Button button = new Button("Add lecturer");

    Button button2 = new Button("Find room");



    public void start(Stage stage) {

        con.initArea();

        textField.setOnAction(e -> con.enterText(textField.getText()));

        VBox root = new VBox(courses, lecturer, rooms, timeslot, textField, button, button2, area);

        lecturer.getItems().addAll(model.getLecturer());

        courses.getItems().addAll(model.getCourses());

        rooms.getItems().addAll(model.getRoom());

        timeslot.getItems().addAll(model.getTimeslot());

        button.setOnAction(e -> con.addLecturer(textField.getText()));

        button2.setOnAction(e -> con.findRoom(courses.getValue()));

        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("JavaFX");

        stage.setScene(scene);

        stage.show();
    }
}



class Instruction {

    //OP TIL AT BLIVE FJERNET HVIS DEN IKKE ER NØDVENDIG!
    //Database db = new Database();

    Instruction() { }
}


class Database {

    Database db = new Database();

    Connection connect = null;

    Database() {
        if(connect == null) open();
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
        } catch (SQLException e ) {
            System.out.println("cannot close");
        }
        connect = null;
    }

}