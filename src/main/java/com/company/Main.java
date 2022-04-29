package com.company;

import javafx.application.Application; //Tilføjer lecturer,courses, timeslot osv.
import javafx.scene.Scene; //Skaber selve vinduet
import javafx.scene.control.*; //til nodes
import javafx.scene.layout.VBox; //Holder styr på vores "childrens"
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;



public class Main extends Application {

    private final Instruction instruct = new Instruction();

    public final Controller cont = new Controller(instruct, this);

    private final TextField tf = new TextField();

    private final TextField sf = new TextField();

    private final TextArea area = new TextArea();

    ComboBox<String> lecturer = new ComboBox<>();
    ComboBox<String> courses = new ComboBox<>();
    ComboBox<String> rooms = new ComboBox<>();
    ComboBox<String> timeslot = new ComboBox<>();

    Button button = new Button("Add Lecturer");
    Button button1 = new Button("Add Course");
    Button button2 = new Button("Add Room");
    Button button3 = new Button("Find Room");

    void setArea(String s){area.setText(s);}
    void clearField(){tf.setText("");}
    @Override

    public void start(Stage stage)
    {
        /*
        // Set title for the stage
        stage.setTitle("Course Management System");

        String courses[] = {"Essential Computing", "Interactive Digital Design", "Software Development" };

        ComboBox combobox = new ComboBox(FXCollections.observableArrayList(courses));
        combobox.setPromptText("Courses Selection");

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

        //Lecturer placeholder
        Label teach = new Label("Lecturer");

        TextField tf1 = new TextField();

        //Lecturer placeholder
        Label room = new Label("Room");

        TextField tf2 = new TextField();

        //Lecturer placeholder
        Label time = new Label("Time");

        TextField tf3 = new TextField();

        // Create a tile pane
        GridPane gridpane = new GridPane();

        gridpane.addRow(0, combobox, selected);
        gridpane.addRow(1, teach, tf1);
        gridpane.addRow(2, room, tf2);
        gridpane.addRow(3, time, tf3);
        */

        cont.initArea();

        tf.setOnAction(e -> cont.enterText(tf.getText()));
        tf.setPromptText("Enter here either: Course, Lecturer or Room");

        sf.setOnAction(e -> cont.enterText(tf.getText()));
        sf.setPromptText("Enter capacity of Students in Room");

        VBox root = new VBox(courses, lecturer, rooms, timeslot, tf, sf, button, button1, button2, button3, area);

        lecturer.getItems().addAll(instruct.getLecturer());
        lecturer.setPromptText("Lecturer");

        courses.getItems().addAll(instruct.getCourses());
        courses.setPromptText("Course");

        rooms.getItems().addAll(instruct.getRoom());
        rooms.setPromptText("Room");

        timeslot.getItems().addAll(instruct.getTimeslot());
        timeslot.setPromptText("Timeslot");

        button.setOnAction(e -> cont.addLecturer(tf.getText()));
        button1.setOnAction(e -> cont.addCourse(tf.getText(),sf.getText()));
        button2.setOnAction(e -> cont.addRoom(tf.getText(),sf.getText()));
        button3.setOnAction(e -> cont.findRoom(courses.getValue()));

        Scene scene = new Scene(root, 1000, 800);

        stage.setTitle("Course Management System");

        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args)
    {
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
        String toArea = "";
        for(String t:instruct.get())toArea += t + "\n";
        view.setArea(toArea);
    }

    void enterText(String s) {
        instruct.add(s);
        view.clearField();
        String toArea = "";
        for(String t:instruct.get())toArea += t + "\n";
        view.setArea(toArea);
    }

    void addLecturer(String s) {
        if(instruct.hasLecturer(s)) {
            view.setArea("Cannot insert lecturer (repeat) " + s);
        } else if (s == "") {
            view.setArea("Cannot add lecturer without ID");
        } else {
            instruct.addLecturer(s);
            view.lecturer.getItems().add(s);
            view.setArea("Lecturer " + s + " added to list of lecturers");
        }
    }

    void addRoom(String s, String stud) {
        if(instruct.hasRoom(s)) {
            view.setArea("Cannot create room (repeat) " + s);
        } else if (s == "") {
            view.setArea("Cannot add Room without ID");
        } else if(stud == "") {
            view.setArea("Cannot add room without capacity (Input as numeral, other inputs cannot be saved)");
        } else {
            instruct.addRoom(s, stud);
            view.rooms.getItems().add(s);
            view.setArea("Room "+s+" added to list of rooms");
        }
    }

    void addCourse(String s, String stud) {
        if(instruct.hasCourse(s)) {
            view.setArea("Cannot create course (repeat) " + s);
        } else if (s == "") {
            view.setArea("Cannot add course without ID");
        } else if (stud == "") {
            view.setArea("Cannot add course without expected student number (Input as numeral, other inputs cannot be saved)");
        } else {
            instruct.addCourses(s, stud);
            view.courses.getItems().add(s);
            view.setArea("Course " + s + " added to list of courses");
        }
    }

    void findRoom(String c) {
        String room = instruct.findRoom(c);
        if(room.equals(""))view.setArea("No Room");
        else view.setArea("Room: " + room);
    }
}



class Instruction {

    Database db = new Database();

    Instruction() {

        addCourses("Essential Computing","60");

        addCourses("Interactive Digitial Systems","50");

        addCourses("Software Development","40");



        addRoom("Auditory 1","150");

        addRoom("Small Auditory 2","80");

        addRoom("Room 8","40");



        addLecturer("Torben");

        addLecturer("Bente");

        addLecturer("Henning");

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        for(String day:days){addTimeslot(day+" AM");addTimeslot(day+" PM");}



        db.cmd("drop table if exists lst1;");
        db.cmd("create table if not exists lst1 " + "(fld1 integer primary key autoincrement, fld2 text);");

        db.cmd("drop table if exists Courses;");
        db.cmd("create table if not exists Courses " + "(ID text, stud integer);");

        db.cmd("drop table if exists Rooms;");
        db.cmd("create table if not exists Rooms " + "(ID text, stud integer);");

        db.cmd("drop table if exists Timeslot;");
        db.cmd("create table if not exists Timeslot " + "(ID text);");

        db.cmd("drop table if exists Lecturer;");
        db.cmd("create table if not exists Lecturer " + "(ID text);");
    }



    void addLecturer(String s) {
        db.cmd("insert into Lecturer (ID) values ('" + s + "');");
    }

    ArrayList<String> getLecturer() {
        return db.query("select ID from Lecturer;","ID");
    }

    boolean hasLecturer(String s) {
        ArrayList<String> lst= db.query("select ID from Lecturer where ID = '" + s + "';","ID");
        System.out.println(lst);
        return lst.size() > 0;
    }

    void addRoom(String s,String stud) {
        db.cmd("insert into Rooms (ID,stud) values ('" + s + "'," + stud + ");");
    }
    ArrayList<String> getRoom() {
        return db.query("select ID from Rooms;","ID");
    }

    boolean hasRoom(String s) {
        ArrayList<String> lst= db.query("select ID from Rooms where ID = '" + s + "';","ID");
        System.out.println(lst);
        return lst.size() > 0;
    }

    void addCourses(String s, String stud) {
        db.cmd("insert into Courses (ID,stud) values ('"+s+"',"+stud+");");
    }

    ArrayList<String> getCourses() {
        return db.query("select ID from Courses;","ID");
    }


    boolean hasCourse(String s) {
        ArrayList<String> lst = db.query("select ID from Courses where ID = '" + s + "';","ID");
        System.out.println(lst);
        return lst.size() > 0;
    }

    String findRoom(String c) {
        ArrayList<String> lst = db.query
                ("select Rooms.ID from Rooms inner join Courses" + " where Courses.ID =" + " '" + c + "' and Rooms.stud > Courses.stud;","ID");
        System.out.println(lst);
        if(lst.size() == 0)return "";
        else return lst.get(0);
    }

    void addTimeslot(String s) {
        db.cmd("insert into Timeslot (ID) values ('" + s + "');");
    }

    ArrayList<String> getTimeslot() {
        return db.query("select ID from Timeslot;","ID");
    }

    void add(String s) {
        db.cmd("insert into list1 (field2) values ('" + s + "');");
    }

    ArrayList<String> get() {
        return db.query("select fld2 from lst1 order by fld1; ","fld2");
    }
}



class Database {

    Connection connect = null;

    Database() {
        if (connect == null) open();
        //check();
    }

    public void open() {
        try {
            //Kan pt ikke forbinde til database, og det er ikke klart hvorfor ikke
            String url = "jdbc:sqlite:listdb.db";
            connect = DriverManager.getConnection(url);

        } catch (SQLException e) {
            System.out.println("cannot open");
            if (connect != null) close();
        }
    }

    public void close() {
        try {
            if (connect != null) connect.close();
        } catch (SQLException e) {
            System.out.println("cannot close");
        }
        connect = null;
    }

    public void cmd(String sql) {

        if (connect == null) open();

        if (connect == null) {
            System.out.println("No connection");
            return;
        }

        Statement stmt = null;
        try {
            stmt = connect.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Error in statement " + sql);
        }

        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            System.out.println("Error in statement " + sql);
        }
    }

    public ArrayList<String> query(String query, String field) {
        ArrayList<String> res = new ArrayList<>();

        if (connect == null) open();

        if (connect == null) {
            System.out.println("No connection");
            return res;
        }

        Statement stmt = null;

        try {
            stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String ID = rs.getString(field);
                res.add(ID);
            }
        } catch (SQLException e) {
            System.out.println("Error in statement " + query + " " + field);
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            System.out.println("Error in statement " + query + " " + field);
        }
        return res;
    }
}