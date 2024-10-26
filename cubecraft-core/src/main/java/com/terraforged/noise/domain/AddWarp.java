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

public class AddWarp implements Domain {

    private final Domain a;
    private final Domain b;

    public AddWarp(Domain a, Domain b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String getSpecName() {
        return "AddWarp";
    }

    @Override
    public float getOffsetX(int seed, float x, float y) {
        return a.getOffsetX(seed, x, y) + b.getOffsetX(seed, x, y);
    }

    @Override
    public float getOffsetY(int seed, float x, float y) {
        return a.getOffsetY(seed, x, y) + b.getOffsetY(seed, x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddWarp addWarp = (AddWarp) o;

        if (!a.equals(addWarp.a)) return false;
        return b.equals(addWarp.b);
    }

    @Override
    public int hashCode() {
        int result = a.hashCode();
        result = 31 * result + b.hashCode();
        return result;
    }

    private static AddWarp create(DataObject data, DataSpec<?> spec, Context context) {
        return new AddWarp(
                spec.get("warp_1", data, Domain.class, context),
                spec.get("warp_2", data, Domain.class, context)
        );
    }

    public static DataSpec<? extends Domain> spec() {
        return DataSpec.builder("CumulativeWarp", AddWarp.class, AddWarp::create)
                .addObj("warp_1", Domain.class, w -> w.a)
                .addObj("warp_2", Domain.class, w -> w.b)
                .build();
    }
}
