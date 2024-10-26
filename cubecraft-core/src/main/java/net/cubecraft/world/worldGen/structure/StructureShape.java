package net.cubecraft.world.worldGen.structure;

public interface StructureShape {
    void generate(StructureGeneratingContainer container);

    int radius();

    int topBound();

    default int bottomBound() {
        return 1;
    }
}
