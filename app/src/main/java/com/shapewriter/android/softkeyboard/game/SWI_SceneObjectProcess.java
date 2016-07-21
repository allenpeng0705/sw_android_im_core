/* ProcessSceneObject.java
 * Created on Jun 3, 2005
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
interface SWI_SceneObjectProcess extends SWI_SceneObject {
	
	boolean isDone();
	
	void nextStep();

}
