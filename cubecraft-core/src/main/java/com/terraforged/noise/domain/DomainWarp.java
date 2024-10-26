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
import com.terraforged.noise.Module;

public class DomainWarp implements Domain {

    private final Module x;
    private final Module y;
    private final Module distance;

    public DomainWarp(Module x, Module y, Module distance) {
        this.x = map(x);
        this.y = map(y);
        this.distance = distance;
    }

    @Override
    public String getSpecName() {
        return "DomainWarp";
    }

    @Override
    public float getOffsetX(int seed, float x, float y) {
        return this.x.getValue(seed, x, y) * this.distance.getValue(seed, x, y);
    }

    @Override
    public float getOffsetY(int seed, float x, float y) {
        return this.y.getValue(seed, x, y) * this.distance.getValue(seed, x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomainWarp that = (DomainWarp) o;

        if (!x.equals(that.x)) return false;
        if (!y.equals(that.y)) return false;
        return distance.equals(that.distance);
    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        result = 31 * result + distance.hashCode();
        return result;
    }

    // map the module to the range -0.5 to 0.5
    // this ensures warping occurs in the positive and negative directions
    private static Module map(Module in) {
        if (in.minValue() == -0.5F && in.maxValue() == 0.5F) {
            return in;
        }

        // if range is not 1 then use map to squash/stretch it to correct range
        return in.map(-0.5, 0.5);
    }

    private static DomainWarp create(DataObject data, DataSpec<?> spec, Context context) {
        return new DomainWarp(
                spec.get("x", data, Module.class, context),
                spec.get("y", data, Module.class, context),
                spec.get("distance", data, Module.class, context)
        );
    }

    public static DataSpec<? extends Domain> spec() {
        return DataSpec.builder("DomainWarp", DomainWarp.class, DomainWarp::create)
                .addObj("x", Module.class, w -> w.x)
                .addObj("y", Module.class, w -> w.y)
                .addObj("distance", Module.class, w -> w.distance)
                .build();
    }
}
