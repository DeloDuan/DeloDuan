package com.glextend.vrlib.strategy.interactive;

import com.glextend.vrlib.GL360Director;
import com.glextend.vrlib.strategy.IModeStrategy;

import java.util.List;

/**
 * Created by  on 16/3/19.
 */
public abstract class AbsInteractiveStrategy implements IModeStrategy, IInteractiveMode {

    private InteractiveModeManager.Params params;

    public AbsInteractiveStrategy(InteractiveModeManager.Params params) {
        this.params = params;
    }

    public InteractiveModeManager.Params getParams() {
        return params;
    }

    protected List<GL360Director> getDirectorList() {
        return params.projectionModeManager.getDirectors();
    }
}
