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

import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataObject;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.func.CellFunc;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.func.Interpolation;
import com.terraforged.noise.util.NoiseSpec;

import java.util.function.Function;

public abstract class NoiseSource implements Module {

    protected final int seed;
    protected final int octaves;
    protected final float gain;
    protected final float frequency;
    protected final float lacunarity;
    protected final Interpolation interpolation;

    public NoiseSource(Builder builder) {
        this.seed = builder.getSeed();
        this.octaves = builder.getOctaves();
        this.lacunarity = builder.getLacunarity();
        this.gain = builder.getGain();
        this.frequency = builder.getFrequency();
        this.interpolation = builder.getInterp();
    }

    @Override
    public float getValue(int seed, float x, float y) {
        return getSourceValue(this.seed + seed, x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoiseSource that = (NoiseSource) o;

        if (seed != that.seed) return false;
        if (octaves != that.octaves) return false;
        if (Float.compare(that.gain, gain) != 0) return false;
        if (Float.compare(that.frequency, frequency) != 0) return false;
        if (Float.compare(that.lacunarity, lacunarity) != 0) return false;
        return interpolation == that.interpolation;
    }

    @Override
    public int hashCode() {
        int result = seed;
        result = 31 * result + octaves;
        result = 31 * result + (gain != 0.0f ? Float.floatToIntBits(gain) : 0);
        result = 31 * result + (frequency != 0.0f ? Float.floatToIntBits(frequency) : 0);
        result = 31 * result + (lacunarity != 0.0f ? Float.floatToIntBits(lacunarity) : 0);
        result = 31 * result + interpolation.hashCode();
        return result;
    }

    public abstract float getSourceValue(int seed, float x, float y);

    public static Builder readData(DataObject data, DataSpec<?> spec, Context context) {
        Builder builder = new Builder();
        builder.seed(NoiseSpec.seed(data, spec, context));
        builder.gain(spec.get("gain", data, DataValue::asDouble));
        builder.octaves(spec.get("octaves", data, DataValue::asInt));
        builder.frequency(spec.get("frequency", data, DataValue::asDouble));
        builder.lacunarity(spec.get("lacunarity", data, DataValue::asDouble));
        builder.interp(spec.get("interpolation", data, v -> Interpolation.valueOf(v.asString())));
        if (data.has("cell_func")) {
            builder.cellFunc(spec.getEnum("cell_func", data, CellFunc.class));
        }
        if (data.has("edge_func")) {
            builder.edgeFunc(spec.getEnum("edge_func", data, EdgeFunc.class));
        }
        if (data.has("dist_func")) {
            builder.distFunc(spec.getEnum("dist_func", data, DistanceFunc.class));
        }
        if (data.has("source")) {
            builder.source(spec.get("source", data, Module.class, context));
        }
        return builder;
    }

    private static <S extends NoiseSource> DataFactory<S> constructor(Function<Builder, S> constructor) {
        return (data, spec, context) -> constructor.apply(readData(data, spec, context));
    }

    public static <S extends NoiseSource> DataSpec.Builder<S> specBuilder(String name, Class<S> type, Function<Builder, S> constructor) {
        return specBuilder(name, type, constructor(constructor));
    }

    public static <S extends NoiseSource> DataSpec.Builder<S> specBuilder(String name, Class<S> type, DataFactory<S> constructor) {
        return DataSpec.builder(name, type, constructor)
                .add("seed", Builder.DEFAULT_SEED, NoiseSpec.seed(f -> f.seed))
                .add("gain", Builder.DEFAULT_GAIN, f -> f.gain)
                .add("octaves", Builder.DEFAULT_OCTAVES, f -> f.octaves)
                .add("frequency", Builder.DEFAULT_FREQUENCY, f -> f.frequency)
                .add("lacunarity", Builder.DEFAULT_LACUNARITY, f -> f.lacunarity)
                .add("interpolation", Builder.DEFAULT_INTERPOLATION, f -> f.interpolation);
    }
}
