module org.example.a2spotyapi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;
    requires java.net.http;


    opens org.example.a2spotyapi to javafx.fxml;
    exports org.example.a2spotyapi;
}