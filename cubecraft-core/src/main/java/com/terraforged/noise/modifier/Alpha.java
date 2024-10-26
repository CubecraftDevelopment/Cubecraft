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

public class Alpha extends Modifier {

    private final Module alpha;

    public Alpha(Module source, Module alpha) {
        super(source);
        this.alpha = alpha;
    }

    @Override
    public String getSpecName() {
        return "Alpha";
    }

    @Override
    public float modify(int seed, float x, float y, float noiseValue) {
        float a = alpha.getValue(seed, x, y);
        return (noiseValue * a) + (1 - a);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Alpha alpha1 = (Alpha) o;

        return alpha.equals(alpha1.alpha);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + alpha.hashCode();
        return result;
    }

    private static final DataFactory<Alpha> factory = (data, spec, context) -> new Alpha(
            spec.get("source", data, Module.class, context),
            spec.get("alpha", data, Module.class, context)
    );

    public static DataSpec<Alpha> spec() {
        return Modifier.sourceBuilder(Alpha.class, factory)
                .addObj("alpha", Module.class, a -> a.alpha)
                .build();
    }
}
