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

/**
 * @author dags <dags@dags.me>
 */
public class Power extends Modifier {

    private final Module n;

    public Power(Module source, Module n) {
        super(source);
        this.n = n;
    }

    @Override
    public String getSpecName() {
        return "Pow";
    }

    @Override
    public float modify(int seed, float x, float y, float noiseValue) {
        return NoiseUtil.pow(noiseValue, n.getValue(seed, x, y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Power power = (Power) o;

        return n.equals(power.n);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + n.hashCode();
        return result;
    }

    public static final DataFactory<Power> factory = (data, spec, context) -> new Power(
            spec.get("source", data, Module.class, context),
            spec.get("power", data, Module.class, context)
    );

    public static DataSpec<Power> spec() {
        return sourceBuilder("Pow", Power.class, factory)
                .addObj("power", Module.class, p -> p.n)
                .build();
    }
}
