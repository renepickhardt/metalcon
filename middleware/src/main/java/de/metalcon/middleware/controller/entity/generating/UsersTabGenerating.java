package de.metalcon.middleware.controller.entity.generating;

import de.metalcon.middleware.controller.entity.generator.UsersTabGenerator;

public interface UsersTabGenerating extends EntityTabGenerating {

    UsersTabGenerator getUsersTabGenerator();

}
