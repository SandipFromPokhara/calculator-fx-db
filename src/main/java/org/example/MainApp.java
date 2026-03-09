package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/calculator.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Sum & Product Calculator");
        stage.setScene(scene);
        stage.setMinWidth(320);
        stage.setMinHeight(250);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}