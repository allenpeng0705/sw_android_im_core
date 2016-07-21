/* BalloonSceneListener.java
 * Created on Jun 5, 2005
 *
 * Copyright (c) 2005 ShapeWriter Inc.
 * Per-Ola Kristensson <pkristen@us.ibm.com>
 * 
 * @author Per-Ola Kristensson
 */
package com.shapewriter.android.softkeyboard.game;

/**
 * 
 * @author pkriste
 */
public interface SWI_PracticeBalloonSceneListener {

	void balloonHit(SWI_EriItem eriItem);

	void missedWord(SWI_EriItem eriItem);

	void outScreen(SWI_EriItem eriItem);

}
