package de.metalcon.middleware.controller.entity.generating;

import de.metalcon.middleware.controller.entity.generator.NewsfeedTabGenerator;

public interface NewsfeedTabGenerating extends EntityTabGenerating {

    NewsfeedTabGenerator getNewsfeedTabGenerator();

}
