/*
 * PracticeGameState.java
 *
 * Created: Oct 5, 2005
 * 
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 */
/**
 * 
 */
package com.shapewriter.android.softkeyboard.game;

import java.io.Serializable;

class SWI_PracticeGameState implements Serializable {
	
	int numBalloonsHit;
	int numWordsEntered;
	
	SWI_PracticeGameState() {
	}
	
	int getCurrentScore() {
		return numBalloonsHit;
	}

	void setCurrentScore(int numBalloonsHit) {
		this.numBalloonsHit = numBalloonsHit;
	}

	int getNumWordsEntered() {
		return numWordsEntered;
	}

	void setNumWordsEntered(int numWordsEntered) {
		this.numWordsEntered = numWordsEntered;
	}

}