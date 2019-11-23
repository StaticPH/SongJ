package com.StaticPH.MicroAud.audioPlayer;

import java.io.File;

// java.util.Collection is a decent conceptual example of an interface class

// Require that all classes implementing this interface define everything herein
public interface IAudioPlayer {
	//???: defining class fields(member variables) in interfaces? or abstract classes?
	void playFile(File file);
}
