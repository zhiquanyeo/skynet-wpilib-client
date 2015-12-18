package com.zhiquanyeo.skynet;
	
import com.zhiquanyeo.skynet.network.SkynetConnection;
import com.zhiquanyeo.skynet.ui.MainWindowController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;


public class Main extends Application {
	private SkynetConnection d_skynetConnection = new SkynetConnection();
	
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
	        
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
