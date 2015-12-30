package edu.wpi.first.wpilibj.livewindow;

import edu.wpi.first.wpilibj.Sendable;

public interface LiveWindowSendable extends Sendable {
	public void updateTable();
	public void startLiveWindowMode();
	public void stopLiveWindowMode();
}
