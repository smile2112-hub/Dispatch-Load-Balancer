package com.load.balance;

import com.load.balance.service.DistanceCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistanceCalculatorTest {
    @Test
    void returnsZeroForTheSameCoordinates() {
        assertEquals(0.0, DistanceCalculator.haversine(12.9716, 77.5946, 12.9716, 77.5946), 0.000001);
    }

    @Test
    void calculatesOneDegreeAlongTheEquator() {
        assertEquals(111.195, DistanceCalculator.haversine(0, 0, 0, 1), 0.01);
    }

    @Test
    void handlesCoordinatesAcrossTheInternationalDateLine() {
        assertEquals(22.239, DistanceCalculator.haversine(0, 179.9, 0, -179.9), 0.001);
    }
}