/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.ai;

import com.badlogic.gdx.utils.Array;

/**
 * @author Vinícius Jardim
 * 10/08/2016
 */
public interface DebugNode {

    // #debugCode

    int getMove();

    String[] getText();

    Array<? extends DebugNode> getChildren();

    AiMap getAiMap();
}
