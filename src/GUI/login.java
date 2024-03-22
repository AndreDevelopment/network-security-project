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
import javafx.stage.Screen;
import javafx.stage.Stage;

public class login extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("LOGIN");

        // Creating a VBox layout for login components
        VBox loginBox = new VBox();
        loginBox.setSpacing(20); // Increased spacing between components
        loginBox.setAlignment(Pos.CENTER);

        // Title
        Label titleLabel = new Label("LOGIN");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;"); // Making the title bold
        titleLabel.setAlignment(Pos.TOP_CENTER); // Aligning the title towards the top
        titleLabel.setPadding(new Insets(20, 0, 0, 0)); // Adding padding to the top

        // Username field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");
        usernameField.setMaxWidth(300); // Limiting width to fit inside the rectangle

        // Password field
        TextField passwordField = new TextField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(300); // Limiting width to fit inside the rectangle

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #9acbff; -fx-min-width: 100px; -fx-min-height: 40px; -fx-font-weight: bold;"); // Setting style to adjust size, color, and text weight

        // Register button
        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #9acbff; -fx-min-width: 100px; -fx-min-height: 40px; -fx-font-weight: bold;"); // Setting style to adjust size, color, and text weight

        // Adding nodes to the login VBox
        loginBox.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton, registerButton);

        // Creating a blue background
        Rectangle blueBackground = new Rectangle(Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight(), Color.web("#c6e2ff"));

        // Creating a white rectangle background with border radius and border color
        Rectangle whiteRectangle = new Rectangle(400, 400);
        whiteRectangle.setFill(Color.WHITE);
        whiteRectangle.setArcWidth(20); // Adding border radius
        whiteRectangle.setArcHeight(20); // Adding border radius
        whiteRectangle.setStroke(Color.web("#dddddd")); // Adding border color

        // Creating a StackPane to layer the background and content
        StackPane root = new StackPane();
        root.getChildren().addAll(blueBackground, whiteRectangle, loginBox);

        // Centering the white rectangle
        StackPane.setAlignment(whiteRectangle, Pos.CENTER);

        Scene scene = new Scene(root, Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight()); // Creating a scene with screen size
        primaryStage.setScene(scene); // Setting the scene to the stage
        primaryStage.show(); // Showing the stage
    }

    public static void main(String[] args) {
        System.setProperty("java.library.path", "/Users/arshroopsandhu/Desktop/javafx-sdk-22/lib");
        launch(args);
    }
}