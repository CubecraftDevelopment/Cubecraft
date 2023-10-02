package ink.flybird.cubecraft.resource;

import ink.flybird.cubecraft.EnvironmentPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ResourcePack {
    private final ArrayList<ZipEntry> entries = new ArrayList<>();
    private ZipFile file;

    /**
     * load file from disk，read all entries to file path
     *
     * @param filePath abs path
     */
    public void load(String filePath) {
        try {
            filePath = EnvironmentPath.RESOURCE_PACK_FOLDER + filePath;
            this.file = new ZipFile(new File(filePath));
            this.entries.clear();
            ZipInputStream inputStream = new ZipInputStream(new FileInputStream(filePath));
            ZipEntry e;
            while ((e = inputStream.getNextEntry()) != null) {
                this.entries.add(e);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getInput(String path) throws IOException {
        return this.file.getInputStream(this.file.getEntry(path));
    }
}