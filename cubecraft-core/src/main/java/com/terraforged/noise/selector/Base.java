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

package com.terraforged.noise.selector;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.func.Interpolation;

/**
 * @author dags <dags@dags.me>
 */
public class Base extends Selector {

    private final Module base;
    protected final float min;
    protected final float max;
    protected final float maxValue;
    protected final float falloff;

    public Base(Module base, Module source, float falloff, Interpolation interpolation) {
        super(source, new Module[]{base, source}, interpolation);
        this.base = base;
        this.min = base.maxValue();
        this.max = base.maxValue() + falloff;
        this.falloff = falloff;
        this.maxValue = Math.max(base.maxValue(), source.maxValue());
    }

    @Override
    public String getSpecName() {
        return "Base";
    }

    @Override
    protected float selectValue(int seed, float x, float y, float upperValue) {
        if (upperValue < max) {
            float lowerValue = base.getValue(seed, x, y);
            if (falloff > 0) {
                float clamp = Math.max(min, upperValue);
                float alpha = (max - clamp) / falloff;
                return blendValues(upperValue, lowerValue, alpha);
            }
            return lowerValue;
        }
        return upperValue;
    }

    @Override
    public float minValue() {
        return base.minValue();
    }

    @Override
    public float maxValue() {
        return maxValue;
    }

    private static final DataFactory<Base> factory = (data, spec, context) -> new Base(
            spec.get("base", data, Module.class, context),
            spec.get("control", data, Module.class, context),
            spec.get("falloff", data, DataValue::asFloat),
            spec.get("interpolation", data, v -> v.asEnum(Interpolation.class))
    );

    public static DataSpec<Base> spec() {
        return DataSpec.builder("Base", Base.class, factory)
                .add("falloff", 0, b -> b.falloff)
                .add("interpolation", Interpolation.LINEAR, b -> b.interpolation)
                .addObj("control", Module.class,b -> b.selector)
                .addObj("base", Module.class,b -> b.base)
                .build();
    }
}
