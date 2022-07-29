package com.coryjreid.cursewiz.json;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = InstalledAddon.Deserializer.class)
public class InstalledAddon {

    private final int mAddonId;
    private final int mFileId;

    public InstalledAddon(final int addonId, final int fileId) {
        mAddonId = addonId;
        mFileId = fileId;
    }

    public int getAddonId() {
        return mAddonId;
    }

    public int getFileId() {
        return mFileId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final InstalledAddon that = (InstalledAddon) o;
        return mAddonId == that.mAddonId && mFileId == that.mFileId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mAddonId, mFileId);
    }

    @Override
    public String toString() {
        return "InstalledAddon{" +
            "mAddonId=" + mAddonId +
            ", mFileId=" + mFileId +
            '}';
    }

    public static class Deserializer extends StdDeserializer<InstalledAddon> {
        public Deserializer() {
            this(null);
        }

        protected Deserializer(final Class<?> vc) {
            super(vc);
        }

        @Override
        public InstalledAddon deserialize(final JsonParser parser, final DeserializationContext context)
            throws IOException {

            final JsonNode rootNode = parser.getCodec().readTree(parser);
            final int addonId = rootNode.get("addonID").asInt();
            final int fileId = rootNode.get("installedFile").get("id").asInt();

            return new InstalledAddon(addonId, fileId);
        }
    }
}
