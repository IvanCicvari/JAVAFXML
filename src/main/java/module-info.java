module com.example.java2project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.naming;
    requires java.xml;


    exports com.example.java2project.remote to java.rmi;
    opens com.example.java2project to javafx.fxml;
    exports com.example.java2project;
    exports com.example.java2project.models;
    opens com.example.java2project.models to javafx.fxml;
}
