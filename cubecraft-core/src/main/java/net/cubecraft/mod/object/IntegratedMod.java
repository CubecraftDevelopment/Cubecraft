package net.cubecraft.mod.object;

public final class IntegratedMod extends Mod {
    private final String descriptorLocation;

    public IntegratedMod(String descriptorLocation) {

        this.descriptorLocation = descriptorLocation;
    }

    @Override
    public void loadDescriptionInfo() {
        try {
            this.getDocument().read(IntegratedMod.class.getResourceAsStream(this.descriptorLocation));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getDescriptorLocation() {
        return this.descriptorLocation;
    }
}
