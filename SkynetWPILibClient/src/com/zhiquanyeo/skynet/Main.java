package com.zhiquanyeo.skynet;
	
import com.zhiquanyeo.skynet.network.SkynetConnection;
import com.zhiquanyeo.skynet.tests.TestSkynetConnectionListener;
import com.zhiquanyeo.skynet.ui.MainWindowController;

import edu.wpi.first.wpilibj.RobotBaseRunner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;


public class Main extends Application {
	private SkynetConnection d_skynetConnection = new SkynetConnection();
	
	private TestSkynetConnectionListener d_testListener = new TestSkynetConnectionListener();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			// Set up
			
			MainWindowController mainWindowController = new MainWindowController(d_skynetConnection);
			FXMLLoader mainWindowLoader = new FXMLLoader(getClass().getResource("ui/MainWindow.fxml"));
			mainWindowLoader.setController(mainWindowController);
			Parent root = mainWindowLoader.load();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			
			// This is done just so that we can resize the window accordingly
	        TitledPane tp = mainWindowController.getTitledPane();
	        tp.heightProperty().addListener((obs, oldHeight, newHeight) -> primaryStage.sizeToScene());
	        
	        primaryStage.titleProperty().set("Skynet WPILib Client");
	        primaryStage.show();
	        
	        // Once this is all set up, we can start running robot code
	        RobotBaseRunner robotRunner = new RobotBaseRunner(d_skynetConnection);
	        Thread t = new Thread(robotRunner);
	        t.start();
	        
	        // === TESTS ===
	        d_skynetConnection.addSubscriber(d_testListener);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
