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

package com.terraforged.noise;

import com.terraforged.cereal.spec.SpecName;
import com.terraforged.noise.combiner.*;
import com.terraforged.noise.domain.Domain;
import com.terraforged.noise.func.CurveFunc;
import com.terraforged.noise.func.Interpolation;
import com.terraforged.noise.func.MidPointCurve;
import com.terraforged.noise.modifier.*;
import com.terraforged.noise.selector.*;
import com.terraforged.noise.source.NoiseSource;

/**
 * @author dags <dags@dags.me>
 */
public interface Module extends Noise, SpecName {

    @Override
    default String getSpecName() {
        return "";
    }

    /**
     * Create a Module who's output is the absolute value of this Module's output (ie negative values return positive)
     *
     * @return a new Abs Module
     */
    default Module abs() {
        if (this instanceof Abs) {
            return this;
        }
        return new Abs(this);
    }

    /**
     * Add the output of another Module to this Module's output
     *
     * @param other - the Module to add
     * @return a new Add Module
     */
    default Module add(Module other) {
        return new Add(this, other);
    }

    /**
     * A utility to Scale and Bias the output of this Module such that the output is scaled by the alpha amount
     * and biased by 1 - alpha
     *
     * @param alpha - the alpha value (expected to be within the range 0-1)
     * @return a new Alpha Module
     */
    default Module alpha(double alpha) {
        return alpha(Source.constant(alpha));
    }

    /**
     * A utility to Scale and Bias the output of this Module such that the output is scaled by the alpha value
     * and biased by 1 - alpha
     *
     * @param alpha - a Module who's output provides the alpha value
     * @return a new Alpha Module
     */
    default Module alpha(Module alpha) {
        if (alpha.minValue() < 0 || alpha.maxValue() > 1) {
            return this;
        }
        return new Alpha(this, alpha);
    }

    /**
     * Combines this and the other Module by blending their outputs only when the other Module's output
     * falls below the provided falloff value.
     *
     * This Module's output becomes more dominant the lower the other Module's output becomes.
     *
     * When the output is above the falloff value only the other Module's output is returned.
     *
     * @param other - the Module that this Module should form the 'base' for
     * @param falloff - the value below which the blending will occur
     * @return a new Base Module
     */
    default Module base(Module other, double falloff) {
        return base(other, falloff, Interpolation.CURVE3);
    }

    /**
     * Combines this and the other Module by blending their outputs only when the other Module's output
     * falls below the provided falloff value.
     *
     * This Module's output becomes more dominant the lower the other Module's output becomes.
     *
     * When the output is above the falloff value only the other Module's output is returned.
     *
     * @param other - the Module that this Module should form the 'base' for
     * @param falloff - the value below which the blending will occur
     * @param interpolation - the interpolation method to use while blending the outputs
     * @return a new Base Module
     */
    default Module base(Module other, double falloff, Interpolation interpolation) {
        return new Base(this, other, (float) falloff, interpolation);
    }

    /**
     * Modifies this Module's output by adding the bias Module's output to the returned value.
     *
     * @param bias - the Module that biases the output
     * @return a new Bias Module
     */
    default Module bias(Module bias) {
        if (bias.minValue() == 0 && bias.maxValue() == 0) {
            return this;
        }
        return new Bias(this, bias);
    }

    /**
     * Modifies this Module's output by adding the bias to the returned value.
     *
     * @param bias - the amount to bias this Module's output by
     * @return a new Bias Module
     */
    default Module bias(double bias) {
        return bias(Source.constant(bias));
    }

    /**
     * Combine two other Modules by using this one to decide how much of each should be blended together
     *
     * @param source0 - the first of the two Modules to blend
     * @param source1 - the second of the two Modules to blend
     * @param midpoint - the value at which source0 & source1 will be blended 50%:50% - values either side of this point
     *                 will strengthen the effect of either source Module proportionally to the distance from the midpoint
     * @param blendRange - the range over which blending occurs, outside of which will produce 100% source 1 or 2
     * @return a new Blend Module
     */
    default Module blend(Module source0, Module source1, double midpoint, double blendRange) {
        return blend(source0, source1, midpoint, blendRange, Interpolation.LINEAR);
    }

    /**
     * Combine two other Modules by using this one to decide how much of each should be blended together
     *
     * @param source0 - the first of the two Modules to blend
     * @param source1 - the second of the two Modules to blend
     * @param midpoint - the value at which source0 & source1 will be blended 50%:50% - values either side of this point
     *                 will strengthen the effect of either source Module proportionally to the distance from the midpoint
     * @param blendRange - the range over which blending occurs, outside of which will produce 100% source 1 or 2
     * @param interpolation - the interpolation method to use while blending the outputs
     * @return a new Blend Module
     */
    default Module blend(Module source0, Module source1, double midpoint, double blendRange, Interpolation interpolation) {
        return new Blend(this, source0, source1, (float) midpoint, (float) blendRange, interpolation);
    }

    /**
     * Similar to the Blend Module but uses an additional Module (blendVar) to vary the blend range at a given position
     *
     * @param variable
     * @param source0 - the first of the two Modules to blend
     * @param source1 - the second of the two Modules to blend
     * @param midpoint - the value at which source0 & source1 will be blended 50%:50% - values either side of this point
     *                  will strengthen the effect of either source Module proportionally to the distance from the midpoint
     * @param min - the lowest possible bound of the blend range
     * @param max - the highest possible bound of the blend range
     * @param interpolation - the interpolation method to use while blending the outputs
     * @return a new BlendVar Module
     */
    default Module blendVar(Module variable, Module source0, Module source1, double midpoint, double min, double max, Interpolation interpolation) {
        return new VariableBlend(this, variable, source0, source1, (float) midpoint, (float) min, (float) max, interpolation);
    }

    /**
     * Modifies this Module's output by amplifying lower values while higher values amplify less
     *
     * @return a new Boost Module
     */
    default Module boost() {
        return boost(1);
    }

    /**
     * Modifies this Module's output by amplifying lower values while higher values amplify less
     *
     * @param iterations - the number of times this Module should be boosted
     * @return a new Boost Module
     */
    default Module boost(int iterations) {
        if (iterations < 1) {
            return this;
        }
        return new Boost(this, iterations);
    }

    /**
     * Caches this Module's output for a given x,y coordinate (useful when this Module is being reused)
     * Cache Modules are not thread-safe.
     *
     * @return a new Cache Module
     */
    default Module cache() {
        if (this instanceof Cache) {
            return this;
        }
        return new Cache(this);
    }

    /**
     * Clamps the output of this Module between the provided min and max Module's output at a given coordinate
     *
     * @param min - a Module that provides the lower bound of the clamp
     * @param max - a Module that provides the upper bound of the clamp
     * @return a new Clamp Module
     */
    default Module clamp(Module min, Module max) {
        if (min.minValue() == min.maxValue() && min.minValue() == minValue() && max.minValue() == max.maxValue() && max.maxValue() == maxValue()) {
            return this;
        }
        return new Clamp(this, min, max);
    }

    /**
     * Clamps the output of this Module between the min and max values
     *
     * @param min - the lower bound of the clamp
     * @param max - the upper bound of the clamp
     * @return a new Clamp Module
     */
    default Module clamp(double min, double max) {
        return clamp(Source.constant(min), Source.constant(max));
    }

    /**
     * Applies a Curve function to the output of this Module
     *
     * @param func - the Curve function to apply to the output
     * @return a new Curve Module
     */
    default Module curve(CurveFunc func) {
        return new Curve(this, func);
    }

    /**
     * Applies a custom Curve function to the output of this Module
     *
     * @param mid - the mid point of the curve
     * @param steepness - the steepness of the curve
     * @return a new Curve Module
     */
    default Module curve(double mid, double steepness) {
        return new Curve(this, new MidPointCurve((float) mid, (float) steepness));
    }

    default Module freq(double x, double y) {
        return freq(Source.constant(x), Source.constant(y));
    }

    default Module freq(Module x, Module y) {
        return new Freq(this, x, y);
    }

    /**
     * Applies a custom Curve function to the output of this Module
     *
     * @param mid - the mid point of the curve
     * @param steepness - the steepness of the curve
     * @return a new Curve Module
     */
    default Module curve(Module mid, Module steepness) {
        return new VariableCurve(this, mid, steepness);
    }

    default Module grad(double lower, double upper, double strength) {
        return grad(Source.constant(lower), Source.constant(upper), Source.constant(strength));
    }

    default Module grad(Module lower, Module upper, Module strength) {
        return new Grad(this, lower, upper, strength);
    }

    /**
     * Inverts the output of this Module (0.1 becomes 0.9 for a standard Module who's output is between 0 and 1)
     *
     * @return a new Invert Module
     */
    default Module invert() {
        return new Invert(this);
    }

    /**
     * Maps the output of this Module so that it lies proportionally between the min and max values at a given coordinate
     *
     * @param min - the lower bound
     * @param max - the upper bound
     * @return a new Map module
     */
    default Module map(Module min, Module max) {
        if (min.minValue() == min.maxValue() && min.minValue() == minValue() && max.minValue() == max.maxValue() && max.maxValue() == maxValue()) {
            return this;
        }
        return new Map(this, min, max);
    }

    /**
     * Maps the output of this Module so that it lies proportionally between the min and max values at a given coordinate
     *
     * @param min - the lower bound
     * @param max - the upper bound
     * @return a new Map module
     */
    default Module map(double min, double max) {
        return map(Source.constant(min), Source.constant(max));
    }

    /**
     * Returns the highest value out of this and the other Module's outputs
     *
     * @param other - the other Module to use
     * @return a new Max Module
     */
    default Module max(Module other) {
        return new Max(this, other);
    }

    /**
     * Returns the lowest value out of this and the other Module's outputs
     *
     * @param other - the other Module to use
     * @return a new Min Module
     */
    default Module min(Module other) {
        return new Min(this, other);
    }

    /**
     * Modulates the coordinates before querying this Module for an output
     *
     * @param direction - a Module that controls the direction of the modulation
     * @param strength - a Module that controls the strength of deviation in the given direction
     * @return a new Modulate Module
     */
    default Module mod(Module direction, Module strength) {
        return new Modulate(this, direction, strength);
    }

    /**
     * Multiplies the outputs of this and the other Module
     *
     * @param other - the other Module to multiply
     * @return a new Multiply Module
     */
    default Module mult(Module other) {
        if (other.minValue() == 1F && other.maxValue() == 1F) {
            return this;
        }
        return new Multiply(this, other);
    }

    default Module blend(double blend, Module... sources) {
        return blend(blend, Interpolation.LINEAR, sources);
    }

    default Module blend(double blend, Interpolation interpolation, Module... sources) {
        return new MultiBlend((float) blend, interpolation, this, sources);
    }

    default Module pow(Module n) {
        if (n.minValue() == 0 && n.maxValue() == 0) {
            return Source.ONE;
        }
        if (n.minValue() == 1 && n.maxValue() == 1) {
            return this;
        }
        return new Power(this, n);
    }

    default Module pow(double n) {
        return pow(Source.constant(n));
    }

    default Module powCurve(double n) {
        return new PowerCurve(this, (float) n);
    }

    default Module scale(Module scale) {
        if (scale.minValue() == 1 && scale.maxValue() == 1) {
            return this;
        }
        return new Scale(this, scale);
    }

    default Module scale(double scale) {
        return scale(Source.constant(scale));
    }

    default Module select(Module lower, Module upper, double lowerBound, double upperBound, double falloff) {
        return select(lower, upper, lowerBound, upperBound, falloff, Interpolation.CURVE3);
    }

    default Module select(Module lower, Module upper, double lowerBound, double upperBound, double falloff, Interpolation interpolation) {
        return new Select(this, lower, upper, (float) lowerBound, (float) upperBound, (float) falloff, interpolation);
    }

    default Module steps(int steps) {
        return steps(steps, 0, 0);
    }

    default Module steps(int steps, double slopeMin, double slopeMax) {
        return steps(steps, slopeMin, slopeMax, Interpolation.LINEAR);
    }

    default Module steps(int steps, double slopeMin, double slopeMax, CurveFunc curveFunc) {
        return steps(Source.constant(steps), Source.constant(slopeMin), Source.constant(slopeMax), curveFunc);
    }

    default Module steps(Module steps, Module slopeMin, Module slopeMax) {
        return steps(steps, slopeMin, slopeMax, Interpolation.LINEAR);
    }

    default Module steps(Module steps, Module slopeMin, Module slopeMax, CurveFunc curveFunc) {
        return new Steps(this, steps, slopeMin, slopeMax, curveFunc);
    }

    default Module sub(Module other) {
        return new Sub(this, other);
    }

    default Module legacyTerrace(double lowerCurve, double upperCurve, int steps, double blendRange) {
        return legacyTerrace(Source.constant(lowerCurve), Source.constant(upperCurve), steps, blendRange);
    }

    default Module legacyTerrace(Module lowerCurve, Module upperCurve, int steps, double blendRange) {
        return new LegacyTerrace(this, lowerCurve, upperCurve, steps, (float) blendRange);
    }

    default Module terrace(double lowerCurve, double upperCurve, double lowerHeight, int steps, double blendRange) {
        return terrace(Source.constant(lowerCurve), Source.constant(upperCurve), Source.constant(lowerHeight), steps, blendRange);
    }

    default Module terrace(Module lowerCurve, Module upperCurve, Module lowerHeight, int steps, double blendRange) {
        return new Terrace(this, lowerCurve, upperCurve, lowerHeight, steps, (float) blendRange);
    }

    default Module terrace(Module modulation, double slope, double blendMin, double blendMax, int steps) {
        return terrace(modulation, Source.ONE, slope, blendMin, blendMax, steps);
    }

    default Module terrace(Module modulation, double slope, double blendMin, double blendMax, int steps, int octaves) {
        return terrace(modulation, Source.ONE, Source.constant(slope), blendMin, blendMax, steps, octaves);
    }

    default Module terrace(Module modulation, Module mask, double slope, double blendMin, double blendMax, int steps) {
        return terrace(modulation, mask, Source.constant(slope), blendMin, blendMax, steps, 1);
    }

    default Module terrace(Module modulation, Module mask, Module slope, double blendMin, double blendMax, int steps) {
        return new AdvancedTerrace(this, modulation, mask, slope, (float) blendMin, (float) blendMax, steps, 1);
    }

    default Module terrace(Module modulation, Module mask, Module slope, double blendMin, double blendMax, int steps, int octaves) {
        return new AdvancedTerrace(this, modulation, mask, slope, (float) blendMin, (float) blendMax, steps, octaves);
    }

    default Module threshold(double threshold) {
        return new Threshold(this, Source.constant(threshold));
    }

    default Module threshold(Module threshold) {
        return new Threshold(this, threshold);
    }

    default Module warp(Domain domain) {
        return new Warp(this, domain);
    }

    default Module warp(Module warpX, Module warpZ, double power) {
        return warp(warpX, warpZ, Source.constant(power));
    }

    default Module warp(Module warpX, Module warpZ, Module power) {
        return warp(Domain.warp(warpX, warpZ, power));
    }

    default Module warp(int seed, int scale, int octaves, double power) {
        return warp(Source.PERLIN, seed, scale, octaves, power);
    }

    default Module warp(Source source, int seed, int scale, int octaves, double power) {
        Module x = Source.build(seed, scale, octaves).build(source);
        Module y = Source.build(seed + 1, scale, octaves).build(source);
        Module p = Source.constant(power);
        return warp(x, y, p);
    }

    default Module warp(Class<? extends NoiseSource> source, int seed, int scale, int octaves, double power) {
        Module x = Source.build(seed, scale, octaves).build(source);
        Module y = Source.build(seed + 1, scale, octaves).build(source);
        Module p = Source.constant(power);
        return warp(x, y, p);
    }
}
