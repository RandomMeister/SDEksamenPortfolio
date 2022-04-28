package com.company;

import javafx.application.Application; //Tilføjer lecturer,corses, timesloth osv.
import javafx.scene.Scene; //Skaber selve vinduet
import javafx.scene.control.*; //til nodes
import javafx.scene.layout.VBox; //Holer styr på vores "childrens"
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

    private final TextField textField = new TextField();

    private final TextArea area = new TextArea();

    //Tilføjer vores pull down lister
    ComboBox<String> lecturer = new ComboBox<>();

    ComboBox<String> courses = new ComboBox<>();

    ComboBox<String> rooms = new ComboBox<>();

    ComboBox<String> timeslot = new ComboBox<>();

    //laver vores knapper, som senere bliver tillagt en bestemt combobox
    Button button = new Button("Add lecturer");

    Button button2 = new Button("Find room");

    void setArea(String s) {
        area.setText(s);
    }

    void clearField() {
        textField.setText("");
    }

    @Override



    public void start(Stage stage) {

        cont.initArea();

        textField.setOnAction(e -> cont.enterText(textField.getText()));

        //VBox root er her vi samler alle "children"
        VBox root = new VBox(courses, lecturer, rooms, timeslot, textField, button, button2, area);

        lecturer.getItems().addAll(instruct.getLecturer());

        courses.getItems().addAll(instruct.getCourses());

        rooms.getItems().addAll(instruct.getRoom());

        timeslot.getItems().addAll(instruct.getTimeslot());

        button.setOnAction(e -> cont.addLecturer(textField.getText()));

        button2.setOnAction(e -> cont.findRoom(courses.getValue()));

        //Vi laver vores vindue
        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("Find a course");

        stage.setScene(scene);

        stage.show();
    }

    //!!!
    //HVAD FUCK ER DEN HER TIL FOR!?
    //!!!
    public static void main(String[] args) {
        launch(args);
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

    void addLecturer(String s) { //Sørger for at hvis en lecturer allerede er tilføjet, kan han/hun ikke tilføjes igen
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

    //!!!
    //HERFRA AF ER DET UKENDT KODE!!!
    //!!
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

    void addRoom(String s, String stud) {
        db.cmd("insert into Rooms (name,stud) values ('" + s + "'," + stud + ");");
    }

    ArrayList<String> getRoom() {
        return db.query("select name from Rooms;","name");
    }

    void addCourses(String s,String stud) {
        db.cmd("insert into Courses (name,stud) values ('" + s + "'," + stud + ");");
    }

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

    void addTimeslot(String s) { // remember to sanitize your data!
        db.cmd("insert into Timeslot (name) values ('" + s + "');");
    }

    ArrayList<String> getTimeslot() {
        return db.query("select name from Timeslot;","name");
    }

    void add(String s) { // remember to sanitize your data!
        db.cmd("insert into lst1 (fld2) values ('" + s + "');");
    }

    ArrayList<String> get() {
        return db.query("select field(2) from list(1) order by field(1);","field(2)");
    }
}

//!!!
//OPAF AF ER DET UKENDT KODE!!!
//!!



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

}