package de.metalcon.middleware.controller.entity.generating;

import de.metalcon.middleware.controller.entity.generator.EventsTabGenerator;

public interface EventsTabGenerating extends EntityTabGenerating {

    EventsTabGenerator getEventsTabGenerator();

}
