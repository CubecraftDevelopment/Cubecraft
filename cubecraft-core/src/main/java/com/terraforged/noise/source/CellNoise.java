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
import com.terraforged.noise.Module;
import com.terraforged.noise.func.CellFunc;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.util.Noise;

public class CellNoise extends NoiseSource {

    private final Module lookup;
    private final CellFunc cellFunc;
    private final DistanceFunc distFunc;
    private final float min;
    private final float max;
    private final float range;
    private final float distance;

    public CellNoise(Builder builder) {
        super(builder);
        lookup = builder.getSource();
        cellFunc = builder.getCellFunc();
        distFunc = builder.getDistFunc();
        distance = builder.getDisplacement();
        min = min(cellFunc, lookup);
        max = max(cellFunc, lookup);
        range = max - min;
    }

    @Override
    public String getSpecName() {
        return "Cell";
    }

    @Override
    public float getSourceValue(int seed, float x, float y) {
        x *= frequency;
        y *= frequency;
        float value = Noise.cell(x, y, seed, distance, cellFunc, distFunc, lookup);
        return cellFunc.mapValue(value, min, max, range);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CellNoise cellNoise = (CellNoise) o;

        if (Float.compare(cellNoise.min, min) != 0) return false;
        if (Float.compare(cellNoise.max, max) != 0) return false;
        if (Float.compare(cellNoise.range, range) != 0) return false;
        if (Float.compare(cellNoise.distance, distance) != 0) return false;
        if (!lookup.equals(cellNoise.lookup)) return false;
        if (cellFunc != cellNoise.cellFunc) return false;
        return distFunc == cellNoise.distFunc;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + lookup.hashCode();
        result = 31 * result + cellFunc.hashCode();
        result = 31 * result + distFunc.hashCode();
        result = 31 * result + (min != 0.0f ? Float.floatToIntBits(min) : 0);
        result = 31 * result + (max != 0.0f ? Float.floatToIntBits(max) : 0);
        result = 31 * result + (range != 0.0f ? Float.floatToIntBits(range) : 0);
        result = 31 * result + (distance != 0.0f ? Float.floatToIntBits(distance) : 0);
        return result;
    }

    static float min(CellFunc func, Module lookup) {
        if (func == CellFunc.NOISE_LOOKUP) {
            return lookup.minValue();
        }
        if (func == CellFunc.DISTANCE) {
            return -1;
        }
        return -1;
    }

    static float max(CellFunc func, Module lookup) {
        if (func == CellFunc.NOISE_LOOKUP) {
            return lookup.maxValue();
        }
        if (func == CellFunc.DISTANCE) {
            return 0.25F;
        }
        return 1;
    }

    public static DataSpec<CellNoise> spec() {
        return specBuilder("Cell", CellNoise.class, CellNoise::new)
                .add("distance", Builder.DEFAULT_DISTANCE, f -> f.distance)
                .add("cell_func", Builder.DEFAULT_CELL_FUNC, f -> f.cellFunc)
                .add("dist_func", Builder.DEFAULT_DIST_FUNC, f -> f.distFunc)
                .addObj("source", Module.class, f -> f.lookup)
                .build();
    }
}
