package net.cubecraft.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionInfo {
    public static final Pattern DISPATCHER = Pattern.compile("(\\w+)-(\\d+)\\.(\\d+)\\.(\\d+)-(\\w+)");
    public static final String CLIENT_ARTIFACT_ID = "cubecraft-client";
    public static final String SERVER_ARTIFACT_ID = "cubecraft-server";

    private final String artifactId;
    private final int archVersion;
    private final int majorVersion;
    private final int minorVersion;
    private final String buildVersion;

    public VersionInfo(String artifactId, int archVersion, int majorVersion, int minorVersion, String buildVersion) {
        this.artifactId = artifactId;
        this.archVersion = archVersion;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.buildVersion = buildVersion;
    }

    public VersionInfo(String version) {
        Matcher matcher = DISPATCHER.matcher(version);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(version);
        }
        this.artifactId = matcher.group(1);
        this.archVersion = Integer.parseInt(matcher.group(2));
        this.majorVersion = Integer.parseInt(matcher.group(3));
        this.minorVersion = Integer.parseInt(matcher.group(4));
        this.buildVersion = matcher.group(5);
    }

    public String getArtifactId() {
        return artifactId;
    }

    public int getArchVersion() {
        return archVersion;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public boolean matchArtifact(VersionInfo version) {
        return this.artifactId.equals(version.getArtifactId());
    }

    public boolean archCompat(VersionInfo version) {
        if (!this.matchArtifact(version)) {
            return false;
        }
        return this.archVersion >= version.getArchVersion();
    }

    public boolean versionCompat(VersionInfo version) {
        if (!this.matchArtifact(version)) {
            return false;
        }
        if (!this.archCompat(version)) {
            return false;
        }
        if (this.majorVersion < version.getMajorVersion()) {
            return false;
        }
        return this.minorVersion >= version.getMinorVersion();
    }

    public boolean compat(VersionInfo version) {
        if (!this.matchArtifact(version)) {
            return false;
        }
        if (!this.archCompat(version)) {
            return false;
        }
        return this.versionCompat(version);
    }

    public boolean match(VersionInfo version) {
        if (!this.matchArtifact(version)) {
            return false;
        }
        if (this.archVersion != version.getArchVersion()) {
            return false;
        }
        if (this.majorVersion != version.getMajorVersion()) {
            return false;
        }
        return this.minorVersion == version.getMinorVersion();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VersionInfo version) {
            if (!this.match(version)) {
                return false;
            }
            return Objects.equals(version.getBuildVersion(), this.buildVersion);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return "%s-%d.%d.%d-%s".formatted(
                this.artifactId,
                this.archVersion,
                this.majorVersion,
                this.minorVersion,
                this.buildVersion
        );
    }

    public String shortVersion() {
        return "%d.%d.%d-%s".formatted(
                this.archVersion,
                this.majorVersion,
                this.minorVersion,
                this.buildVersion
        );
    }
}
