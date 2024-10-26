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
import com.terraforged.noise.Source;

/**
 * @author dags <dags@dags.me>
 */
public class Bias extends Modifier {

    private final Module bias;

    public Bias(Module source, float bias) {
        this(source, Source.constant(bias));
    }

    public Bias(Module source, Module bias) {
        super(source);
        this.bias = bias;
    }

    @Override
    public String getSpecName() {
        return "Bias";
    }

    @Override
    public float minValue() {
        return super.minValue() + bias.minValue();
    }

    @Override
    public float maxValue() {
        return super.maxValue() + bias.maxValue();
    }

    @Override
    public float modify(int seed, float x, float y, float noiseValue) {
        return noiseValue + bias.getValue(seed, x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Bias bias1 = (Bias) o;

        return bias.equals(bias1.bias);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + bias.hashCode();
        return result;
    }

    private static final DataFactory<Bias> factory = (data, spec, context) -> new Bias(
            spec.get("source", data, Module.class, context),
            spec.get("bias", data, DataValue::asFloat)
    );

    public static DataSpec<Bias> spec() {
        return Modifier.sourceBuilder(Bias.class, factory)
                .addObj("bias", Module.class, b -> b.bias)
                .build();
    }
}
