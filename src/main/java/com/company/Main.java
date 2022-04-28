package com.company;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;

//TEST

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;



public class Main extends Application {

    private final Model model = new Model();

    public final Controller con = new Controller(model,this);

    private final TextField field = new TextField();

    private final TextArea area = new TextArea();

    ComboBox<String> lecturer = new ComboBox<>();

    ComboBox<String> courses = new ComboBox<>();

    ComboBox<String> rooms = new ComboBox<>();

    ComboBox<String> timeslot = new ComboBox<>();

    Button button = new Button("Add lecturer");

    Button button2 = new Button("Find room");

    //Udarbejdes pt
    //Button button3 = new Button("VI SKAL HAVE ET COURSE");

    //Udarbejdes pt
    //Button button4 = new Button("VI SKAL HAVE en tid?");

    void setArea(String s) {
        area.setText(s);
    }

    void clearField() {
        field.setText("");
    }

    @Override



    public void start(Stage stage) {

        con.initArea();

        field.setOnAction(e->con.enterText(field.getText()));

        VBox root = new VBox(courses,lecturer,rooms,timeslot,field,button,button2,area);

        lecturer.getItems().addAll(model.getLecturer());

        courses.getItems().addAll(model.getCourses());

        rooms.getItems().addAll(model.getRoom());

        timeslot.getItems().addAll(model.getTimeslot());

        button.setOnAction(e -> con.addLecturer(field.getText()));

        button2.setOnAction(e -> con.findRoom(courses.getValue()));

        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("JavaFX");

        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



class Controller {

    Model model;

    Main view;

    Controller(Model model, Main view) {
        this.model = model; this.view = view;
    }

    void initArea() {
        String toarea = "";
        for(String t:model.get()) toarea += t + "\n";
        view.setArea(toarea);
    }

    void enterText(String s) {
        model.add(s);
        view.clearField();
        String toarea = "";
        for(String t:model.get()) toarea += t + "\n";
        view.setArea(toarea);
    }

    void addLecturer(String s) {
        if(model.hasLecturer(s)) {
            view.setArea("Cannot insert lecturer (repeat) " + s);
        } else {
            model.addLecturer(s);
            view.lecturer.getItems().add(s);
        }
    }

    void findRoom(String c) {
        String room = model.findRoom(c);
        if(room.equals("")) view.setArea ("No Room");
        else view.setArea("Room: " + room);
    }
}



class Model {
    MyDB db = new MyDB();
    Model() {
    }

    void addLecturer(String s) {
        db.cmd("insert into Lecturer (name) values ('" + s + "');");
    }

    ArrayList<String> getLecturer() {
        return db.query("select name from Lecturer;","name");
    }

    boolean hasLecturer(String s) {
        ArrayList <String> lst = db.query("select name from Lecturer where name = '" + s + "';","name");
        System.out.println(lst);
        return lst.size() > 0;
    }

    /*void addRoom(String s, String stud) {
        db.cmd("insert into Rooms (name,stud) values ('" + s + "'," + stud + ");");
    }*/

    ArrayList<String> getRoom() {
        return db.query("select name from Rooms;","name");
    }

    /*void addCourses(String s,String stud) {
        db.cmd("insert into Courses (name,stud) values ('" + s + "'," + stud + ");");
    }*/

    ArrayList<String> getCourses() {
        return db.query("select name from Courses;","name");
    }

    String findRoom(String c) {
        ArrayList <String> lst = db.query
                ("select Rooms.name from Rooms inner join Courses" + " where Courses.name = '" + c + "' and Rooms.stud > Courses.stud;","name");
        System.out.println(lst);
        if(lst.size() == 0) return "";
        else return lst.get(0);
    }

    /*void addTimeslot(String s) { // remember to sanitize your data!
        db.cmd("insert into Timeslot (name) values ('" + s + "');");
    }*/

    ArrayList<String> getTimeslot() {
        return db.query("select name from Timeslot;","name");
    }

    void add(String s) { // remember to sanitize your data!
        db.cmd("insert into lst1 (fld2) values ('" + s + "');");
    }

    ArrayList<String> get() {
        return db.query("select fld2 from lst1 order by fld1;","fld2");
    }
}



class MyDB {
    Connection conn = null;
    MyDB() {
        if(conn == null) open();
    }

    public void open() {
        try {
            String url = "jdbc:sqlite:listdb.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("cannot open");
            if (conn != null) close();
        }
    }

    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e ) {
            System.out.println("cannot close");
        }
        conn = null;
    }

    public void cmd(String sql) {

        if(conn == null)open();

        if(conn == null) {
            System.out.println("No connection");return;
        }

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
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

    public ArrayList<String> query(String query,String fld)
    {
        ArrayList<String> res = new ArrayList<>();

        if(conn == null)open();

        if(conn == null) {
            System.out.println("No connection");return res;
        }

        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String name = rs.getString(fld);
                res.add(name);
            }
        } catch (SQLException e ) {
            System.out.println("Error in statement " + query + " " + fld);
        }
        try {
            if (stmt != null) { stmt.close(); }
        } catch (SQLException e ) {
            System.out.println("Error in statement " + query + " " + fld);
        }
        return res;
    }
}
