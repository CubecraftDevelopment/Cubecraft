/*
 *
 * MIT License
 *
 * Copyright (c) 2020 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.func.Interpolation;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseUtil;

import java.util.Arrays;

public class RidgeNoise extends NoiseSource {

    private static final int RIDGED_MAX_OCTAVE = 30;

    private final Interpolation interpolation;
    private final float[] spectralWeights;
    private final float min;
    private final float max;
    private final float range;

    public RidgeNoise(Builder builder) {
        super(builder);
        int octaves = Math.min(RIDGED_MAX_OCTAVE, builder.getOctaves());

        this.interpolation = builder.getInterp();
        this.spectralWeights = new float[octaves];

        float h = 1.0F;
        float frequency = 1.0F;
        for (int i = 0; i < octaves; i++) {
            spectralWeights[i] = NoiseUtil.pow(frequency, -h);
            frequency *= lacunarity;
        }

        min = 0;
        max = calculateMaxBound(builder.getOctaves(), builder.getGain());
        range = Math.abs(max - min);
    }

    @Override
    public String getSpecName() {
        return "Ridge";
    }

    @Override
    public float getSourceValue(int seed, float x, float y) {
        x *= frequency;
        y *= frequency;

        float amp = 2.0F;
        float value = 0.0F;
        float weight = 1.0F;

        for (int octave = 0; octave < octaves; octave++) {
            float signal = Noise.singlePerlin(x, y, seed + octave, interpolation);
            signal = 1.0f - Math.abs(signal);
            signal *= signal;

            signal *= weight;
            weight = signal * amp;
            weight = NoiseUtil.clamp(weight, 0, 1);
            value += (signal * spectralWeights[octave]);

            x *= lacunarity;
            y *= lacunarity;
            amp *= gain;
        }
        return NoiseUtil.map(value, min, max, range);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RidgeNoise that)) return false;
        if (!super.equals(o)) return false;

        if (Float.compare(that.min, min) != 0) return false;
        if (Float.compare(that.max, max) != 0) return false;
        if (Float.compare(that.range, range) != 0) return false;
        if (interpolation != that.interpolation) return false;
        return Arrays.equals(spectralWeights, that.spectralWeights);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + interpolation.hashCode();
        result = 31 * result + Arrays.hashCode(spectralWeights);
        result = 31 * result + (min != 0.0f ? Float.floatToIntBits(min) : 0);
        result = 31 * result + (max != 0.0f ? Float.floatToIntBits(max) : 0);
        result = 31 * result + (range != 0.0f ? Float.floatToIntBits(range) : 0);
        return result;
    }

    private float calculateMaxBound(int octaves, float gain) {
        float amp = 2.0F;
        float value = 0.0F;
        float weight = 1.0F;

        for (int curOctave = 0; curOctave < octaves; curOctave++) {
            float noise = 1.0f;
            noise *= weight;
            weight = noise * amp;
            weight = Math.min(1F, Math.max(0F, weight));
            value += (noise * spectralWeights[curOctave]);
            amp *= gain;
        }

        return value;
    }

    public static DataSpec<RidgeNoise> ridgeSpec() {
        return specBuilder("Ridge", RidgeNoise.class, RidgeNoise::new).build();
    }
}