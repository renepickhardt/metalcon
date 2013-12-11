package de.metalcon.middleware.controller.entity;

import org.springframework.beans.factory.annotation.Autowired;

import de.metalcon.middleware.controller.MetalconController;
import de.metalcon.middleware.core.EntityUrlMapper;

public abstract class EntityController extends MetalconController {

    @Autowired
    protected EntityUrlMapper urlMapper;
    
}
