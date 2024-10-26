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

public class Threshold extends Modifier {

    private final Module threshold;

    public Threshold(Module source, Module threshold) {
        super(source);
        this.threshold = threshold;
    }

    @Override
    public String getSpecName() {
        return "Threshold";
    }

    @Override
    public float modify(int seed, float x, float y, float noiseValue) {
        float limit = threshold.getValue(seed, x, y);
        if (noiseValue < limit) {
            return 0F;
        }
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Threshold threshold1 = (Threshold) o;

        return threshold.equals(threshold1.threshold);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + threshold.hashCode();
        return result;
    }

    private static final DataFactory<Threshold> factory = (data, spec, context) -> new Threshold(
            spec.get("source", data, Module.class, context),
            spec.get("threshold", data, Module.class, context)
    );

    public static DataSpec<Threshold> spec() {
        return Modifier.sourceBuilder(Threshold.class, factory)
                .addObj("threshold", Module.class, t -> t.threshold)
                .build();
    }
}
