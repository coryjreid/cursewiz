package com.coryjreid.cursewiz.json;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MinecraftInstance {

    private final Manifest mManifest;
    private final List<InstalledAddon> mInstalledAddons;

    @JsonCreator
    public MinecraftInstance(
        @JsonProperty("manifest") final Manifest manifest,
        @JsonProperty("installedAddons") final List<InstalledAddon> installedAddons) {
        mManifest = manifest;
        mInstalledAddons = installedAddons;
    }

    public Manifest getManifest() {
        return mManifest;
    }

    public List<InstalledAddon> getInstalledAddons() {
        return mInstalledAddons;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MinecraftInstance that = (MinecraftInstance) o;
        return mManifest.equals(that.mManifest) && mInstalledAddons.equals(that.mInstalledAddons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mManifest, mInstalledAddons);
    }

    @Override
    public String toString() {
        return "MinecraftInstance{" +
            "mManifest=" + mManifest +
            ", mInstalledAddons=" + mInstalledAddons +
            '}';
    }
}
