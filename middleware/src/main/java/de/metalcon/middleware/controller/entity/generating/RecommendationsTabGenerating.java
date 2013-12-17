package de.metalcon.middleware.controller.entity.generating;

import de.metalcon.middleware.controller.entity.generator.RecommendationsTabGenerator;

public interface RecommendationsTabGenerating extends EntityTabGenerating {

    RecommendationsTabGenerator getRecommendationsTabGenerator();

}
