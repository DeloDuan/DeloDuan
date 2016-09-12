package com.glextend.vrlib.strategy.projection;

import com.glextend.vrlib.model.GLPosition;
import com.glextend.vrlib.objects.GLAbsObject3D;

/**
 * Created by  on 16/6/25.
 */
public interface IProjectionMode {
    GLAbsObject3D getObject3D();
    GLPosition getModelPosition();
}
