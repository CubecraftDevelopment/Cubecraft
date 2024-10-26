package com.terraforged.noise.func;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.util.NoiseUtil;

public class MidPointCurve implements CurveFunc {

    private final float mid;
    private final float steepness;

    public MidPointCurve(float mid, float steepness) {
        this.mid = mid;
        this.steepness = steepness;
    }

    @Override
    public String getSpecName() {
        return "MidCurve";
    }

    @Override
    public float apply(float value) {
        return NoiseUtil.curve(value, mid, steepness);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MidPointCurve that = (MidPointCurve) o;

        if (Float.compare(that.mid, mid) != 0) return false;
        return Float.compare(that.steepness, steepness) == 0;
    }

    @Override
    public int hashCode() {
        int result = (mid != 0.0f ? Float.floatToIntBits(mid) : 0);
        result = 31 * result + (steepness != 0.0f ? Float.floatToIntBits(steepness) : 0);
        return result;
    }

    private static final DataFactory<MidPointCurve> factory = (data, spec, context) -> new MidPointCurve(
            spec.get("midpoint", data, DataValue::asFloat),
            spec.get("steepness", data, DataValue::asFloat)
    );

    public static DataSpec<MidPointCurve> spec() {
        return DataSpec.builder("MidCurve", MidPointCurve.class, factory)
                .add("midpoint", 0.5F, m -> m.mid)
                .add("steepness", 4, m -> m.steepness)
                .build();
    }
}
