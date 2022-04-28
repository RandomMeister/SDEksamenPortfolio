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

public class Main extends Application{

    private final Instruction instruct = new Instruction();

    public final Controller cont = new Controller(instruct, this);

    private final TextField textField = new TextField();

    private final TextArea area = new TextArea();

    ComboBox<String> lecturer = new ComboBox<>();

    ComboBox<String> courses = new ComboBox<>();

    ComboBox<String> rooms = new ComboBox<>();

    ComboBox<String> timeslot = new ComboBox<>();

    Button button = new Button("Add lecturer");

    Button button2 = new Button("Find room");

    void setArea(String s) {
        area.setText(s);
    }

    void clearField() {
        textField.setText("");
    }



    public void start(Stage stage) {

        cont.initArea();

        textField.setOnAction(e -> cont.enterText(textField.getText()));

        VBox root = new VBox(courses, lecturer, rooms, timeslot, textField, button, button2, area);

        lecturer.getItems().addAll(instruct.getLecturer());

        courses.getItems().addAll(instruct.getCourses());

        rooms.getItems().addAll(instruct.getRoom());

        timeslot.getItems().addAll(instruct.getTimeslot());

        button.setOnAction(e -> cont.addLecturer(textField.getText()));

        button2.setOnAction(e -> cont.findRoom(courses.getValue()));

        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("JavaFX");

        stage.setScene(scene);

        stage.show();
    }
}



class Controller {

    Instruction instruct;

    Main view;

    Controller(Instruction instruct, Main view) {
        this.instruct = instruct; this.view = view;
    }

    void initArea() {
        String toArea = ""; //Y U EMPTY
        for(String t:instruct.get()) toArea += t + "\n";
        view.setArea(toArea);
    }

    void enterText(String s) {
        instruct.add(s);
        view.clearField();
        String toArea = "";
        for(String t:instruct.get()) toArea += t + "\n";
        view.setArea(toArea);
    }

    void addLecturer(String s) {
        if(instruct.hasLecturer(s)) {
            view.setArea("That Lecturer is already on the list " + s);
        } else {
            instruct.addLecturer(s);
            view.lecturer.getItems().add(s);
        }
    }

    void findRoom(String c) {
        String room = instruct.findRoom(c);
        if(room.equals("")) view.setArea ("No Room");
        else view.setArea("Room: " + room);
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