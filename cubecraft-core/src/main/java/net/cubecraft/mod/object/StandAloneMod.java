package net.cubecraft.mod.object;

import java.io.IOException;
import java.util.jar.JarFile;

public final class StandAloneMod extends Mod {
    private final JarFile file;

    public StandAloneMod(JarFile file) {
        this.file = file;
    }

    @Override
    public void loadDescriptionInfo() {
        try {
            JarFile file = this.getFile();
            this.getDocument().read(file.getInputStream(file.getEntry("mod_info.toml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JarFile getFile() {
        return file;
    }
}
