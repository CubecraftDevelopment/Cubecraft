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
public class VariableBlend extends Selector {

    private final Module source0;
    private final Module source1;
    private final Module variator;
    private final float midpoint;
    private final float maxBlend;
    private final float minBlend;

    public VariableBlend(Module control, Module variator, Module source0, Module source1, float midpoint, float minBlend, float maxBlend, Interpolation interpolation) {
        super(control, new Module[]{source0, source1}, interpolation);
        this.source0 = source0;
        this.source1 = source1;
        this.midpoint = midpoint;
        this.maxBlend = maxBlend;
        this.minBlend = minBlend;
        this.variator = variator;
    }

    @Override
    public String getSpecName() {
        return "VariBlend";
    }

    @Override
    protected float selectValue(int seed, float x, float y, float selector) {
        float radius = minBlend + variator.getValue(seed, x, y) * maxBlend;

        float min = Math.max(0, midpoint - radius);
        if (selector < min) {
            return source0.getValue(seed, x, y);
        }

        float max = Math.min(1, midpoint + radius);
        if (selector > max) {
            return source1.getValue(seed, x, y);
        }

        float alpha = (selector - min) / (max - min);
        return blendValues(source0.getValue(seed, x, y), source1.getValue(seed, x, y), alpha);
    }

    private static final DataFactory<VariableBlend> factory = (data, spec, context) -> new VariableBlend(
            spec.get("control", data, Module.class, context),
            spec.get("variator", data, Module.class, context),
            spec.get("lower", data, Module.class, context),
            spec.get("upper", data, Module.class, context),
            spec.get("midpoint", data, DataValue::asFloat),
            spec.get("blend_min", data, DataValue::asFloat),
            spec.get("blend_max", data, DataValue::asFloat),
            spec.get("interp", data, v -> v.asEnum(Interpolation.class))
    );

    public static DataSpec<VariableBlend> spec() {
        return DataSpec.builder("VariBlend", VariableBlend.class, factory)
                .add("midpoint", 0.5F, v -> v.midpoint)
                .add("blend_min", 0F, v -> v.minBlend)
                .add("blend_max", 1F, v -> v.maxBlend)
                .add("interp", Interpolation.LINEAR, v -> v.interpolation)
                .addObj("control", Module.class, v -> v.selector)
                .addObj("variator", Module.class, v -> v.variator)
                .addObj("lower", Module.class, v -> v.source0)
                .addObj("upper", Module.class, v -> v.source1)
                .build();
    }
}
