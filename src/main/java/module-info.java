module com.example.cad {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cad to javafx.fxml;
    exports com.example.cad;
}
