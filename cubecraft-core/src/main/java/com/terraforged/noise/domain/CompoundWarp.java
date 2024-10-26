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

package com.terraforged.noise.domain;

import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataObject;

public class CompoundWarp implements Domain {

    private final Domain a;
    private final Domain b;

    public CompoundWarp(Domain a, Domain b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String getSpecName() {
        return "CompoundWarp";
    }

    @Override
    public float getOffsetX(int seed, float x, float y) {
        float ax = a.getX(seed, x, y);
        float ay = a.getY(seed, x, y);
        return b.getOffsetX(seed, ax, ay);
    }

    @Override
    public float getOffsetY(int seed, float x, float y) {
        float ax = a.getX(seed, x, y);
        float ay = a.getY(seed, x, y);
        return b.getOffsetY(seed, ax, ay);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompoundWarp that = (CompoundWarp) o;

        if (!a.equals(that.a)) return false;
        return b.equals(that.b);
    }

    @Override
    public int hashCode() {
        int result = a.hashCode();
        result = 31 * result + b.hashCode();
        return result;
    }

    private static CompoundWarp create(DataObject data, DataSpec<?> spec, Context context) {
        return new CompoundWarp(
                spec.get("warp_1", data, Domain.class, context),
                spec.get("warp_2", data, Domain.class, context)
        );
    }

    public static DataSpec<? extends Domain> spec() {
        return DataSpec.builder("CumulativeWarp", CompoundWarp.class, CompoundWarp::create)
                .addObj("warp_1", Domain.class, w -> w.a)
                .addObj("warp_2", Domain.class, w -> w.b)
                .build();
    }
}
