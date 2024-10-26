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
import com.terraforged.noise.util.NoiseUtil;

public class VariableCurve extends Modifier {

    private final Module midpoint;
    private final Module gradient;

    public VariableCurve(Module source, Module mid, Module gradient) {
        super(source);
        this.midpoint = mid;
        this.gradient = gradient;
    }

    @Override
    public String getSpecName() {
        return "VariCurve";
    }

    @Override
    public float modify(int seed, float x, float y, float noiseValue) {
        float mid = midpoint.getValue(seed, x, y);
        float curve = gradient.getValue(seed, x, y);
        return NoiseUtil.curve(noiseValue, mid, curve);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        VariableCurve that = (VariableCurve) o;

        if (!midpoint.equals(that.midpoint)) return false;
        return gradient.equals(that.gradient);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + midpoint.hashCode();
        result = 31 * result + gradient.hashCode();
        return result;
    }

    private static final DataFactory<VariableCurve> factory = (data, spec, context) -> new VariableCurve(
            spec.get("source", data, Module.class, context),
            spec.get("midpoint", data, Module.class, context),
            spec.get("gradient", data, Module.class, context)
    );

    public static DataSpec<VariableCurve> spec() {
        return Modifier.sourceBuilder("VariCurve", VariableCurve.class, factory)
                .addObj("midpoint", Module.class, v -> v.midpoint)
                .addObj("gradient", Module.class, v -> v.gradient)
                .build();
    }
}
