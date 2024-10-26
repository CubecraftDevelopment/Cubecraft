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

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;
import com.terraforged.noise.util.NoiseUtil;

/**
 * @author dags <dags@dags.me>
 */
public class Clamp extends Modifier {

    private final Module min;
    private final Module max;

    public Clamp(Module source, float min, float max) {
        this(source, Source.constant(min), Source.constant(max));
    }

    public Clamp(Module source, Module min, Module max) {
        super(source);
        this.min = min;
        this.max = max;
    }

    @Override
    public String getSpecName() {
        return "Clamp";
    }

    @Override
    public float minValue() {
        return min.minValue();
    }

    @Override
    public float maxValue() {
        return max.maxValue();
    }

    @Override
    public float modify(int seed, float x, float y, float noiseValue) {
        float min = this.min.getValue(seed, x, y);
        float max = this.max.getValue(seed, x, y);
        return NoiseUtil.clamp(noiseValue, min, max);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Clamp clamp = (Clamp) o;

        if (!min.equals(clamp.min)) return false;
        return max.equals(clamp.max);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + min.hashCode();
        result = 31 * result + max.hashCode();
        return result;
    }

    private static final DataFactory<Clamp> factory = (data, spec, context) -> new Clamp(
            spec.get("source", data, Module.class, context),
            spec.get("min", data, Module.class, context),
            spec.get("max", data, Module.class, context)
    );

    public static DataSpec<Clamp> spec() {
        return Modifier.sourceBuilder(Clamp.class, factory)
                .addObj("min", Module.class, c -> c.min)
                .addObj("max", Module.class, c -> c.max)
                .build();
    }
}
