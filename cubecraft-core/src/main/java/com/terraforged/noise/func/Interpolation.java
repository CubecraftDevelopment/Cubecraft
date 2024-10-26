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

package com.terraforged.noise.func;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.util.NoiseUtil;

import java.util.function.Function;

/**
 * https://github.com/Auburns/FastNoise_Java
 */
public enum Interpolation implements CurveFunc {
    LINEAR {
        @Override
        public float apply(float f) {
            return f;
        }
    },
    CURVE3 {
        @Override
        public float apply(float f) {
            return NoiseUtil.interpHermite(f);
        }
    },
    CURVE4 {
        @Override
        public float apply(float f) {
            return NoiseUtil.interpQuintic(f);
        }
    },;

    @Override
    public String getSpecName() {
        return "Interpolation";
    }

    public abstract float apply(float f);

    private static final DataFactory<Interpolation> factory = (data, spec, context) -> spec.getEnum("mode", data, Interpolation.class);

    public static DataSpec<Interpolation> spec() {
        return DataSpec.builder("Interpolation", Interpolation.class, factory)
                .add("mode", Interpolation.LINEAR, Function.identity())
                .build();
    }
}
