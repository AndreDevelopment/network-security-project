package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class RegisterPage extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("NEW USER REGISTRATION");

        // Creating a VBox layout for registration components
        VBox registrationBox = new VBox();
        registrationBox.setSpacing(20); // Increased spacing between components
        registrationBox.setAlignment(Pos.CENTER);

        // Title
        Label titleLabel = new Label("NEW USER REGISTRATION");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // Making the title bold
        titleLabel.setAlignment(Pos.TOP_CENTER); // Aligning the title towards the top
        titleLabel.setPadding(new Insets(20, 0, 0, 0)); // Adding padding to the top

        // Name field
        HBox nameBox = new HBox(); // Container to hold the label and text field horizontally
        nameBox.setAlignment(Pos.CENTER); // Centering the content horizontally
        Label nameLabel = new Label("Name  ");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");
        nameField.setMaxWidth(300); // Limiting width to fit inside the rectangle
        nameBox.getChildren().addAll(nameLabel, nameField);

        // Username field
        HBox usernameBox = new HBox(); // Container to hold the label and text field horizontally
        usernameBox.setAlignment(Pos.CENTER); // Centering the content horizontally
        Label usernameLabel = new Label("Username  ");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");
        usernameField.setMaxWidth(300); // Limiting width to fit inside the rectangle
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Password field
        HBox passwordBox = new HBox(); // Container to hold the label and text field horizontally
        passwordBox.setAlignment(Pos.CENTER); // Centering the content horizontally
        Label passwordLabel = new Label("Password  ");
        TextField passwordField = new TextField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(300); // Limiting width to fit inside the rectangle
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        // Re-Password field
        HBox reEnterPassBox = new HBox(); // Container to hold the label and text field horizontally
        reEnterPassBox.setAlignment(Pos.CENTER); // Centering the content horizontally
        Label reEnterPassBoxLabel = new Label("Re-Enter Password  ");
        TextField reEnterPassBoxField = new TextField();
        reEnterPassBoxField.setPromptText("Re-Enter Password");
        reEnterPassBoxField.setMaxWidth(300); // Limiting width to fit inside the rectangle
        reEnterPassBox.getChildren().addAll(reEnterPassBoxLabel, reEnterPassBoxField);

        // Register button
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #9acbff; -fx-min-width: 100px; -fx-min-height: 40px; -fx-font-weight: bold;"); // Setting style to adjust size, color, and text weight
        registerButton.setOnAction(e -> {
            // Create an instance of LoginPage and show its stage
            LoginPage loginPage = new LoginPage();
            Stage loginStage = new Stage();
            loginPage.start(loginStage);
            // Close the current stage (RegisterPage)
            primaryStage.close();
        });

        // Adding nodes to the registration VBox
        registrationBox.getChildren().addAll(titleLabel, nameBox, usernameBox, passwordBox, reEnterPassBox, registerButton);

        // Creating a blue background
        Rectangle blueBackground = new Rectangle(1000, 800, Color.web("#c6e2ff"));

        // Creating a white rectangle background with border radius
        Rectangle whiteRectangle = new Rectangle(400, 400);
        whiteRectangle.setFill(Color.WHITE);
        whiteRectangle.setArcWidth(20); // Adding border radius
        whiteRectangle.setArcHeight(20); // Adding border radius

        // Creating a StackPane to layer the background and content
        StackPane root = new StackPane();
        root.getChildren().addAll(blueBackground, whiteRectangle, registrationBox);

        // Centering the white rectangle
        StackPane.setAlignment(whiteRectangle, Pos.CENTER);

        Scene scene = new Scene(root, 1000, 800); // Creating a scene
        primaryStage.setScene(scene); // Setting the scene to the stage
        primaryStage.show(); // Showing the stage
    }

    public static void main(String[] args) {
        System.setProperty("java.library.path", "C:\\javafx-sdk-22\\lib");
        launch(args);
    }
}
