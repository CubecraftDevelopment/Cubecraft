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
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

public class Boost extends Modifier {

    private final int iterations;

    public Boost(Module source, int iterations) {
        super(source.map(0, 1));
        this.iterations = Math.max(1, iterations);
    }

    @Override
    public String getSpecName() {
        return "Boost";
    }

    @Override
    public float modify(int seed, float x, float y, float value) {
        for (int i = 0; i < iterations; i++) {
            value = NoiseUtil.pow(value, 1 - value);
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Boost boost = (Boost) o;

        return iterations == boost.iterations;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + iterations;
        return result;
    }

    private static final DataFactory<Boost> factory = (data, spec, context) -> new Boost(
            spec.get("source", data, Module.class, context),
            spec.get("iterations", data, DataValue::asInt)
    );

    public static DataSpec<Boost> spec() {
        return Modifier.specBuilder(Boost.class, factory)
                .add("iterations", 1, b -> b.iterations)
                .addObj("source", Module.class,  b -> b.source)
                .build();
    }
}
