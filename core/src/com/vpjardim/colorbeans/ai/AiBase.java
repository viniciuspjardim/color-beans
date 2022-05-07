/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.ai;

import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.core.Cfg;
import com.vpjardim.colorbeans.input.InputBase;

/**
 * @author Vinícius Jardim
 *         2016/04/27
 */
public interface AiBase {

    void init(Map map, Cfg.Ai cfg);

    void update();

    InputBase getInput();
}
