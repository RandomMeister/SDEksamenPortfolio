module com.example.sdeksamenportfolio {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.sdeksamenportfolio to javafx.fxml;
    //exports com.example.sdeksamenportfolio;
    exports com.company;
    opens com.company to javafx.fxml;
}