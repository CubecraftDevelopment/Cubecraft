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

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

public class Sin extends NoiseSource {

    private final Module alpha;

    public Sin(Builder builder) {
        super(builder);
        alpha = builder.getSource();
    }

    @Override
    public String getSpecName() {
        return "Sin";
    }

    @Override
    public float getSourceValue(int seed, float x, float y) {
        float a = alpha.getValue(seed, x, y);
        x *= frequency;
        y *= frequency;

        final float noise;
        if (a == 0) {
            noise = NoiseUtil.sin(x);
        } else if (a == 1) {
            noise =NoiseUtil.sin(y);
        } else {
            float sx = NoiseUtil.sin(x);
            float sy = NoiseUtil.sin(y);
            noise = NoiseUtil.lerp(sx, sy, a);
        }

        return NoiseUtil.map(noise, -1, 1, 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Sin sin = (Sin) o;

        return alpha.equals(sin.alpha);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + alpha.hashCode();
        return result;
    }

    private static final DataFactory<Sin> factory = (data, spec, context) -> new Sin(
            new Builder().frequency(spec.get("frequency", data, DataValue::asDouble)).source(spec.get("alpha", data, Module.class, context))
    );

    public static DataSpec<Sin> spec() {
        return DataSpec.builder("Sin", Sin.class, factory)
                .add("frequency", 1F, s -> s.frequency)
                .addObj("alpha", s -> s.alpha)
                .build();
    }
}
