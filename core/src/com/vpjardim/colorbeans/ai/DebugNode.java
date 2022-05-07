/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.ai;

import com.badlogic.gdx.utils.Array;

/**
 * @author Vinícius Jardim
 *         2016/08/10
 */
public interface DebugNode {

    // #debugCode

    int getMove();

    String[] getText();

    Array<? extends DebugNode> getChildren();

    AiMap getAiMap();
}
