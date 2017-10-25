package tetris;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class. Is used to create game window and create element of GameUI class
 * 
 * @author Alex Piskun
 *
 */
public class Main extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Tetris");
		try {
			GameUI menu = new GameUI(primaryStage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
