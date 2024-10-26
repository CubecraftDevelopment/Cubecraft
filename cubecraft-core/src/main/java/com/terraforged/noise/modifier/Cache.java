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

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;

/**
 * @author dags <dags@dags.me>
 */
public class Cache extends Modifier {

    private final ThreadLocal<Value> localValue = ThreadLocal.withInitial(Value::new);

    public Cache(Module source) {
        super(source);
    }

    @Override
    public String getSpecName() {
        return "Cache";
    }

    @Override
    public float minValue() {
        return source.minValue();
    }

    @Override
    public float maxValue() {
        return source.maxValue();
    }

    @Override
    public float modify(int seed, float x, float y, float noiseValue) {
        return 0;
    }

    @Override
    public float getValue(int seed, float x, float y) {
        Value value = this.localValue.get();
        if (value.matches(x, y)) {
            return value.value;
        }
        return value.set(x, y, source.getValue(seed, x, y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Cache cache = (Cache) o;

        return localValue.equals(cache.localValue);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + localValue.hashCode();
        return result;
    }

    private static class Value {

        private float x = 0;
        private float y = 0;
        private float value = 0;
        private boolean empty = true;

        private boolean matches(float x, float y) {
            return !empty && x == this.x && y == this.y;
        }

        private float set(float x, float y, float value) {
            this.x = x;
            this.y = y;
            this.value = value;
            this.empty = false;
            return value;
        }
    }

    public static DataSpec<Cache> spec() {
        return spec(Cache.class, Cache::new);
    }
}
