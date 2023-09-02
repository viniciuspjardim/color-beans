package com.vpjardim.colorbeans.ai;

import com.vpjardim.colorbeans.Map;
import com.vpjardim.colorbeans.core.Cfg;
import com.vpjardim.colorbeans.input.InputBase;

public interface AiBase {
    void init(Map map, Cfg.Ai cfg);

    void update();

    InputBase getInput();
}
