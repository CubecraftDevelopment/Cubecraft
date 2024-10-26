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
public class Select extends Selector {

    protected final Module control;
    protected final Module source0;
    protected final Module source1;

    protected final float lowerBound;
    protected final float upperBound;
    protected final float edgeFalloff;
    protected final float lowerCurveMin;
    protected final float lowerCurveMax;
    protected final float lowerCurveRange;
    protected final float upperCurveMin;
    protected final float upperCurveMax;
    protected final float upperCurveRange;

    public Select(Module control, Module source0, Module source1, float lowerBound, float upperBound, float edgeFalloff, Interpolation interpolation) {
        super(control, new Module[]{source0, source1}, interpolation);
        this.control = control;
        this.source0 = source0;
        this.source1 = source1;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.edgeFalloff = edgeFalloff;
        this.lowerCurveMin = lowerBound - edgeFalloff;
        this.lowerCurveMax = lowerBound + edgeFalloff;
        this.lowerCurveRange = lowerCurveMax - lowerCurveMin;
        this.upperCurveMin = upperBound - edgeFalloff;
        this.upperCurveMax = upperBound + edgeFalloff;
        this.upperCurveRange = upperCurveMax - upperCurveMin;
    }

    @Override
    public String getSpecName() {
        return "Select";
    }

    @Override
    public float selectValue(int seed, float x, float y, float value) {
        if (edgeFalloff == 0) {
            if (value < lowerCurveMax) {
                return source0.getValue(seed, x, y);
            }

            if (value > upperCurveMin) {
                return source1.getValue(seed, x, y);
            }

            return source0.getValue(seed, x, y);
        }

        if (value < lowerCurveMin) {
            return source0.getValue(seed, x, y);
        }

        // curve
        if (value < lowerCurveMax) {
            float alpha = (value - lowerCurveMin) / lowerCurveRange;
            return blendValues(source0.getValue(seed, x, y), source1.getValue(seed, x, y), alpha);
        }

        if (value < upperCurveMin) {
            return source1.getValue(seed, x, y);
        }

        if (value < upperCurveMax) {
            float alpha = (value - upperCurveMin) / upperCurveRange;
            return blendValues(source1.getValue(seed, x, y), source0.getValue(seed, x, y), alpha);
        }

        return source0.getValue(seed, x, y);
    }

    private static final DataFactory<Select> factory = (data, spec, context) -> new Select(
            spec.get("control", data, Module.class, context),
            spec.get("lower", data, Module.class, context),
            spec.get("upper", data, Module.class, context),
            spec.get("lower_bound", data, DataValue::asFloat),
            spec.get("upper_bound", data, DataValue::asFloat),
            spec.get("falloff", data, DataValue::asFloat),
            spec.get("interp", data, v -> v.asEnum(Interpolation.class))
    );

    public static DataSpec<Select> spec() {
        return DataSpec.builder(Select.class, factory)
                .add("lower_bound", 0, s -> s.lowerBound)
                .add("upper_bound", 1, s -> s.upperBound)
                .add("falloff", 0, s -> s.edgeFalloff)
                .add("interp", Interpolation.LINEAR, s -> s.interpolation)
                .addObj("control", Module.class, s -> s.selector)
                .addObj("lower", Module.class, s -> s.source0)
                .addObj("upper", Module.class, s -> s.source1)
                .build();
    }
}
