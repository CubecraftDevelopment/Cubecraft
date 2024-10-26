package com.terraforged.noise.func;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.util.NoiseUtil;

public class SCurve implements CurveFunc {

    private final float lower;
    private final float upper;

    public SCurve(float lower, float upper) {
        this.lower = lower;
        this.upper = upper < 0 ? Math.max(-lower, upper) : upper;
    }

    @Override
    public String getSpecName() {
        return "SCurve";
    }

    @Override
    public float apply(float value) {
        return NoiseUtil.pow(value, lower + (upper * value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SCurve sCurve = (SCurve) o;

        if (Float.compare(sCurve.lower, lower) != 0) return false;
        return Float.compare(sCurve.upper, upper) == 0;
    }

    @Override
    public int hashCode() {
        int result = (lower != 0.0f ? Float.floatToIntBits(lower) : 0);
        result = 31 * result + (upper != 0.0f ? Float.floatToIntBits(upper) : 0);
        return result;
    }

    private static final DataFactory<SCurve> factory = (data, spec, context) -> new SCurve(
            spec.get("lower", data, DataValue::asFloat),
            spec.get("upper", data, DataValue::asFloat)
    );

    public static DataSpec<SCurve> spec() {
        return DataSpec.builder("SCurve", SCurve.class, factory)
                .add("lower", 0, s -> s.lower)
                .add("upper", 1, s -> s.upper)
                .build();
    }
}
