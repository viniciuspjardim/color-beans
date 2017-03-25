/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.ai;

import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.core.Cfg;
import com.vpjardim.colorbeans.input.InputBase;

/**
 * @author Vinícius Jardim
 * 27/04/2016
 */
public interface AiBase {

    void init(Map map, Cfg.Ai cfg);
    void update();
    InputBase getInput();
}
