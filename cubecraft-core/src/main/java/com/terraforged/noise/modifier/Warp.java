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
import com.terraforged.noise.domain.Domain;

/**
 * @author dags <dags@dags.me>
 */
public class Warp extends Modifier {

    private final Domain domain;

    public Warp(Module source, Domain domain) {
        super(source);
        this.domain = domain;
    }

    @Override
    public String getSpecName() {
        return "Warp";
    }

    @Override
    public float getValue(int seed, float x, float y) {
        return source.getValue(seed, domain.getX(seed, x, y), domain.getY(seed, x, y));
    }

    @Override
    public float modify(int seed, float x, float y, float noiseValue) {
        // not used
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Warp warp = (Warp) o;

        return domain.equals(warp.domain);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + domain.hashCode();
        return result;
    }

    private static final DataFactory<Warp> factory = (data, spec, context) -> new Warp(
            spec.get("source", data, Module.class, context),
            spec.get("domain", data, Domain.class, context)
    );

    public static DataSpec<Warp> spec() {
        return Modifier.sourceBuilder(Warp.class, factory)
                .addObj("domain", Domain.class, m -> m.domain)
                .build();
    }
}
