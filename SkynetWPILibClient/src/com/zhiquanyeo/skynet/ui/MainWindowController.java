package com.zhiquanyeo.skynet.ui;

import java.util.UUID;
import java.util.logging.Logger;

import com.zhiquanyeo.skynet.network.ISkynetConnectionListener;
import com.zhiquanyeo.skynet.network.SkynetConnection;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class MainWindowController implements ISkynetConnectionListener {
	private final static Logger LOGGER = Logger.getLogger(MainWindowController.class.getName());

	@FXML
	private TitledPane tpEndpointInfo;

	@FXML
	private TextField txtHostName;
	
	@FXML
	private TextField txtHostPort;
	
	@FXML
	private TextField txtClientId;
	
    @FXML
    private Button disconnectButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label messagesLabel;

    @FXML
    private ProgressIndicator serviceRunningIndicator;

    @FXML
    private Button connectButton;
    
    @FXML
    private Label driverStationStatusLabel;

    private ConnectService connectService;
    private DisconnectService disconnectService;

    private StringProperty statusMessagesProperty;
    private BooleanProperty connectedProperty;
    private StringProperty driverStationStatusProperty;
    
    private SkynetConnection d_skynetConnection;
    
    public MainWindowController(SkynetConnection connection) {
    	d_skynetConnection = connection;
    	connection.addSubscriber(this);
    }
    
    public TitledPane getTitledPane() {
    	return tpEndpointInfo;
    }

    @FXML
    public void initialize() {
        connectService = new ConnectService();
        disconnectService = new DisconnectService();

        BooleanBinding anyServiceRunning = connectService.runningProperty().or(disconnectService.runningProperty());
        serviceRunningIndicator.visibleProperty().bind(anyServiceRunning);
        cancelButton.visibleProperty().bind(anyServiceRunning);
        connectButton.disableProperty().bind(connectedProperty().or(anyServiceRunning));
        disconnectButton.disableProperty().bind(connectedProperty().not().or(anyServiceRunning));
        messagesLabel.textProperty().bind(statusMessagesProperty());
        driverStationStatusLabel.textProperty().bind(driverStationStatusProperty());

        connectService.messageProperty().addListener((ObservableValue<? extends String> observableValue, String oldValue, String newValue) -> {
            statusMessagesProperty().set(newValue);
        });
        disconnectService.messageProperty().addListener((ObservableValue<? extends String> observableValue, String oldValue, String newValue) -> {
            statusMessagesProperty().set(newValue);
        });

        statusMessagesProperty().set("Disconnected.");
        driverStationStatusProperty().set("Disconnected");
        
        txtHostPort.textProperty().addListener((obs, oldValue, newValue) -> {
        	if (newValue.equals("")) {
        		((StringProperty) obs).setValue("1883");
        	}
        	else {
        		try {
        			// Attempt to parse the current text
        			Integer.valueOf(newValue);
        		}
        		catch (Exception e) {
        			((StringProperty) obs).setValue(oldValue);
        		}
        	}
        });
        
        txtClientId.textProperty().set("skynet-" + (100000 + (int)(Math.random() * 900000)));
    }

    @FXML
    public void cancel() {
        LOGGER.info("cancel");
        connectService.cancel();
        disconnectService.cancel();
    }

    @FXML
    public void connect() {
        LOGGER.info("connect");
        disconnectService.cancel();
        connectService.restart();
    }

    @FXML
    public void disconnect() {
        LOGGER.info("disconnect");
        connectService.cancel();
        disconnectService.restart();
    }

    private StringProperty statusMessagesProperty() {
        if (statusMessagesProperty == null) {
            statusMessagesProperty = new SimpleStringProperty();
        }
        return statusMessagesProperty;
    }

    private BooleanProperty connectedProperty() {
        if (connectedProperty == null) {
            connectedProperty = new SimpleBooleanProperty(Boolean.FALSE);
        }
        return connectedProperty;
    }
    
    private StringProperty driverStationStatusProperty() {
    	if (driverStationStatusProperty == null) {
    		driverStationStatusProperty = new SimpleStringProperty();
    	}
    	return driverStationStatusProperty;
    }

    private class ConnectService extends Service<Void> {

        @Override
        protected void succeeded() {
            statusMessagesProperty().set("Connected.");
            connectedProperty().set(true);
        }

        @Override
        protected void failed() {
            statusMessagesProperty().set("Connecting failed.");
            LOGGER.severe(getException().getMessage());
            connectedProperty().set(false);
        }

        @Override
        protected void cancelled() {
            statusMessagesProperty().set("Connecting cancelled.");
            connectedProperty().set(false);
        }

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    updateMessage("Connecting....");
                    String url = "tcp://" + txtHostName.getText() + ":" + txtHostPort.getText();
                    
                    d_skynetConnection.connect(url, txtClientId.getText());
                    return null;
                }
            };
        }

    }

    private class DisconnectService extends Service<Void> {

        @Override
        protected void succeeded() {
            statusMessagesProperty().set("");
            connectedProperty().set(false);
        }

        @Override
        protected void cancelled() {
            statusMessagesProperty().set("Disconnecting cancelled.");
            connectedProperty().set(false);
        }

        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    updateMessage("Disconnecting....");
                    d_skynetConnection.disconnect();
                    return null;
                }
            };
        }

    }

	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionLost(String cause) {
		// Use Platform.runLater to invoke this on the UI thread
		Platform.runLater(new Runnable() {
			public void run() {
				statusMessagesProperty().set("Connection Lost: " + cause);
				connectedProperty().set(false);
			}
		});
	}

	@Override
	public void onRobotDigitalInputChanged(int channel, boolean value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRobotAnalogInputChanged(int channel, double value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRobotStatusMessage(String statusType, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRobotGeneralMessage(String message) {
		// TODO Auto-generated method stub
		
	}
}
