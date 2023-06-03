package de.umr.tsquare.dataintegration.batch;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ApplicationConfig {

    @Value("${chunk.size}")
    private int chunkSize;

    @Value("${integration.distance.threshold.meters}")
    private int thresholdMeters;

    @Value("${integration.equality.threshold.meters}")
    private int equalityThresholdMeters;

    @Value("${integration.equality.threshold.levenshtein}")
    private int equalityLevenshteinDistanceThreshold;

}
