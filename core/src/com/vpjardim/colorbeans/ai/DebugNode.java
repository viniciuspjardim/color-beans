package com.vpjardim.colorbeans.ai;

import com.badlogic.gdx.utils.Array;

/** #debugCode */
public interface DebugNode {
    int getMove();

    String[] getText();

    Array<? extends DebugNode> getChildren();

    AiMap getAiMap();
}
