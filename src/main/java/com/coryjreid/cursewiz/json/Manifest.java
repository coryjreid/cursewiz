package com.coryjreid.cursewiz.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = Manifest.Deserializer.class)
public class Manifest {

    private final String mMinecraftVersion;
    private final String mModLoaderVersion;
    private final List<File> mFiles;

    public Manifest(final String minecraftVersion, final String modLoaderVersion, final List<File> files) {
        mMinecraftVersion = minecraftVersion;
        mModLoaderVersion = modLoaderVersion;
        mFiles = files;
    }

    public Collection<File> getFiles() {
        return new HashSet<>(mFiles);
    }

    public String getMinecraftVersion() {
        return mMinecraftVersion;
    }

    public String getModLoaderVersion() {
        return mModLoaderVersion;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Manifest manifest = (Manifest) o;
        return mFiles.equals(manifest.mFiles)
            && mMinecraftVersion.equals(manifest.mMinecraftVersion)
            && mModLoaderVersion.equals(manifest.mModLoaderVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mFiles, mMinecraftVersion, mModLoaderVersion);
    }

    @Override
    public String toString() {
        return "Manifest{" +
            "mMinecraftVersion='" + mMinecraftVersion + '\'' +
            ", mModLoaderVersion='" + mModLoaderVersion + '\'' +
            ", mFiles=" + mFiles +
            '}';
    }

    public static class Deserializer extends StdDeserializer<Manifest> {
        public Deserializer() {
            this(null);
        }

        protected Deserializer(final Class<?> vc) {
            super(vc);
        }

        @Override
        public Manifest deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
            final JsonNode rootNode = parser.getCodec().readTree(parser);
            final String minecraftVersion = rootNode.get("minecraft").get("version").asText();
            final String modLoaderVersion = rootNode.get("minecraft").get("modLoaders").get(0).get("id").asText();
            final List<File> files = new ArrayList<>();
            rootNode.get("files").forEach(node -> {
                files.add(new File(node.get("projectID").asInt(), node.get("fileID").asInt()));
            });

            return new Manifest(minecraftVersion, modLoaderVersion, files);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class File {

        private final int mProjectId;
        private final int mFileId;

        @JsonCreator
        public File(
            @JsonProperty("projectID") final int projectId,
            @JsonProperty("fileID") final int fileId) {
            mProjectId = projectId;
            mFileId = fileId;
        }

        public int getProjectId() {
            return mProjectId;
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
            final File file = (File) o;
            return mProjectId == file.mProjectId && mFileId == file.mFileId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(mProjectId, mFileId);
        }

        @Override
        public String toString() {
            return "File{" +
                "mProjectId=" + mProjectId +
                ", mFileId=" + mFileId +
                '}';
        }
    }
}
