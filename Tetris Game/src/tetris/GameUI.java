package tetris;

import java.applet.Applet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JLayer;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.*;

/**
 * 
 * This class is used to execute game processes. This class contains methods to
 * initialize fields - constructor - GameUI() and initializeTimelines()
 * 
 * @author Alex Piskun
 *
 */
public class GameUI {

	private KeyCode arg = KeyCode.TAB;
	private Stage stage, movesStage;
	private Group menu, game, scores, moves, map;
	private Scene menuScene, gameScene, scoresScene, movesScene, mapScene;
	private Image imageBack, imagePlay, imageScores, imageBlock[], figure1,
			figure2, figure3, figure4;
	private ImageView viewBack, viewPlay, viewScores, viewBlock;
	private Button scoresBtn, newGameBtn, playBtn, pauseBtn, backBtn,
			backScoresBtn, continueBtn, easyBtn, mediumBtn, hardBtn, autoBtn,
			loadBtn, mapBtn, mapBackBtn;
	private Integer score = 0, currentSave = 1, bestSave = 1;
	private Text scoreText, scorePlate, loseScore, loadingScore, movesText,
			movesBest, savesText, savesBest, mapText;
	private Text[] gamesText;
	private ImageView[][] gameBoard;
	private char[][] blocksMatrix;
	private int[] topBlock, bottomBlock, filesSorted;
	private Timeline gameTimeline, checkingLines, autoPlayer, loadingTimeline;
	private boolean gameOver = false, restarted = false, moveLeft, moveRight,
			autoPlay = false, lineSpinned = false, wasSpinned = false,
			justAppeared = false, downPressed = false, newAutoFigure = true,
			autoRestarted = false, newGame = true, loading = false,
			saveWas = false, loaded = false, wasLoaded = false,
			autoPause = false, timeToStop = true;
	private int figure = 0, figureState = 0, mostEmpty[], difficulty = 0;
	private File saveGame, saveGame1, saveGameSort, folder, lastSaved;
	URL music;
	private PrintWriter savingFile;
	private BufferedReader loadingFile, sorting;
	private String textFromFile, newSaveName;
	private Object semaphore;
	private CheckingEngine engine;

	// private StringBuilder saveBuilder;
	private File[] filesInFolder;
	private Integer[] movesArray, filesSizes, topTen;

	/**
	 * Constructor. Is used to initialise fields in the class
	 * 
	 * @param newStage
	 * @throws IOException
	 */

	public GameUI(Stage newStage) throws IOException {
		new Thread()
		{
			public void run() {
				String bip = "daft.mp3";
				Media hit = new Media(new File(bip).toURI().toString());
				AudioClip mediaPlayer = new AudioClip(hit.getSource());
				mediaPlayer.play();
			}
		}.start();

		movesStage = new Stage();
		movesStage.setHeight(300);
		movesStage.setWidth(450);
		movesStage.setResizable(false);
		stage = newStage;
		stage.setHeight(600);
		stage.setWidth(389);
		stage.setResizable(false);
		menu = new Group();
		game = new Group();
		scores = new Group();
		moves = new Group();
		map = new Group();
		stage.setTitle("Tetris");
		difficulty = 3;
		movesScene = new Scene(moves, 300, 450);
		movesScene.setFill(Color.BLACK);
		menuScene = new Scene(menu, 384, 576);
		gameScene = new Scene(game, 384, 576);
		scoresScene = new Scene(scores, 384, 576);
		mapScene = new Scene(map, 300, 450);
		mapScene.setFill(Color.BLACK);
		imageBack = new Image("file:tetrisMenu.png");
		imagePlay = new Image("file:tetrisStage.png");
		imageScores = new Image("file:tetrisScores.png");

		movesStage.setScene(movesScene);

		figure1 = new Image("file:figure1.png");
		figure2 = new Image("file:figure2.png");
		figure3 = new Image("file:figure3.png");
		figure4 = new Image("file:figure4.png");

		imageBlock = new Image[7];
		imageBlock[0] = new Image("file:block1.png");
		imageBlock[1] = new Image("file:block2.png");
		imageBlock[2] = new Image("file:block3.png");
		imageBlock[3] = new Image("file:block4.png");
		imageBlock[4] = new Image("file:block5.png");
		imageBlock[5] = new Image("file:block6.png");
		imageBlock[6] = new Image("file:block7.png");

		folder = new File("saves/");
		filesInFolder = folder.listFiles();

		for (int i = 0; i < filesInFolder.length; i++) {
			currentSave++;
		}

		if (currentSave != 1)
			currentSave--;

		saveGame1 = new File("saves/sg_" + currentSave.toString() + ".txt");
		lastSaved = new File("saves/sg_" + currentSave.toString() + ".txt");
		currentSave++;

		gameBoard = new ImageView[15][10];
		for (int i = 0; i < 15; i++) {
			gameBoard[i] = new ImageView[10];
		}
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 10; j++) {
				gameBoard[i][j] = new ImageView();
			}
		}
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 10; j++) {
				gameBoard[i][j].setLayoutX(j * 32 + 32);
				gameBoard[i][j].setLayoutY(i * 32 + 64);
			}
		}

		blocksMatrix = new char[15][10];

		for (int i = 0; i < 15; i++) {
			blocksMatrix[i] = new char[10];

		}

		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 10; j++) {
				blocksMatrix[i][j] = '0';

			}
		}

		topBlock = new int[2];
		bottomBlock = new int[2];
		filesSizes = new Integer[currentSave - 1];
		topTen = new Integer[10];
		filesSorted = new int[currentSave - 1];
		mostEmpty = new int[10];
		movesArray = new Integer[3];
		for (int i = 0; i < 3; i++) {
			movesArray[i] = 0;
		}
		for (int i = 0; i < 10; i++) {
			mostEmpty[i] = 0;
		}

		viewBack = new ImageView(imageBack);
		viewPlay = new ImageView(imagePlay);
		viewScores = new ImageView(imageScores);
		viewBlock = new ImageView(imageBlock[1]);

		viewBlock.setLayoutX(64);
		viewBlock.setLayoutY(128);

		gamesText = new Text[10];
		for (int i = 0; i < 10; i++) {

			if (i < 5) {
				gamesText[i] = new Text(i * 90, 32, "Sample");
				gamesText[i].setLayoutY(90);
			} else {
				gamesText[i] = new Text(i * 90 - 5 * 90, 32, "Sample");
				gamesText[i].setLayoutY(190);
			}
			gamesText[i].setFill(Color.CHARTREUSE);
			gamesText[i].setFont(Font.font("Impact", 30));
		}
		mapText = new Text(40, 32, "Top 10 games (length): ");
		mapText.setLayoutY(10);

		mapText.setFill(Color.CHARTREUSE);
		mapText.setFont(Font.font("Impact", 30));

		savesText = new Text(0, 32, "Best save: ");
		savesText.setLayoutY(150);

		savesText.setFill(Color.CHARTREUSE);
		savesText.setFont(Font.font("Impact", 40));

		movesText = new Text(0, 32, "Fav. move: ");
		movesText.setLayoutY(50);

		movesText.setFill(Color.CHARTREUSE);
		movesText.setFont(Font.font("Impact", 40));

		scoreText = new Text(110, 48, "Score:");

		scoreText.setFill(Color.CHARTREUSE);
		scoreText.setFont(Font.font("Impact", 40));

		loseScore = new Text(100, 320, "You Lose");

		loseScore.setFill(Color.CHARTREUSE);
		loseScore.setFont(Font.font("Impact", 60));

		loadingScore = new Text(40, 320, "Press C to coninue");

		loadingScore.setFill(Color.CHARTREUSE);
		loadingScore.setFont(Font.font("Impact", 40));

		scorePlate = new Text(225, 48, "0");

		scorePlate.setFill(Color.CHARTREUSE);
		scorePlate.setFont(Font.font("Impact", 40));

		scoresBtn = new Button("View Scores");
		newGameBtn = new Button("New Game");
		loadBtn = new Button("Load Game");
		easyBtn = new Button("Easy");
		mediumBtn = new Button("Medium");
		hardBtn = new Button("INSANE");
		autoBtn = new Button("Auto");
		playBtn = new Button("Start");
		continueBtn = new Button("Continue");

		pauseBtn = new Button("Pause");
		backBtn = new Button("Back and save");

		backScoresBtn = new Button("Back to main menu");

		mapBtn = new Button("Map");
		mapBackBtn = new Button("Back");

		newGameBtn.setLayoutX(42);
		newGameBtn.setLayoutY(416);

		loadBtn.setLayoutX(42);
		loadBtn.setLayoutY(320);

		continueBtn.setLayoutX(42);
		continueBtn.setLayoutY(320);

		easyBtn.setLayoutX(42);
		easyBtn.setLayoutY(128);

		mediumBtn.setLayoutX(42);
		mediumBtn.setLayoutY(224);

		hardBtn.setLayoutX(42);
		hardBtn.setLayoutY(320);

		autoBtn.setLayoutX(42);
		autoBtn.setLayoutY(416);

		scoresBtn.setLayoutX(42);
		scoresBtn.setLayoutY(416);

		playBtn.setLayoutX(0);
		playBtn.setLayoutY(0);

		pauseBtn.setLayoutX(0);
		pauseBtn.setLayoutY(0);

		backBtn.setLayoutX(284);
		backBtn.setLayoutY(0);

		mapBtn.setLayoutX(390);
		mapBtn.setLayoutY(230);

		mapBackBtn.setLayoutX(390);
		mapBackBtn.setLayoutY(230);

		backScoresBtn.setLayoutX(42);
		backScoresBtn.setLayoutY(448);

		newGameBtn.setMinSize(300, 64);
		loadBtn.setMinSize(300, 64);
		continueBtn.setMinSize(300, 64);
		easyBtn.setMinSize(300, 64);
		mediumBtn.setMinSize(300, 64);
		hardBtn.setMinSize(300, 64);
		autoBtn.setMinSize(300, 64);
		scoresBtn.setMinSize(300, 64);
		playBtn.setMinSize(100, 64);
		mapBtn.setMinSize(50, 40);
		mapBackBtn.setMinSize(50, 40);

		pauseBtn.setMinSize(100, 64);
		backBtn.setMinSize(100, 64);
		backScoresBtn.setMinSize(300, 64);

		semaphore = new Object();
		engine = new CheckingEngine(semaphore);
		engine.setDaemon(true);
		engine.start();

		// saveBuilder = new StringBuilder();
		if (saveGame1.exists()) {
			saveWas = true;
			loadingFile = new BufferedReader(new FileReader(
					saveGame1.getAbsoluteFile()));

			if (loadingFile.readLine() == null)
				saveWas = false;

			loadingFile = new BufferedReader(new FileReader(
					saveGame1.getAbsoluteFile()));
		}
		newGameBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				movesStage.close();
				;
				menu.getChildren().remove(newGameBtn);
				menu.getChildren().remove(scoresBtn);
				menu.getChildren().remove(loadBtn);
				if (!newGame)
					menu.getChildren().remove(continueBtn);
				continueBtn.setLayoutX(42);
				continueBtn.setLayoutY(320);
				menu.getChildren().add(easyBtn);
				menu.getChildren().add(mediumBtn);
				menu.getChildren().add(hardBtn);
				menu.getChildren().add(autoBtn);
				if (!saveGame.exists())
					try {
						saveGame.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			}
		});

		mapBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				movesStage.setScene(mapScene);
			}
		});

		mapBackBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				movesStage.setScene(movesScene);
			}
		});

		loadBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				movesStage.close();
				if (!newGame)
					menu.getChildren().remove(continueBtn);
				loading = true;
				blocksMatrix = nullify();
				restarted = true;
				try {
					textFromFile = loadingFile.readLine();
					difficulty = textFromFile.toCharArray()[0] - '0';

				} catch (IOException e) {

					e.printStackTrace();
				}
				if (newGame)
					initializeTimelines();
				menu.getChildren().remove(loadBtn);
				menu.getChildren().add(continueBtn);
				stage.setScene(gameScene);

				loadingTimeline.play();
			}
		});

		easyBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				menu.getChildren().remove(easyBtn);
				menu.getChildren().remove(mediumBtn);
				menu.getChildren().remove(hardBtn);
				menu.getChildren().remove(autoBtn);
				menu.getChildren().add(continueBtn);
				menu.getChildren().add(newGameBtn);
				// menu.getChildren().add(loadBtn);
				menu.getChildren().add(scoresBtn);
				difficulty = 90;
				try {
					savingFile = new PrintWriter(saveGame.getAbsoluteFile());
				} catch (FileNotFoundException e) {

					e.printStackTrace();
				}
				if (newGame)
					initializeTimelines();

				stage.setScene(gameScene);

				gameTimeline.play();
			}
		});

		mediumBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				menu.getChildren().remove(easyBtn);
				menu.getChildren().remove(mediumBtn);
				menu.getChildren().remove(hardBtn);
				menu.getChildren().remove(autoBtn);
				menu.getChildren().add(continueBtn);
				menu.getChildren().add(newGameBtn);
				// menu.getChildren().add(loadBtn);
				menu.getChildren().add(scoresBtn);
				difficulty = 45;
				try {
					savingFile = new PrintWriter(saveGame.getAbsoluteFile());
				} catch (FileNotFoundException e) {

					e.printStackTrace();
				}
				if (newGame)
					initializeTimelines();
				stage.setScene(gameScene);

				gameTimeline.play();
			}
		});
		hardBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				menu.getChildren().remove(easyBtn);
				menu.getChildren().remove(mediumBtn);
				menu.getChildren().remove(hardBtn);
				menu.getChildren().remove(autoBtn);
				menu.getChildren().add(continueBtn);
				menu.getChildren().add(newGameBtn);
				// menu.getChildren().add(loadBtn);
				menu.getChildren().add(scoresBtn);
				difficulty = 20;
				try {
					savingFile = new PrintWriter(saveGame.getAbsoluteFile());
				} catch (FileNotFoundException e) {

					e.printStackTrace();
				}
				if (newGame)
					initializeTimelines();
				stage.setScene(gameScene);

				gameTimeline.play();
			}
		});
		autoBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				menu.getChildren().remove(easyBtn);
				menu.getChildren().remove(mediumBtn);
				menu.getChildren().remove(hardBtn);
				menu.getChildren().remove(autoBtn);
				menu.getChildren().add(continueBtn);
				// menu.getChildren().add(newGameBtn);
				// menu.getChildren().add(loadBtn);
				menu.getChildren().add(scoresBtn);
				autoPlay = true;
				difficulty = 1;
				try {
					savingFile = new PrintWriter(saveGame.getAbsoluteFile());
				} catch (FileNotFoundException e) {

					e.printStackTrace();
				}

				if (newGame)
					initializeTimelines();
				stage.setScene(gameScene);
				gameTimeline.play();
			}
		});

		scoresBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				movesStage.close();
				stage.setScene(scoresScene);
			}
		});

		playBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				game.getChildren().remove(playBtn);
				game.getChildren().add(pauseBtn);
				gameTimeline.play();
			}
		});

		continueBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stage.setScene(gameScene);
				try {
					loadingFile.close();
				} catch (IOException e2) {

					e2.printStackTrace();
				}
				StringBuilder textFile = new StringBuilder();

				try {
					loadingFile = new BufferedReader(new FileReader(saveGame
							.getAbsoluteFile()));
				} catch (FileNotFoundException e1) {

					e1.printStackTrace();
				}
				String s;
				try {
					while ((s = loadingFile.readLine()) != null) {
						textFile.append(s);
						textFile.append('\n');
					}
				} catch (IOException e1) {

					e1.printStackTrace();
				}
				try {
					loadingFile.close();
				} catch (IOException e1) {

					e1.printStackTrace();
				}
				try {
					savingFile = new PrintWriter(saveGame.getAbsoluteFile());
				} catch (FileNotFoundException e) {

					e.printStackTrace();
				}
				savingFile.print(textFile.toString());
				gameTimeline.play();
			}
		});

		pauseBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				gameTimeline.pause();
				game.getChildren().remove(pauseBtn);
				game.getChildren().add(playBtn);
			}
		});

		backBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				gameTimeline.pause();
				stage.setScene(menuScene);

				savingFile.close();

			}
		});

		backScoresBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				stage.setScene(menuScene);

			}
		});

		game.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent arg0) {
				if (!autoPlay && !loading) {
					if (stage != null)
						arg = arg0.getCode();
					if (arg0.getCode() == KeyCode.R) {
						try {
							savingFile = new PrintWriter(saveGame
									.getAbsoluteFile());
						} catch (FileNotFoundException e) {

							e.printStackTrace();
						}
						savingFile.println(difficulty);
						score = 0;
						gameTimeline.pause();
						blocksMatrix = nullify();
						game.getChildren().remove(loadingScore);
						NewSave();
						restarted = true;
						gameTimeline.play();
					}
					if (arg0.getCode() == KeyCode.LEFT) {
						moveLeft = true;
						moveFigure();
						savingFile.print('L');

					}
					if (arg0.getCode() == KeyCode.RIGHT) {
						moveRight = true;
						moveFigure();
						savingFile.print('R');

					}
					if (arg0.getCode() == KeyCode.UP) {
						spin();
						savingFile.print('U');

					}
					if (arg0.getCode() == KeyCode.DOWN) {
						downPressed = true;
						// savingFile.print('D');

					}
					if (arg0.getCode() == KeyCode.C && loaded && difficulty != 1) {
						gameTimeline.play();
						game.getChildren().remove(loadingScore);
						loaded = false;
						// savingFile.print('D');
					}
					if (arg0.getCode() == KeyCode.C && loaded && difficulty == 1) {
						try {
							savingFile = new PrintWriter(saveGame
									.getAbsoluteFile());
						} catch (FileNotFoundException e) {

							e.printStackTrace();
						}
						savingFile.println(difficulty);
						score = 0;
						gameTimeline.pause();
						blocksMatrix = nullify();
						NewSave();
						restarted = true;
						gameTimeline.play();
						game.getChildren().remove(loadingScore);
						loaded = false;
						// savingFile.print('D');
					}
				}
			}

		});

		stage.setScene(menuScene);

		map.getChildren().add(mapText);
		map.getChildren().add(mapBackBtn);

		moves.getChildren().add(movesText);
		moves.getChildren().add(savesText);
		moves.getChildren().add(mapBtn);
		menu.getChildren().add(viewBack);
		// menu.getChildren().add(scoresBtn);
		menu.getChildren().add(newGameBtn);
		game.getChildren().add(viewPlay);
		if (saveWas)
			menu.getChildren().add(loadBtn);
		// game.getChildren().add(viewBlock);
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 10; j++) {
				game.getChildren().add(gameBoard[i][j]);
			}
		}
		game.getChildren().add(backBtn);
		game.getChildren().add(pauseBtn);
		game.getChildren().add(scorePlate);
		game.getChildren().add(scoreText);

		scores.getChildren().add(viewScores);
		scores.getChildren().add(backScoresBtn);
		stage.setScene(menuScene);
		stage.show();

		movesStage.show();
		if (!saveWas)
			currentSave--;
		if (filesInFolder.length != 0 && saveWas)
			SortAll();

		if (saveWas) {
			saveGame1 = new File("saves/sg_" + bestSave.toString() + ".txt");
			loadingFile = new BufferedReader(new FileReader(
					saveGame1.getAbsoluteFile()));
			// saveGame1 = new File()
		} else {
			saveGame1 = new File("saves/sg_" + currentSave.toString() + ".txt");
			// loadingFile = new BufferedReader(new FileReader(
			// saveGame1.getAbsoluteFile()));
		}
		saveGame = new File("saves/sg_" + currentSave.toString() + ".txt");
		if (currentSave > 10)
			for (Integer i = 1; i < 11; i++) {
				gamesText[i - 1].setText(i.toString() + ": "
						+ topTen[i - 1].toString());
			}
		if (saveWas) {
			StringBuilder pseudoSave = new StringBuilder();
			File oldSave = new File("saves/sg_" + bestSave.toString() + ".txt");
			BufferedReader pseudoCreator = new BufferedReader(new FileReader(
					oldSave.getAbsoluteFile()));
			String textFromFile = new String();
			while ((textFromFile = pseudoCreator.readLine()) != null) {
				for (int i = 0; i < textFromFile.length(); i++) {
					if (textFromFile.toCharArray()[i] == '1') {
						pseudoSave
								.append("New Game Difficulty : 1 (auto player)");
						pseudoSave.append('\n');
						break;
					}
					if (textFromFile.toCharArray()[i] == '9') {
						pseudoSave.append("New Game Difficulty : 90 (Easy)");
						pseudoSave.append('\n');
						break;
					}
					if (textFromFile.toCharArray()[i] == '2') {
						pseudoSave.append("New Game Difficulty : 20 (Hard)");
						pseudoSave.append('\n');
						break;
					}
					if (textFromFile.toCharArray()[i] == '4') {
						pseudoSave.append("New Game Difficulty : 40 (Medium)");
						pseudoSave.append('\n');
						break;
					}
					if (textFromFile.toCharArray()[i] == 'F') {
						pseudoSave.append("New Figure Appeared : №"
								+ textFromFile.toCharArray()[i + 1]);
						pseudoSave.append('\n');
						break;
					}
					if (textFromFile.toCharArray()[i] == 'L') {
						pseudoSave.append("Left move of figure");
						pseudoSave.append('\n');
					}
					if (textFromFile.toCharArray()[i] == 'R') {
						pseudoSave.append("Right move of figure");
						pseudoSave.append('\n');
					}
					if (textFromFile.toCharArray()[i] == 'U') {
						pseudoSave.append("Spinning of a figure clockwise");
						pseudoSave.append('\n');
					}
					if (textFromFile.toCharArray()[i] == 'N') {
						pseudoSave.append("Figure is down proceeded");
						pseudoSave.append('\n');
						break;
					}
				}
			}
			pseudoCreator.close();

		}
		map.getChildren().addAll(gamesText);

	}

	private char[][] set(char[][] matrix, int numberOfFigure) {

		scorePlate.setText(score.toString());

		if (numberOfFigure == 0) {
			topBlock[0] = 5;
			topBlock[1] = 0;
			bottomBlock[0] = 5;
			bottomBlock[1] = 3;
			matrix[0][5] = '1';
			matrix[1][5] = '1';
			matrix[2][5] = '1';
			matrix[3][5] = '1';
		}
		if (numberOfFigure == 1) {
			topBlock[0] = 4;
			topBlock[1] = 0;
			bottomBlock[0] = 5;
			bottomBlock[1] = 1;
			matrix[0][4] = '2';
			matrix[0][5] = '2';
			matrix[1][4] = '2';
			matrix[1][5] = '2';
		}
		if (numberOfFigure == 2) {
			topBlock[0] = 3;
			topBlock[1] = 0;
			bottomBlock[0] = 5;
			bottomBlock[1] = 2;
			matrix[0][4] = '3';
			matrix[0][5] = '3';
			matrix[1][4] = '3';
			matrix[2][4] = '3';
		}
		if (numberOfFigure == 3) {
			topBlock[0] = 3;
			topBlock[1] = 0;
			bottomBlock[0] = 5;
			bottomBlock[1] = 2;
			matrix[0][4] = '4';
			matrix[0][5] = '4';
			matrix[1][5] = '4';
			matrix[2][5] = '4';
		}
		if (numberOfFigure == 4) {
			topBlock[0] = 3;
			topBlock[1] = 0;
			bottomBlock[0] = 5;
			bottomBlock[1] = 2;
			matrix[0][4] = '5';
			matrix[1][4] = '5';
			matrix[1][5] = '5';
			matrix[2][5] = '5';
		}
		if (numberOfFigure == 5) {
			topBlock[0] = 3;
			topBlock[1] = 0;
			bottomBlock[0] = 5;
			bottomBlock[1] = 2;
			matrix[0][5] = '6';
			matrix[1][5] = '6';
			matrix[1][4] = '6';
			matrix[2][4] = '6';
		}
		if (numberOfFigure == 6) {
			topBlock[0] = 3;
			topBlock[1] = 0;
			bottomBlock[0] = 5;
			bottomBlock[1] = 2;
			matrix[0][3] = '7';
			matrix[0][4] = '7';
			matrix[0][5] = '7';
			matrix[1][4] = '7';
		}
		// if (autoRestarted)
		// autoPlayer.play();
		return matrix;
	}

	/*
	 * private boolean checkMatrix() { for (int i = 14; i > -1; i--) for (int j
	 * = 0; j < 10; j++) { if (blocksMatrix[i][j] == '1' || blocksMatrix[i][j]
	 * == '2' || blocksMatrix[i][j] == '3' || blocksMatrix[i][j] == '4' ||
	 * blocksMatrix[i][j] == '5' || blocksMatrix[i][j] == '6' ||
	 * blocksMatrix[i][j] == '7') { if (i == 14) { return true; } else { if
	 * (blocksMatrix[i + 1][j] == '~') return true; } } } return false; }
	 */
	private boolean checkNew(char[][] matrix, int numberOfFigure) {
		if (numberOfFigure == 0) {
			if (matrix[0][5] == '~' || matrix[1][5] == '~'
					|| matrix[2][5] == '~' || matrix[3][5] == '~')
				return true;
		}
		if (numberOfFigure == 1) {
			if (matrix[0][4] == '~' || matrix[0][5] == '~'
					|| matrix[1][4] == '~' || matrix[1][5] == '~')
				return true;
		}
		if (numberOfFigure == 2) {
			if (matrix[0][4] == '~' || matrix[0][5] == '~'
					|| matrix[1][4] == '~' || matrix[2][4] == '~')
				return true;
		}
		if (numberOfFigure == 3) {
			if (matrix[0][4] == '~' || matrix[0][5] == '~'
					|| matrix[1][5] == '~' || matrix[2][5] == '~')
				return true;
		}
		if (numberOfFigure == 4) {
			if (matrix[0][4] == '~' || matrix[1][4] == '~'
					|| matrix[1][5] == '~' || matrix[2][5] == '~')
				return true;
		}
		if (numberOfFigure == 5) {
			if (matrix[0][5] == '~' || matrix[1][5] == '~'
					|| matrix[1][4] == '~' || matrix[2][4] == '~')
				return true;
		}
		if (numberOfFigure == 6) {
			if (matrix[0][3] == '7' || matrix[0][4] == '7'
					|| matrix[0][5] == '7' || matrix[1][4] == '7')
				return true;
		}
		return false;
	}

	private char[][] nullify() {
		char[][] matrix = new char[15][10];

		for (int i = 0; i < 15; i++) {
			matrix[i] = new char[10];

		}

		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 10; j++) {
				matrix[i][j] = '0';

			}
		}

		return matrix;
	}

	private boolean checkMoveAllowed(char[][] matrix) {

		for (int i = 14; i > -1; i--)
			for (int j = 0; j < 10; j++) {
				if (matrix[i][j] == '1' || matrix[i][j] == '2'
						|| matrix[i][j] == '3' || matrix[i][j] == '4'
						|| matrix[i][j] == '5' || matrix[i][j] == '6'
						|| matrix[i][j] == '7') {
					if (moveLeft && (j <= 0 || matrix[i][j - 1] == '~')) {
						return false;
					}
					if (moveRight && (j >= 9 || matrix[i][j + 1] == '~')) {
						return false;
					}
				}

			}

		return true;

	}

	/**
	 * This method is used to izitialize game timelines, to execute game
	 * processes
	 */
	public void initializeTimelines() {
		gameTimeline = new Timeline(new KeyFrame(Duration.millis(10),
				new EventHandler<ActionEvent>() {

					boolean newFigure = true, cannotMove = false;
					Random random = new Random();
					int gameDiff = difficulty;

					@Override
					public void handle(ActionEvent event) {
						timeToStop = true;
						if (loaded) {
							newFigure = false;
							loaded = false;
							wasLoaded = true;
						}
						if (gameDiff == difficulty) {
							if (restarted && !wasLoaded) {
								game.getChildren().remove(loseScore);
								newFigure = true;
								cannotMove = false;
								restarted = false;
							}
							try {

								engine.setMatrix(blocksMatrix);
								synchronized (semaphore) {
									engine.startChecking();
									semaphore.notifyAll();
									if (engine.getChecking())
										semaphore.wait();
								}
								cannotMove = engine.getMoveState();
							
							} catch (Throwable error) {
								error.printStackTrace();
							}

							check();
							// System.out.print(cannotMove);
							if (!cannotMove) {
								timeToStop = true;
								if (newFigure) {
									autoPause = true;
									autoPlayer.pause();
									// System.out.println(autoPlayer.getStatus());
									if (newGame) {
										if (!wasLoaded) {
											savingFile.println(difficulty);
											newGame = false;
										} else {
											newGame = false;
										}
									}
									System.out.println(autoPlayer.getStatus());

									newAutoFigure = true;

									figure = random.nextInt(7);

									if (!checkNew(blocksMatrix, figure)) {
										blocksMatrix = set(blocksMatrix, figure);
										savingFile.print('F');
										savingFile.println(figure);
									}

									else {
										gameOver = true;
										gameTimeline.pause();
										// System.out.println(autoPlayer
										// .getStatus());
										game.getChildren().add(loseScore);
										if (autoPlay) {
											score = 0;
											blocksMatrix = nullify();
											restarted = true;
											autoRestarted = true;
											NewSave();
											autoPause = false;
											// System.out.println(autoPlayer
											// .getStatus());
											// autoPlayer.pause();

										}
									}
									newFigure = false;
									justAppeared = true;
									if (autoPlay && !autoRestarted) {
										timeToStop = false;
										autoPlayer.play();
										autoRestarted = false;
									}
									if (autoPlay && autoPause) {
										timeToStop = false;
										autoPlayer.play();
										autoPause = false;
									}
								}
								if (!justAppeared)
									check();
								if (!newFigure && !justAppeared) {
									downProceeding();
								}

							} else {
								timeToStop = true;
								if (autoPlay)
									autoPlayer.pause();

								for (int i = 0; i < 15; i++) {
									for (int j = 0; j < 10; j++) {
										if (blocksMatrix[i][j] == '1'
												|| blocksMatrix[i][j] == '2'
												|| blocksMatrix[i][j] == '3'
												|| blocksMatrix[i][j] == '4'
												|| blocksMatrix[i][j] == '5'
												|| blocksMatrix[i][j] == '6'
												|| blocksMatrix[i][j] == '7') {
											gameBoard[i][j]
													.setImage(imageBlock[figure]);
											blocksMatrix[i][j] = '~';

										}
									}
								}
								/*
								 * for (int i = 0; i < 15; i++) { for (int j =
								 * 0; j < 10; j++) {
								 * System.out.print(blocksMatrix[i][j]); }
								 * System.out.println(); } System.out.println();
								 */
								downPressed = false;
								justAppeared = false;
								newFigure = true;
								cannotMove = false;
								lineSpinned = false;
								figureState = 0;
								autoRestarted = false;
								if (!justAppeared && !wasSpinned
										&& !downPressed)
									gameDiff = 0;
								else
									gameDiff = difficulty;
								// if (autoPlay && autoPause)
								// autoPlayer.play();

							}
							if (!justAppeared && !wasSpinned && !downPressed)
								gameDiff = 0;
							else
								gameDiff = difficulty;
							justAppeared = false;
						} else
							gameDiff++;
						if (gameOver && autoPlay) {
							gameTimeline.play();
							gameOver = false;
							gameDiff = difficulty;

						}

					}
				}));
		gameTimeline.setCycleCount(Timeline.INDEFINITE);

		loadingTimeline = new Timeline(new KeyFrame(Duration.millis(75),
				new EventHandler<ActionEvent>() {

					boolean newFigure = true, cannotMove = false;
					boolean nextString = true, nextSymbol = false,
							notToRead = false;
					int symbol = 0;

					@Override
					public void handle(ActionEvent event) {
						try {

							engine.setMatrix(blocksMatrix);
							synchronized (semaphore) {
								engine.startChecking();
								semaphore.notifyAll();
								if (engine.getChecking())
									semaphore.wait();
							}
							cannotMove = engine.getMoveState();
						} catch (Throwable error) {
							error.printStackTrace();
						}
						check();

						if (!cannotMove) {
							if (nextString) {
								try {
									textFromFile = loadingFile.readLine();
								} catch (IOException e) {

									e.printStackTrace();
								}
								nextString = false;
							}
							if (textFromFile != null) {
								System.out.println(textFromFile);
								if (textFromFile.toCharArray()[0] == 'F') {
									newFigure = true;
								}
								if (newFigure) {
									figure = textFromFile.toCharArray()[1] - '0';
									blocksMatrix = set(blocksMatrix, figure);
									newFigure = false;
									justAppeared = true;
									nextString = true;
								}
								if (!nextString) {
									if (symbol > textFromFile.length() - 1) {
										notToRead = true;
										nextString = true;
										symbol = 0;
									}
									nextSymbol = true;
									if (textFromFile.toCharArray()[symbol] == 'N'
											&& nextSymbol && !notToRead) {
										downProceeding();
										symbol = 0;
										nextString = true;
										nextSymbol = false;
									}
									if (textFromFile.toCharArray()[symbol] == 'L'
											&& nextSymbol && !notToRead) {
										moveLeft = true;
										moveFigure();
										symbol++;
									}
									if (textFromFile.toCharArray()[symbol] == 'R'
											&& nextSymbol && !notToRead) {
										moveRight = true;
										moveFigure();
										symbol++;
									}
									if (textFromFile.toCharArray()[symbol] == 'U'
											&& nextSymbol && !notToRead) {
										spin();
										symbol++;
									}
									notToRead = false;
								} else
									symbol = 0;
								if (!justAppeared)
									check();

							} else {
								try {
									loadingFile.close();
								} catch (IOException e2) {

									e2.printStackTrace();
								}
								StringBuilder textFile = new StringBuilder();
								try {
									loadingFile = new BufferedReader(
											new FileReader(saveGame1
													.getAbsoluteFile()));
								} catch (FileNotFoundException e1) {

									e1.printStackTrace();
								}
								String s;
								try {
									while ((s = loadingFile.readLine()) != null) {
										textFile.append(s);
										textFile.append('\n');
									}
								} catch (IOException e1) {

									e1.printStackTrace();
								}
								try {
									loadingFile.close();
								} catch (IOException e1) {

									e1.printStackTrace();
								}
								try {
									savingFile = new PrintWriter(lastSaved
											.getAbsoluteFile());
								} catch (FileNotFoundException e) {

									e.printStackTrace();
								}
								savingFile.print(textFile.toString());
								loading = false;
								loaded = true;

								loadingTimeline.pause();
								game.getChildren().add(loadingScore);
							}

						} else {
							for (int i = 0; i < 15; i++) {
								for (int j = 0; j < 10; j++) {
									if (blocksMatrix[i][j] == '1'
											|| blocksMatrix[i][j] == '2'
											|| blocksMatrix[i][j] == '3'
											|| blocksMatrix[i][j] == '4'
											|| blocksMatrix[i][j] == '5'
											|| blocksMatrix[i][j] == '6'
											|| blocksMatrix[i][j] == '7') {
										gameBoard[i][j]
												.setImage(imageBlock[figure]);
										blocksMatrix[i][j] = '~';

									}
								}
							}
							downPressed = false;
							justAppeared = false;
							newFigure = true;
							cannotMove = false;
							lineSpinned = false;
							figureState = 0;

						}
						justAppeared = false;

					}
				}));
		loadingTimeline.setCycleCount(Timeline.INDEFINITE);

		checkingLines = new Timeline(new KeyFrame(Duration.millis(1),
				new EventHandler<ActionEvent>() {
					int tildasInLine = 0, lines = 0;

					@Override
					public void handle(ActionEvent event) {
						for (int i = 0; i < 15; i++) {
							for (int j = 0; j < 10; j++) {
								if (blocksMatrix[i][j] == '~') {
									tildasInLine++;

								}
							}
							if (tildasInLine == 10) {
								lines++;
								for (int g = 0; g < 10; g++) {
									blocksMatrix[i][g] = '0';
								}
								for (int e = i - 1; e > -1; e--) {
									for (int g = 0; g < 10; g++) {
										if (blocksMatrix[e][g] == '~') {
											blocksMatrix[e + 1][g] = blocksMatrix[e][g];
											blocksMatrix[e][g] = '0';
											gameBoard[e + 1][g]
													.setImage(gameBoard[e][g]
															.getImage());
											gameBoard[e][g].setImage(null);
										}
									}
								}
								if (lines == 1) {
									score = score + 10;
								}
								if (lines == 2) {
									score = score + 100;
								}
								if (lines == 3) {
									score = score + 300;
								}
								if (lines > 4) {
									score = score + 500;
								}
							} else
								lines = 0;
							tildasInLine = 0;
						}
						if (topBlock[0] < 0) {
							topBlock[0]++;
							bottomBlock[0]++;
						}
						if (bottomBlock[0] > 14) {
							topBlock[0]--;
							bottomBlock[0]--;
						}
					}
				}));
		checkingLines.setCycleCount(Timeline.INDEFINITE);
		checkingLines.play();

		autoPlayer = new Timeline(new KeyFrame(Duration.millis(1),
				new EventHandler<ActionEvent>() {
					int min = 0, numberOfMins = 0, minFirst = 0, maxMins = 0,
							maxMinFirst = 0;

					@Override
					public void handle(ActionEvent event) {
						if (timeToStop)
							autoPlayer.pause();
						if (autoRestarted) {
							min = 0;
							numberOfMins = 0;
							minFirst = 0;
							autoRestarted = false;
						}
						if (newAutoFigure) {
							if (timeToStop)
								autoPlayer.pause();
							downPressed = false;
							min = 0;
							numberOfMins = 0;
							minFirst = 0;
							for (int j = 0; j < 10; j++)
								for (int i = 0; i < 15; i++) {
									if (blocksMatrix[i][j] != '~') {
										mostEmpty[j] = i;
									} else {
										if (j == 0) {
											min = mostEmpty[j];
											numberOfMins = 1;
										} else {
											if (mostEmpty[j] > min) {
												minFirst = j;
												min = mostEmpty[j];
												numberOfMins = 1;
											}
										}
										break;
									}
									if (j == 0) {
										min = mostEmpty[j];
										numberOfMins = 1;
									} else {
										if (mostEmpty[j] > min) {
											minFirst = j;
											min = mostEmpty[j];
											numberOfMins = 1;
										}
									}
								}
							maxMinFirst = minFirst;
							maxMins = numberOfMins;
							for (int i = minFirst + 1; i < 10; i++) {
								if (mostEmpty[i] == min) {
									if (numberOfMins == 0) {
										minFirst = i;
									}
									numberOfMins++;
									if (i == 9) {
										if (maxMins < numberOfMins) {
											maxMins = numberOfMins;
											maxMinFirst = minFirst;
										}
									}
									// maxMins = numberOfMins;
								} else {
									if (maxMins < numberOfMins) {
										maxMins = numberOfMins;
										maxMinFirst = minFirst;
									}
									numberOfMins = 0;
								}
							}
							/*
							 * for (int i = minFirst + 1; i < 10; i++) { if
							 * (mostEmpty[i] == min) { numberOfMins++; maxMins =
							 * numberOfMins; if(numberOfMins == 1) minFirst = i;
							 * } else if(numberOfMins > maxMins){ maxMins =
							 * numberOfMins; maxMinFirst = minFirst; } }
							 */
							minFirst = maxMinFirst;
							numberOfMins = maxMins;
							newAutoFigure = false;
							for (int i = 0; i < 10; i++) {
								mostEmpty[i] = 0;
							}
						}
						/*
						 * System.out.print(min); System.out.print(' ');
						 * System.out.print(numberOfMins);
						 * System.out.print(' '); System.out.print(minFirst);
						 * System.out.print(' '); System.out.println();
						 */
						if (!downPressed && !timeToStop) {
							if (numberOfMins == 1) {
								if (figure == 0) {
									if (bottomBlock[0] > minFirst) {
										moveLeft = true;
										moveFigure();
										savingFile.print('L');
									}
									if (bottomBlock[0] < minFirst) {
										moveRight = true;
										moveFigure();
										savingFile.print('R');
									}
									if (bottomBlock[0] == minFirst) {
										downPressed = true;
									}
								}
								if (figure == 1) {
									if (bottomBlock[0] > 1) {
										moveLeft = true;
										moveFigure();
										savingFile.print('L');
									}
									if (bottomBlock[0] == 1) {
										downPressed = true;
									}
								}
								if (figure == 2) {
									if (minFirst > 1) {
										if (blocksMatrix[min - 1][minFirst - 1] == '0'
												&& blocksMatrix[min - 1][minFirst - 2] == '0') {
											if (figureState != 1
													&& bottomBlock[1] < 13) {
												spin();
												savingFile.print('U');
											}
											if (bottomBlock[0] > minFirst) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if (bottomBlock[0] < minFirst) {
												moveRight = true;
												moveFigure();
												savingFile.print('R');
											}
											if (bottomBlock[0] == minFirst) {
												downPressed = true;
											}
										} else {
											if ((bottomBlock[0] - 1) > minFirst) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if ((bottomBlock[0] - 1) < minFirst) {
												moveRight = true;
												moveFigure();
												savingFile.print('R');
											}
											if ((bottomBlock[0] - 1) == minFirst) {
												downPressed = true;
											}
										}
									} else {
										if ((bottomBlock[0] - 1) > minFirst) {
											moveLeft = true;
											moveFigure();
											savingFile.print('L');
										}
										if ((bottomBlock[0] - 1) < minFirst) {
											moveRight = true;
											moveFigure();
											savingFile.print('R');
										}
										if ((bottomBlock[0] - 1) == minFirst) {
											downPressed = true;
										}
									}
								}
								if (figure == 3) {
									if (minFirst < 8) {
										if (blocksMatrix[min - 1][minFirst + 1] == '0'
												&& blocksMatrix[min - 1][minFirst + 2] == '0') {
											if (figureState != 3) {
												spin();
												savingFile.print('U');
											}
											if (bottomBlock[0] > minFirst) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if (bottomBlock[0] < minFirst) {
												moveRight = true;
												moveFigure();
												savingFile.print('R');
											}
											if (bottomBlock[0] == minFirst) {
												downPressed = true;
											}
										} else {
											if (bottomBlock[0] > minFirst) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if (bottomBlock[0] < minFirst) {
												moveRight = true;
												moveFigure();
												savingFile.print('R');
											}
											if (bottomBlock[0] == minFirst) {
												downPressed = true;
											}
										}
									} else {
										if (bottomBlock[0] > minFirst) {
											moveLeft = true;
											moveFigure();
											savingFile.print('L');
										}
										if (bottomBlock[0] < minFirst) {
											moveRight = true;
											moveFigure();
											savingFile.print('R');
										}
										if (bottomBlock[0] == minFirst) {
											downPressed = true;
										}
									}
								}
								if (figure == 4) {
									if (minFirst > 0) {
										if (blocksMatrix[min - 1][minFirst - 1] == '0'
												&& blocksMatrix[min - 2][minFirst - 1] == '0') {
											if (bottomBlock[0] > minFirst) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if (bottomBlock[0] < minFirst) {
												moveRight = true;
												moveFigure();
												savingFile.print('R');
											}
											if (bottomBlock[0] == minFirst) {
												downPressed = true;
											}

										} else {
											if (bottomBlock[0] > 1) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if (bottomBlock[0] == 1) {
												downPressed = true;
											}
										}
									} else {
										if (bottomBlock[0] > 1) {
											moveLeft = true;
											moveFigure();
											savingFile.print('L');
										}
										if (bottomBlock[0] == 1) {
											downPressed = true;
										}
									}
								}
								if (figure == 5) {
									if (minFirst < 9) {
										if (blocksMatrix[min - 1][minFirst + 1] == '0'
												&& blocksMatrix[min - 2][minFirst + 1] == '0') {
											if ((bottomBlock[0] - 1) > minFirst) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if ((bottomBlock[0] - 1) < minFirst) {
												moveRight = true;
												moveFigure();
												savingFile.print('R');
											}
											if ((bottomBlock[0] - 1) == minFirst) {
												downPressed = true;
											}

										} else {
											if (bottomBlock[0] > 1) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if (bottomBlock[0] == 1) {
												downPressed = true;
											}
										}
									} else {
										if (bottomBlock[0] > 1) {
											moveLeft = true;
											moveFigure();
											savingFile.print('L');
										}
										if (bottomBlock[0] == 1) {
											downPressed = true;
										}
									}
								}
								if (figure == 6) {
									if (minFirst < 9 && minFirst > 0) {
										if (blocksMatrix[min - 1][minFirst - 1] == '0'
												&& blocksMatrix[min - 1][minFirst + 1] == '0') {
											if ((bottomBlock[0] - 2) > minFirst) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if ((bottomBlock[0] - 2) > minFirst) {
												moveRight = true;
												moveFigure();
												savingFile.print('R');
											}
											if ((bottomBlock[0] - 2) == minFirst) {
												downPressed = true;
											}
										} else {
											if (blocksMatrix[min - 1][minFirst - 1] == '0') {
												if (figureState != 1) {
													spin();
													savingFile.print('U');
												}
												if ((bottomBlock[0] - 1) > minFirst) {
													moveLeft = true;
													moveFigure();
													savingFile.print('L');
												}
												if ((bottomBlock[0] - 1) > minFirst) {
													moveRight = true;
													moveFigure();
													savingFile.print('R');
												}
												if ((bottomBlock[0] - 1) == minFirst) {
													downPressed = true;
												}
											}
											if (blocksMatrix[min - 1][minFirst + 1] == '0') {
												if (figureState != 3) {
													spin();
													savingFile.print('U');
												}
												if ((bottomBlock[0] - 2) > minFirst) {
													moveLeft = true;
													moveFigure();
													savingFile.print('L');
												}
												if ((bottomBlock[0] - 2) > minFirst) {
													moveRight = true;
													moveFigure();
													savingFile.print('R');
												}
												if ((bottomBlock[0] - 2) == minFirst) {
													downPressed = true;
												}
											}
											if (blocksMatrix[min - 1][minFirst - 1] != '0'
													&& blocksMatrix[min - 1][minFirst + 1] != '0') {
												if (bottomBlock[0] > 2) {
													moveLeft = true;
													moveFigure();
													savingFile.print('L');
												}
												if (bottomBlock[0] == 2) {
													downPressed = true;
												}
											}
										}
									} else {
										if (minFirst == 0) {
											if (figureState != 3) {
												spin();
												savingFile.print('U');
											}
											if ((bottomBlock[0] - 2) > 0) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if ((bottomBlock[0] - 2) > 0) {
												moveRight = true;
												moveFigure();
												savingFile.print('R');
											}
											if ((bottomBlock[0] - 2) == 0) {
												downPressed = true;
											}
										}
										if (minFirst == 9) {
											if (figureState != 1) {
												spin();
												savingFile.print('U');
											}
											if (bottomBlock[0] > 0) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if (bottomBlock[0] > 0) {
												moveRight = true;
												moveFigure();
												savingFile.print('R');
											}
											if (bottomBlock[0] == 0) {
												downPressed = true;
											}
										}
									}

								}

							}
							if (numberOfMins == 2) {
								if (figure == 0) {
									if (bottomBlock[0] > minFirst) {
										moveLeft = true;
										moveFigure();
										savingFile.print('L');
									}
									if (bottomBlock[0] < minFirst) {
										moveRight = true;
										moveFigure();
										savingFile.print('R');
									}
									if (bottomBlock[0] == minFirst) {
										downPressed = true;
									}
								}
								if (figure == 1) {
									if (bottomBlock[0] > minFirst + 1) {
										moveLeft = true;
										moveFigure();
										savingFile.print('L');
									}
									if (bottomBlock[0] < minFirst + 1) {
										moveRight = true;
										moveFigure();
										savingFile.print('R');
									}
									if (bottomBlock[0] == minFirst + 1) {
										downPressed = true;
									}
								}
								if (figure == 2) {
									if (figureState != 2 && bottomBlock[1] < 13) {
										spin();
										savingFile.print('U');
									}
									if (bottomBlock[0] > minFirst + 1) {
										moveLeft = true;
										moveFigure();
										savingFile.print('L');
									}
									if (bottomBlock[0] < minFirst + 1) {
										moveRight = true;
										moveFigure();
										savingFile.print('R');
									}
									if (bottomBlock[0] == minFirst + 1) {
										downPressed = true;
									}

								}
								if (figure == 3) {
									if (figureState != 2) {
										spin();
										savingFile.print('U');
									}
									if (bottomBlock[0] > minFirst + 1) {
										moveLeft = true;
										moveFigure();
										savingFile.print('L');
									}
									if (bottomBlock[0] < minFirst + 1) {
										moveRight = true;
										moveFigure();
										savingFile.print('R');
									}
									if (bottomBlock[0] == minFirst + 1) {
										downPressed = true;
									}

								}
								if (figure == 4) {
									if (minFirst < 8) {
										if (blocksMatrix[min - 1][minFirst + 2] == '0') {
											if (figureState != 1) {
												spin();
												savingFile.print('U');
											}
											if (bottomBlock[0] > minFirst + 2) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if (bottomBlock[0] < minFirst + 2) {
												moveRight = true;
												moveFigure();
												savingFile.print('R');
											}
											if (bottomBlock[0] == minFirst + 2) {
												downPressed = true;
											}
										} else {
											if (bottomBlock[0] > minFirst + 1) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if (bottomBlock[0] < minFirst + 1) {
												moveRight = true;
												moveFigure();
												savingFile.print('R');
											}
											if (bottomBlock[0] == minFirst + 1) {
												downPressed = true;
											}
										}
									} else {
										if (bottomBlock[0] > minFirst + 1) {
											moveLeft = true;
											moveFigure();
											savingFile.print('L');
										}
										if (bottomBlock[0] < minFirst + 1) {
											moveRight = true;
											moveFigure();
											savingFile.print('R');
										}
										if (bottomBlock[0] == minFirst + 1) {
											downPressed = true;
										}
									}
								}
								if (figure == 5) {
									if (minFirst > 1) {
										if (blocksMatrix[min - 1][minFirst - 1] == '0') {
											if (figureState != 1) {
												spin();
												savingFile.print('U');
											}
											if (bottomBlock[0] > minFirst + 1) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if (bottomBlock[0] < minFirst + 1) {
												moveRight = true;
												moveFigure();
												savingFile.print('R');
											}
											if (bottomBlock[0] == minFirst + 1) {
												downPressed = true;
											}
										} else {
											if (bottomBlock[0] > minFirst + 1) {
												moveLeft = true;
												moveFigure();
												savingFile.print('L');
											}
											if (bottomBlock[0] < minFirst + 1) {
												moveRight = true;
												moveFigure();
												savingFile.print('R');
											}
											if (bottomBlock[0] == minFirst + 1) {
												downPressed = true;
											}
										}
									} else {
										if (bottomBlock[0] > minFirst + 1) {
											moveLeft = true;
											moveFigure();
											savingFile.print('L');
										}
										if (bottomBlock[0] < minFirst + 1) {
											moveRight = true;
											moveFigure();
											savingFile.print('R');
										}
										if (bottomBlock[0] == minFirst + 1) {
											downPressed = true;
										}
									}
								}
								if (figure == 6) {
									if (figureState != 3) {
										spin();
										savingFile.print('U');
									}
									if ((bottomBlock[0] - 2) > minFirst) {
										moveLeft = true;
										moveFigure();
										savingFile.print('L');
									}
									if ((bottomBlock[0] - 2) < minFirst) {
										moveRight = true;
										moveFigure();
										savingFile.print('R');
									}
									if ((bottomBlock[0] - 2) == minFirst) {
										downPressed = true;
									}
								}
							}
							if (numberOfMins > 2) {
								if (figure == 0) {
									if (numberOfMins > 3 && !lineSpinned) {
										spin();
										savingFile.print('U');
									}
									if (bottomBlock[0] > minFirst + 3) {
										moveLeft = true;
										moveFigure();
										savingFile.print('L');
									}
									if (bottomBlock[0] < minFirst + 3) {
										moveRight = true;
										moveFigure();
										savingFile.print('R');
									}
									if (bottomBlock[0] == minFirst + 3) {
										downPressed = true;
									}

								}
								if (figure == 1) {
									if (bottomBlock[0] > minFirst + 1) {
										moveLeft = true;
										moveFigure();
										savingFile.print('L');
									}
									if (bottomBlock[0] < minFirst + 1) {
										moveRight = true;
										moveFigure();
										savingFile.print('R');
									}
									if (bottomBlock[0] == minFirst + 1) {
										downPressed = true;
									}
								}
								if (figure == 2) {
									if (figureState != 3 && bottomBlock[1] < 13) {
										spin();
										savingFile.print('U');
									}
									if ((bottomBlock[0] - 2) > minFirst) {
										moveLeft = true;
										moveFigure();
										savingFile.print('L');
									}
									if ((bottomBlock[0] - 2) < minFirst) {
										moveRight = true;
										moveFigure();
										savingFile.print('R');
									}
									if ((bottomBlock[0] - 2) == minFirst) {
										downPressed = true;
									}
								}
								if (figure == 3) {
									if (figureState != 1) {
										spin();
										savingFile.print('U');
									}
									if ((bottomBlock[0] - 2) > minFirst) {
										moveLeft = true;
										moveFigure();
										savingFile.print('L');
									}
									if ((bottomBlock[0] - 2) < minFirst) {
										moveRight = true;
										moveFigure();
										savingFile.print('R');
									}
									if ((bottomBlock[0] - 2) == minFirst) {
										downPressed = true;
									}
								}
								if (figure == 4) {
									if (figureState != 1) {
										spin();
										savingFile.print('U');
									}
									if ((bottomBlock[0] - 2) > minFirst) {
										moveLeft = true;
										moveFigure();
										savingFile.print('L');
									}
									if ((bottomBlock[0] - 2) < minFirst) {
										moveRight = true;
										moveFigure();
										savingFile.print('R');
									}
									if ((bottomBlock[0] - 2) == minFirst) {
										downPressed = true;
									}
								}
								if (figure == 5) {
									if (figureState != 1) {
										spin();
										savingFile.print('U');
									}
									if ((bottomBlock[0] - 2) > minFirst) {
										moveLeft = true;
										moveFigure();
										savingFile.print('L');
									}
									if ((bottomBlock[0] - 2) < minFirst) {
										moveRight = true;
										moveFigure();
										savingFile.print('R');
									}
									if ((bottomBlock[0] - 2) == minFirst) {
										downPressed = true;
									}
								}
								if (figure == 6) {
									if (figureState != 2) {
										spin();
										savingFile.print('U');
									}

									if ((bottomBlock[0] - 2) > minFirst) {
										moveLeft = true;
										moveFigure();
										savingFile.print('L');
									}
									if ((bottomBlock[0] - 2) < minFirst) {
										moveRight = true;
										moveFigure();
										savingFile.print('R');
									}
									if ((bottomBlock[0] - 2) == minFirst) {
										downPressed = true;
									}

								}
							}
							/*
							 * System.out.print(min); System.out.print(' ');
							 * System.out.print(numberOfMins);
							 * System.out.print(' ');
							 * System.out.print(minFirst); System.out.println();
							 * for (int i = 0; i < 10; i++) { mostEmpty[i] = 0;
							 * }
							 */
						}
					}
				}));
		if (autoPlay) {
			autoPlayer.setCycleCount(Timeline.INDEFINITE);
			// autoPlayer.play();
		}
	}

	private void spin() {
		if (autoPlay) {
			autoPlayer.pause();
			if (bottomBlock[1] < 13
					&& (blocksMatrix[bottomBlock[1] + 1][bottomBlock[0]] != '~')
					&& (blocksMatrix[bottomBlock[1] + 1][bottomBlock[0] - 1] != '~')
					&& (blocksMatrix[bottomBlock[1] + 1][bottomBlock[0] - 2] != '~')) {
			} else
				return;
		}
		// if (autoPlay)
		// savingFile.print('U');
		/*
		 * System.out.println("Trying to spin"); System.out.println(figure);
		 */
		// gameTimeline.pause();
		if (figure == 0) {
			if (bottomBlock[0] > 2) {
				if (checkPossibility()) {
					if (!lineSpinned) {
						blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] = blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]];
						blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] = '0';
						blocksMatrix[bottomBlock[1]][bottomBlock[0] - 2] = blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]];
						blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] = '0';
						blocksMatrix[bottomBlock[1]][bottomBlock[0] - 3] = blocksMatrix[bottomBlock[1] - 3][bottomBlock[0]];
						blocksMatrix[bottomBlock[1] - 3][bottomBlock[0]] = '0';
						lineSpinned = true;
					} else {
						blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] = blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1];
						blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] = '0';
						blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] = blocksMatrix[bottomBlock[1]][bottomBlock[0] - 2];
						blocksMatrix[bottomBlock[1]][bottomBlock[0] - 2] = '0';
						blocksMatrix[bottomBlock[1] - 3][bottomBlock[0]] = blocksMatrix[bottomBlock[1]][bottomBlock[0] - 3];
						blocksMatrix[bottomBlock[1]][bottomBlock[0] - 3] = '0';
						lineSpinned = false;
					}
				}
			} else {
				if (checkPossibility()) {
					if (!lineSpinned) {
						blocksMatrix[bottomBlock[1]][bottomBlock[0] + 1] = blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]];
						blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] = '0';
						blocksMatrix[bottomBlock[1]][bottomBlock[0] + 2] = blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]];
						blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] = '0';
						blocksMatrix[bottomBlock[1]][bottomBlock[0] + 3] = blocksMatrix[bottomBlock[1] - 3][bottomBlock[0]];
						blocksMatrix[bottomBlock[1] - 3][bottomBlock[0]] = '0';
						lineSpinned = true;
					} else {
						blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] = blocksMatrix[bottomBlock[1]][bottomBlock[0] + 1];
						blocksMatrix[bottomBlock[1]][bottomBlock[0] + 1] = '0';
						blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] = blocksMatrix[bottomBlock[1]][bottomBlock[0] + 2];
						blocksMatrix[bottomBlock[1]][bottomBlock[0] + 2] = '0';
						blocksMatrix[bottomBlock[1] - 3][bottomBlock[0]] = blocksMatrix[bottomBlock[1]][bottomBlock[0] + 3];
						blocksMatrix[bottomBlock[1]][bottomBlock[0] + 3] = '0';
						lineSpinned = false;
					}
				}
			}
		}
		if (figure == 2 || figure == 3 || figure == 4 || figure == 5
				|| figure == 6) {
			if (figureState == 0 && !wasSpinned) {
				if (checkPossibility()) {
					nullifyMatrix();
					figureState = 1;
					setNewFigure();
					wasSpinned = true;
				}
			}
			if (figureState == 1 && !wasSpinned) {
				if (checkPossibility()) {
					nullifyMatrix();
					if (figure != 4 && figure != 5)
						figureState = 2;
					else
						figureState = 0;
					setNewFigure();
					wasSpinned = true;
				}
			}
			if (figureState == 2 && !wasSpinned) {
				if (checkPossibility()) {
					nullifyMatrix();
					figureState = 3;
					setNewFigure();
					wasSpinned = true;
				}
			}
			if (figureState == 3 && !wasSpinned) {
				if (checkPossibility()) {
					nullifyMatrix();
					figureState = 0;
					setNewFigure();
					wasSpinned = true;
				}
			}
		}
		wasSpinned = false;
		check();
		// gameTimeline.play();

		if (autoPlay && !timeToStop)
			autoPlayer.play();
	}

	private boolean checkPossibility() {
		if (figure == 0) {
			if (lineSpinned == false) {
				if (bottomBlock[0] > 2) {
					if ((blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '1')
							&& (blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] == '1')
							&& (blocksMatrix[bottomBlock[1]][bottomBlock[0] - 2] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0] - 2] == '1')
							&& (blocksMatrix[bottomBlock[1]][bottomBlock[0] - 3] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0] - 3] == '1'))
						return true;
				} else {
					if ((blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '1')
							&& (blocksMatrix[bottomBlock[1]][bottomBlock[0] + 1] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0] + 1] == '1')
							&& (blocksMatrix[bottomBlock[1]][bottomBlock[0] + 2] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0] + 2] == '1')
							&& (blocksMatrix[bottomBlock[1]][bottomBlock[0] + 3] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0] + 3] == '1'))
						return true;
				}
			} else {
				if ((blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '1')
						&& (blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] == '1')
						&& (blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] == '1')
						&& (blocksMatrix[bottomBlock[1] - 3][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1] - 3][bottomBlock[0]] == '1'))
					return true;
			}
		}
		if (figureState == 0) {
			if (figure == 2) {
				if ((blocksMatrix[topBlock[1]][topBlock[0]] == '0' || blocksMatrix[topBlock[1]][topBlock[0]] == '3')
						&& (blocksMatrix[topBlock[1]][topBlock[0] + 1] == '0' || blocksMatrix[topBlock[1]][topBlock[0] + 1] == '3')
						&& (blocksMatrix[topBlock[1]][topBlock[0] + 2] == '0' || blocksMatrix[topBlock[1]][topBlock[0] + 2] == '3')
						&& (blocksMatrix[topBlock[1] + 1][topBlock[0] + 2] == '0' || blocksMatrix[topBlock[1] + 1][topBlock[0] + 2] == '3'))
					return true;
			}
			if (figure == 3) {
				if ((blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '4')
						&& (blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] == '4')
						&& (blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] == '4')
						&& (blocksMatrix[bottomBlock[1]][bottomBlock[0] - 2] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0] - 2] == '4'))
					return true;
			}
			if (figure == 4) {
				if ((blocksMatrix[topBlock[1]][topBlock[0] + 1] == '0' || blocksMatrix[topBlock[1]][topBlock[0] + 1] == '5')
						&& (blocksMatrix[topBlock[1]][topBlock[0] + 2] == '0' || blocksMatrix[topBlock[1]][topBlock[0] + 2] == '5')
						&& (blocksMatrix[topBlock[1] + 1][topBlock[0]] == '0' || blocksMatrix[topBlock[1] + 1][topBlock[0]] == '5')
						&& (blocksMatrix[topBlock[1] + 1][topBlock[0] + 1] == '0' || blocksMatrix[topBlock[1] + 1][topBlock[0] + 1] == '5'))
					return true;
			}
			if (figure == 5) {
				if ((blocksMatrix[topBlock[1]][topBlock[0]] == '0' || blocksMatrix[topBlock[1]][topBlock[0]] == '6')
						&& (blocksMatrix[topBlock[1]][topBlock[0] + 1] == '0' || blocksMatrix[topBlock[1]][topBlock[0] + 1] == '6')
						&& (blocksMatrix[topBlock[1] + 1][topBlock[0] + 1] == '0' || blocksMatrix[topBlock[1] + 1][topBlock[0] + 1] == '6')
						&& (blocksMatrix[topBlock[1] + 1][topBlock[0] + 2] == '0' || blocksMatrix[topBlock[1] + 1][topBlock[0] + 2] == '6'))
					return true;
			}
			if (figure == 6) {
				if ((blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '7')
						&& (blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] == '7')
						&& (blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 1] == '0' || blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 1] == '7')
						&& (blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] == '7'))
					return true;
			}
		}
		if (figureState == 1) {
			if (figure == 2) {
				if ((blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '3')
						&& (blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] == '3')
						&& (blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] == '3')
						&& (blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] == '3'))
					return true;
			}
			if (figure == 3) {
				if ((blocksMatrix[topBlock[1]][topBlock[0]] == '0' || blocksMatrix[topBlock[1]][topBlock[0]] == '4')
						&& (blocksMatrix[topBlock[1] + 1][topBlock[0]] == '0' || blocksMatrix[topBlock[1] + 1][topBlock[0] + 1] == '4')
						&& (blocksMatrix[topBlock[1] + 2][topBlock[0]] == '0' || blocksMatrix[topBlock[1] + 2][topBlock[0] + 2] == '4')
						&& (blocksMatrix[topBlock[1] + 2][topBlock[0] + 1] == '0' || blocksMatrix[topBlock[1] + 2][topBlock[0] + 1] == '4'))
					return true;
			}
			if (figure == 4) {
				if ((blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '5')
						&& (blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 1] == '5')
						&& (blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 1] == '0' || blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 1] == '5')
						&& (blocksMatrix[bottomBlock[1] - 2][bottomBlock[0] - 1] == '0' || blocksMatrix[bottomBlock[1] - 2][bottomBlock[0] - 1] == '5'))
					return true;
			}
			if (figure == 5) {
				if ((blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] == '6')
						&& (blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 1] == '0' || blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 1] == '6')
						&& (blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] == '6')
						&& (blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] == '6'))
					return true;
			}
			if (figure == 6) {
				if ((blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '7')
						&& (blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] == '7')
						&& (blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 1] == '0' || blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 1] == '7')
						&& (blocksMatrix[bottomBlock[1]][bottomBlock[0] - 2] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0] - 2] == '7'))
					return true;
			}
		}
		if (figureState == 2) {
			if (figure == 2) {
				if ((blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '3')
						&& (blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] == '3')
						&& (blocksMatrix[bottomBlock[1]][bottomBlock[0] - 2] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0] - 2] == '3')
						&& (blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 2] == '0' || blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 2] == '3'))
					return true;
			}
			if (figure == 3) {
				if ((blocksMatrix[topBlock[1]][topBlock[0]] == '0' || blocksMatrix[topBlock[1]][topBlock[0]] == '4')
						&& (blocksMatrix[topBlock[1] + 1][topBlock[0]] == '0' || blocksMatrix[topBlock[1] + 1][topBlock[0]] == '4')
						&& (blocksMatrix[topBlock[1]][topBlock[0] + 1] == '0' || blocksMatrix[topBlock[1]][topBlock[0] + 1] == '4')
						&& (blocksMatrix[topBlock[1]][topBlock[0] + 2] == '0' || blocksMatrix[topBlock[1]][topBlock[0] + 2] == '4'))
					return true;
			}
			if (figure == 6) {
				if ((blocksMatrix[topBlock[1]][topBlock[0]] == '0' || blocksMatrix[topBlock[1]][topBlock[0]] == '7')
						&& (blocksMatrix[topBlock[1] + 1][topBlock[0]] == '0' || blocksMatrix[topBlock[1] + 1][topBlock[0]] == '7')
						&& (blocksMatrix[topBlock[1] + 1][topBlock[0] + 1] == '0' || blocksMatrix[topBlock[1] + 1][topBlock[0] + 1] == '7')
						&& (blocksMatrix[topBlock[1] + 2][topBlock[0]] == '0' || blocksMatrix[topBlock[1] + 2][topBlock[0]] == '7'))
					return true;
			}
		}
		if (figureState == 3) {
			if (figure == 2) {
				if ((blocksMatrix[topBlock[1]][topBlock[0]] == '0' || blocksMatrix[topBlock[1]][topBlock[0]] == '3')
						&& (blocksMatrix[topBlock[1]][topBlock[0] + 1] == '0' || blocksMatrix[topBlock[1]][topBlock[0] + 1] == '3')
						&& (blocksMatrix[topBlock[1] + 1][topBlock[0]] == '0' || blocksMatrix[topBlock[1] + 1][topBlock[0]] == '3')
						&& (blocksMatrix[topBlock[1] + 2][topBlock[0]] == '0' || blocksMatrix[topBlock[1] + 2][topBlock[0]] == '3'))
					return true;
			}
			if (figure == 3) {
				if ((blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1]][bottomBlock[0]] == '4')
						&& (blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] == '4')
						&& (blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] == '0' || blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] == '4')
						&& (blocksMatrix[bottomBlock[1] - 2][bottomBlock[0] - 1] == '0' || blocksMatrix[bottomBlock[1] - 2][bottomBlock[0] - 1] == '4'))
					return true;
			}
			if (figure == 6) {
				if ((blocksMatrix[topBlock[1]][topBlock[0]] == '0' || blocksMatrix[topBlock[1]][topBlock[0]] == '7')
						&& (blocksMatrix[topBlock[1]][topBlock[0] + 1] == '0' || blocksMatrix[topBlock[1]][topBlock[0] + 1] == '7')
						&& (blocksMatrix[topBlock[1] + 1][topBlock[0] + 1] == '0' || blocksMatrix[topBlock[1] + 1][topBlock[0] + 1] == '7')
						&& (blocksMatrix[topBlock[1]][topBlock[0] + 2] == '0' || blocksMatrix[topBlock[1]][topBlock[0] + 2] == '7'))
					return true;
			}
		}
		return false;
	}

	private void check() {
		// gameTimeline.pause();
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 10; j++) {
				if (blocksMatrix[i][j] == '1')
					gameBoard[i][j].setImage(imageBlock[0]);
				if (blocksMatrix[i][j] == '2')
					gameBoard[i][j].setImage(imageBlock[1]);
				if (blocksMatrix[i][j] == '3')
					gameBoard[i][j].setImage(imageBlock[2]);
				if (blocksMatrix[i][j] == '4')
					gameBoard[i][j].setImage(imageBlock[3]);
				if (blocksMatrix[i][j] == '5')
					gameBoard[i][j].setImage(imageBlock[4]);
				if (blocksMatrix[i][j] == '6')
					gameBoard[i][j].setImage(imageBlock[5]);
				if (blocksMatrix[i][j] == '7')
					gameBoard[i][j].setImage(imageBlock[6]);
				if (blocksMatrix[i][j] == '0')
					gameBoard[i][j].setImage(null);
			}
		}
		// gameTimeline.play();
	}

	private void downProceeding() {
		if (autoPlay) {
			timeToStop = true;
			autoPlayer.pause();
			try {

				engine.setMatrix(blocksMatrix);
				synchronized (semaphore) {
					engine.startChecking();
					semaphore.notifyAll();
					if (engine.getChecking())
						semaphore.wait();
				}
				boolean cannotMove = engine.getMoveState();
				if (cannotMove) {
					return;
				}
			} catch (Throwable error) {
				error.printStackTrace();
			}
		}
		// gameTimeline.pause();
		int blocks = 0;
		for (int i = 14; i > -1; i--)
			for (int j = 0; j < 10; j++) {
				if (blocksMatrix[i][j] == '1' || blocksMatrix[i][j] == '2'
						|| blocksMatrix[i][j] == '3'
						|| blocksMatrix[i][j] == '4'
						|| blocksMatrix[i][j] == '5'
						|| blocksMatrix[i][j] == '6'
						|| blocksMatrix[i][j] == '7') {
					blocksMatrix[i + 1][j] = blocksMatrix[i][j];
					blocksMatrix[i][j] = '0';
					blocks++;
					if (blocks == 4)
						break;
				}
				if (blocks == 4)
					break;
			}
		blocks = 0;
		topBlock[1]++;
		bottomBlock[1]++;
		if (bottomBlock[1] > 14) {
			topBlock[1]--;
			bottomBlock[1]--;
		}
		if (!loading)
			savingFile.println('N');
		if (autoPlay) {
			try {

				engine.setMatrix(blocksMatrix);
				synchronized (semaphore) {
					engine.startChecking();
					semaphore.notifyAll();
					if (engine.getChecking())
						semaphore.wait();
				}
				boolean cannotMove = engine.getMoveState();
				if (!cannotMove) {
					timeToStop = false;
					autoPlayer.play();
				}
			} catch (Throwable error) {
				error.printStackTrace();
			}
		}
		// gameTimeline.play();

	}

	private void moveFigure() {
		// if (autoPlay) {
		// autoPlayer.pause();

		// }
		// gameTimeline.pause();
		int blocks = 0;
		boolean canMove = true;

		canMove = checkMoveAllowed(blocksMatrix);
		if (canMove) {
			for (int i = 14; i > -1; i--)
				for (int j = 0; j < 10; j++) {
					if ((blocksMatrix[i][j] == '1' || blocksMatrix[i][j] == '2'
							|| blocksMatrix[i][j] == '3'
							|| blocksMatrix[i][j] == '4'
							|| blocksMatrix[i][j] == '5'
							|| blocksMatrix[i][j] == '6' || blocksMatrix[i][j] == '7')
							&& moveLeft) {
						blocksMatrix[i][j - 1] = blocksMatrix[i][j];
						blocksMatrix[i][j] = '0';
						blocks++;
						if (blocks == 4)
							break;
					}
					if (blocks == 4)
						break;
				}
			for (int i = 14; i > -1; i--)
				for (int j = 9; j > -1; j--) {
					if ((blocksMatrix[i][j] == '1' || blocksMatrix[i][j] == '2'
							|| blocksMatrix[i][j] == '3'
							|| blocksMatrix[i][j] == '4'
							|| blocksMatrix[i][j] == '5'
							|| blocksMatrix[i][j] == '6' || blocksMatrix[i][j] == '7')
							&& moveRight) {
						blocksMatrix[i][j + 1] = blocksMatrix[i][j];
						blocksMatrix[i][j] = '0';
						blocks++;
						if (blocks == 4)
							break;
					}
					if (blocks == 4)
						break;
				}
			if (moveLeft) {
				// if (autoPlay)
				// savingFile.print('L');
				topBlock[0]--;
				bottomBlock[0]--;
			}
			if (moveRight) {
				// if (autoPlay)
				// savingFile.print('R');
				topBlock[0]++;
				bottomBlock[0]++;
			}
			if (topBlock[0] < 0) {
				topBlock[0]++;
				bottomBlock[0]++;
			}
			if (bottomBlock[0] > 9) {
				topBlock[0]--;
				bottomBlock[0]--;
			}
			blocks = 0;
			moveLeft = false;
			moveRight = false;
		}
		blocks = 0;
		moveLeft = false;
		moveRight = false;
		check();
		// if (autoPlay)
		// autoPlayer.play();
		// gameTimeline.play();
	}

	private void nullifyMatrix() {
		for (int i = topBlock[1]; i < bottomBlock[1] + 1; i++)
			for (int j = topBlock[0]; j < bottomBlock[0] + 1; j++)
				if (blocksMatrix[i][j] != '0' && blocksMatrix[i][j] != '~') {
					blocksMatrix[i][j] = '0';
				}
	}

	private void setNewFigure() {
		if (figure == 2) {
			if (figureState == 1) {
				blocksMatrix[topBlock[1]][topBlock[0]] = '3';
				blocksMatrix[topBlock[1]][topBlock[0] + 1] = '3';
				blocksMatrix[topBlock[1]][topBlock[0] + 2] = '3';
				blocksMatrix[topBlock[1] + 1][topBlock[0] + 2] = '3';
			}
			if (figureState == 2) {
				blocksMatrix[bottomBlock[1]][bottomBlock[0]] = '3';
				blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] = '3';
				blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] = '3';
				blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] = '3';
			}
			if (figureState == 3) {
				blocksMatrix[bottomBlock[1]][bottomBlock[0]] = '3';
				blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] = '3';
				blocksMatrix[bottomBlock[1]][bottomBlock[0] - 2] = '3';
				blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 2] = '3';
			}
			if (figureState == 0) {
				blocksMatrix[topBlock[1]][topBlock[0]] = '3';
				blocksMatrix[topBlock[1]][topBlock[0] + 1] = '3';
				blocksMatrix[topBlock[1] + 1][topBlock[0]] = '3';
				blocksMatrix[topBlock[1] + 2][topBlock[0]] = '3';
			}
		}
		if (figure == 3) {
			if (figureState == 1) {
				blocksMatrix[bottomBlock[1]][bottomBlock[0]] = '4';
				blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] = '4';
				blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] = '4';
				blocksMatrix[bottomBlock[1]][bottomBlock[0] - 2] = '4';
			}
			if (figureState == 2) {
				blocksMatrix[topBlock[1]][topBlock[0]] = '4';
				blocksMatrix[topBlock[1] + 1][topBlock[0]] = '4';
				blocksMatrix[topBlock[1] + 2][topBlock[0]] = '4';
				blocksMatrix[topBlock[1] + 2][topBlock[0] + 1] = '4';
			}
			if (figureState == 3) {
				blocksMatrix[topBlock[1]][topBlock[0]] = '4';
				blocksMatrix[topBlock[1] + 1][topBlock[0]] = '4';
				blocksMatrix[topBlock[1]][topBlock[0] + 1] = '4';
				blocksMatrix[topBlock[1]][topBlock[0] + 2] = '4';
			}
			if (figureState == 0) {
				blocksMatrix[bottomBlock[1]][bottomBlock[0]] = '4';
				blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] = '4';
				blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] = '4';
				blocksMatrix[bottomBlock[1] - 2][bottomBlock[0] - 1] = '4';
			}
		}
		if (figure == 4) {
			if (figureState == 1) {
				blocksMatrix[topBlock[1]][topBlock[0] + 1] = '5';
				blocksMatrix[topBlock[1]][topBlock[0] + 2] = '5';
				blocksMatrix[topBlock[1] + 1][topBlock[0]] = '5';
				blocksMatrix[topBlock[1] + 1][topBlock[0] + 1] = '5';
			}

			if (figureState == 0) {
				blocksMatrix[bottomBlock[1]][bottomBlock[0]] = '5';
				blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] = '5';
				blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 1] = '5';
				blocksMatrix[bottomBlock[1] - 2][bottomBlock[0] - 1] = '5';
			}
		}
		if (figure == 5) {
			if (figureState == 1) {
				blocksMatrix[topBlock[1]][topBlock[0]] = '6';
				blocksMatrix[topBlock[1]][topBlock[0] + 1] = '6';
				blocksMatrix[topBlock[1] + 1][topBlock[0] + 1] = '6';
				blocksMatrix[topBlock[1] + 1][topBlock[0] + 2] = '6';
			}
			if (figureState == 0) {
				blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] = '6';
				blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 1] = '6';
				blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] = '6';
				blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] = '6';
			}
		}
		if (figure == 6) {
			if (figureState == 1) {
				blocksMatrix[bottomBlock[1]][bottomBlock[0]] = '7';
				blocksMatrix[bottomBlock[1] - 1][bottomBlock[0]] = '7';
				blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 1] = '7';
				blocksMatrix[bottomBlock[1] - 2][bottomBlock[0]] = '7';
			}
			if (figureState == 2) {
				blocksMatrix[bottomBlock[1]][bottomBlock[0]] = '7';
				blocksMatrix[bottomBlock[1]][bottomBlock[0] - 1] = '7';
				blocksMatrix[bottomBlock[1] - 1][bottomBlock[0] - 1] = '7';
				blocksMatrix[bottomBlock[1]][bottomBlock[0] - 2] = '7';
			}
			if (figureState == 3) {
				blocksMatrix[topBlock[1]][topBlock[0]] = '7';
				blocksMatrix[topBlock[1] + 1][topBlock[0]] = '7';
				blocksMatrix[topBlock[1] + 1][topBlock[0] + 1] = '7';
				blocksMatrix[topBlock[1] + 2][topBlock[0]] = '7';
			}
			if (figureState == 0) {
				blocksMatrix[topBlock[1]][topBlock[0]] = '7';
				blocksMatrix[topBlock[1]][topBlock[0] + 1] = '7';
				blocksMatrix[topBlock[1] + 1][topBlock[0] + 1] = '7';
				blocksMatrix[topBlock[1]][topBlock[0] + 2] = '7';
			}
		}
	}

	private void SortAll() throws IOException {
		String Text;
		long timeFirst, timeSecond;
		int numberOfLines = 0;
		boolean nextString = false;
		for (Integer i = 1; i < currentSave; i++) {
			saveGameSort = new File("saves/sg_" + i.toString() + ".txt");
			// System.out.println(i);
			try {
				sorting = new BufferedReader(new FileReader(
						saveGameSort.getAbsoluteFile()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			System.out.println(saveGameSort.getName());
			while ((Text = sorting.readLine()) != null) {

				if (Text.toCharArray()[0] == 'F'
						|| Text.toCharArray()[0] == '0'
						|| Text.toCharArray()[0] == '1'
						|| Text.toCharArray()[0] == '2'
						|| Text.toCharArray()[0] == '3'
						|| Text.toCharArray()[0] == '4'
						|| Text.toCharArray()[0] == '5'
						|| Text.toCharArray()[0] == '6'
						|| Text.toCharArray()[0] == '7'
						|| Text.toCharArray()[0] == '8'
						|| Text.toCharArray()[0] == '9') {
					nextString = true;

				}
				if (!nextString) {
					for (int symbol = 0; symbol < Text.length(); symbol++) {
						// if(symbol >= Text.length())
						// break;
						if ((Text.toCharArray()[symbol] == 'N'
								|| Text.toCharArray()[symbol] == '0'
								|| Text.toCharArray()[symbol] == '1'
								|| Text.toCharArray()[symbol] == '2'
								|| Text.toCharArray()[symbol] == '3'
								|| Text.toCharArray()[symbol] == '4'
								|| Text.toCharArray()[symbol] == '5'
								|| Text.toCharArray()[symbol] == '6'
								|| Text.toCharArray()[symbol] == '7'
								|| Text.toCharArray()[symbol] == '8' || Text
									.toCharArray()[symbol] == '9'))
							break;
						if (Text.toCharArray()[symbol] == 'L')
							movesArray[0]++;
						if (Text.toCharArray()[symbol] == 'R')
							movesArray[1]++;
						if (Text.toCharArray()[symbol] == 'U')
							movesArray[2]++;
						// symbol++;
					}
					// symbol = 0;
					numberOfLines++;
				}
				nextString = false;
			}

			filesSizes[i - 1] = numberOfLines;
			filesSorted[i - 1] = numberOfLines;
			numberOfLines = 0;
		}
		int[] arrayMoves = new int[3];
		for (int j = 0; j < 3; j++) {
			arrayMoves[j] = movesArray[j];
		}



		for (int j = 0; j < 3; j++) {
			if (arrayMoves[0] == 0 && arrayMoves[2] == 0 && arrayMoves[2] == 0) {
				setTextMoves("None", movesArray[j]);
				break;
			}
			if (arrayMoves[2] == movesArray[j]) {
				if (j == 0) {
					setTextMoves("Left", movesArray[j]);
				}
				if (j == 1) {
					setTextMoves("Right", movesArray[j]);
				}
				if (j == 2) {
					setTextMoves("Up", movesArray[j]);
				}
				break;
			}
		}

		timeFirst = System.currentTimeMillis();
		//for (int i = 0; i < 300000; i++) {



		/*	if (i != 299999) {
				for (int j = 0; j < currentSave - 1; j++) {
					filesSorted[j] = filesSizes[j];
				}
			}
		}*/
		timeSecond = System.currentTimeMillis();
		
		//System.out.println(scalaSorting.);

		if (currentSave > 10)
			for (int i = 0; i < 10; i++) {
				topTen[i] = filesSorted[currentSave - 2 - i];
				System.out.println(topTen[i]);
			}

		System.out.print("Scala sort time (milliseconds): ");
		System.out.println((timeSecond - timeFirst));
		for (int i = 0; i < currentSave - 1; i++) {
			if (filesSizes[i] == filesSorted[currentSave - 2]) {
				System.out.print("Scala best game number: ");
				System.out.println(i + 1);
				setTextSaves(i + 1);
				bestSave = i + 1;
				break;
			}
		}

		for (int i = 0; i < currentSave - 1; i++) {
			filesSorted[i] = filesSizes[i];
		}

		/*
		 * timeFirst = System.currentTimeMillis();
		 * 
		 * for (int i = 0; i < 300000; i++) { bubbleSort(filesSorted); if (i !=
		 * 299999) { for (int j = 0; j < currentSave - 1; j++) { filesSorted[j]
		 * = filesSizes[j]; } } }
		 * 
		 * timeSecond = System.currentTimeMillis();
		 * System.out.print("Java sort time (milliseconds): ");
		 * System.out.println((timeSecond - timeFirst)); for (int i = 0; i <
		 * currentSave; i++) { if (filesSizes[i] == filesSorted[currentSave-2])
		 * { System.out.print("Java best game number: "); System.out.println(i +
		 * 1); break; } }
		 */

	}

	private static void bubbleSort(int[] arr) {
		for (int i = arr.length - 1; i > 0; i--) {
			for (int j = 0; j < i; j++) {

				if (arr[j] > arr[j + 1]) {
					int tmp = arr[j];
					arr[j] = arr[j + 1];
					arr[j + 1] = tmp;
				}
			}
		}
	}

	private void NewSave() {
		gameTimeline.pause();
		timeToStop = true;
		StringBuilder saveBuilder = new StringBuilder();
		saveBuilder.append("saves/sg_");
		saveBuilder.append(currentSave);
		saveBuilder.append(".txt");
		savingFile.close();
		newSaveName = saveBuilder.toString();
		// System.out.println(newSaveName);
		// saveGame.renameTo(new File(newSaveName));
		// System.out.println(saveGame.getName());

		saveGame = new File(newSaveName);
		// System.out.println(saveGame.getName());
		try {
			savingFile = new PrintWriter(saveGame.getAbsoluteFile());
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		savingFile.println(difficulty);

		gameTimeline.play();

		currentSave++;

	}

	private void setTextMoves(String name, Integer size) {
		movesBest = new Text(225, 32, name + " (" + size.toString() + ")");
		movesBest.setLayoutY(50);
		movesBest.setFill(Color.CHARTREUSE);
		movesBest.setFont(Font.font("Impact", 40));
		moves.getChildren().add(movesBest);
	}

	private void setTextSaves(Integer save) {
		if (save != 0)
			savesBest = new Text(225, 32, save.toString());
		else
			savesBest = new Text(225, 32, "None");
		savesBest.setLayoutY(150);
		savesBest.setFill(Color.CHARTREUSE);
		savesBest.setFont(Font.font("Impact", 40));
		moves.getChildren().add(savesBest);
	}
}
