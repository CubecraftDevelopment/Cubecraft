//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.gb2022.quantum3d.util;

public class RunningAvg {
    private final long[] slots;
    private int offset;

    public RunningAvg(int slotCount) {
        this.slots = new long[slotCount];
        this.offset = 0;
    }

    public void init(long value) {
        while(this.offset < this.slots.length) {
            this.slots[this.offset++] = value;
        }

    }

    public void add(long value) {
        this.slots[this.offset++ % this.slots.length] = value;
        this.offset %= this.slots.length;
    }

    public long avg() {
        long sum = 0L;

        for (long slot : this.slots) {
            sum += slot;
        }

        return sum / (long)this.slots.length;
    }

    public void dampenForLowResTicker() {
        if (this.avg() > 10000000L) {
            for(int i = 0; i < this.slots.length; ++i) {
                long[] var10000 = this.slots;
                var10000[i] = (long)((float)var10000[i] * 0.9F);
            }
        }

    }
}
