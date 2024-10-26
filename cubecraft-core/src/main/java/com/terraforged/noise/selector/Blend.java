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
public class Blend extends Selector {

    protected final Module source0;
    protected final Module source1;
    protected final float blend;
    protected final float midpoint;
    protected final float blendLower;
    protected final float blendUpper;
    protected final float blendRange;

    public Blend(Module selector, Module source0, Module source1, float midPoint, float blendRange, Interpolation interpolation) {
        super(selector, new Module[]{source0, source1}, interpolation);
        float mid = selector.minValue() + ((selector.maxValue() - selector.minValue()) * midPoint);
        this.blend = blendRange;
        this.source0 = source0;
        this.source1 = source1;
        this.midpoint = midPoint;
        this.blendLower = Math.max(selector.minValue(), mid - (blendRange / 2F));
        this.blendUpper = Math.min(selector.maxValue(), mid + (blendRange / 2F));
        this.blendRange = blendUpper - blendLower;
    }

    @Override
    public String getSpecName() {
        return "Blend";
    }

    @Override
    public float selectValue(int seed, float x, float y, float select) {
        if (select < blendLower) {
            return source0.getValue(seed, x, y);
        }
        if (select > blendUpper) {
            return source1.getValue(seed, x, y);
        }
        float alpha = (select - blendLower) / blendRange;
        return blendValues(source0.getValue(seed, x, y), source1.getValue(seed, x, y), alpha);
    }

    private static final DataFactory<Blend> factory = (data, spec, context) -> new Blend(
            spec.get("control", data, Module.class, context),
            spec.get("lower", data, Module.class, context),
            spec.get("upper", data, Module.class, context),
            spec.get("midpoint", data, DataValue::asFloat),
            spec.get("blend_range", data, DataValue::asFloat),
            spec.getEnum("interpolation", data, Interpolation.class)
    );

    public static DataSpec<Blend> spec() {
        return DataSpec.builder(Blend.class, factory)
                .add("midpoint", 0.5F, b -> b.midpoint)
                .add("blend_range", 0F, b -> b.blend)
                .add("interpolation", Interpolation.LINEAR, b -> b.interpolation)
                .addObj("control", Module.class, b -> b.selector)
                .addObj("lower", Module.class, b -> b.source0)
                .addObj("upper", Module.class, b -> b.source1)
                .build();
    }
}
