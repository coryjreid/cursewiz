package com.coryjreid.cursewiz.toml;

import com.moandjiezana.toml.Toml;

public class PackMod {
    private final String mModName;
    private final String mFileName;
    private final String mSide;
    private final String mUrl;
    private final String mHashFormat;
    private final String mHash;
    private final long mFileId;
    private final long mProjectId;

    public PackMod(
        final String modName,
        final String fileName,
        final String side,
        final String url,
        final String hashFormat,
        final String hash,
        final long fileId,
        final long projectId) {
        mModName = modName;
        mFileName = fileName;
        mSide = side;
        mUrl = url;
        mHashFormat = hashFormat;
        mHash = hash;
        mFileId = fileId;
        mProjectId = projectId;
    }

    public String getModName() {
        return mModName;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getSide() {
        return mSide;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getHashFormat() {
        return mHashFormat;
    }

    public String getHash() {
        return mHash;
    }

    public long getFileId() {
        return mFileId;
    }

    public long getProjectId() {
        return mProjectId;
    }

    public static PackMod createPackModFromToml(final Toml toml) {
        return new PackMod(
            toml.getString("name"),
            toml.getString("filename"),
            toml.getString("side"),
            toml.getString("download.url"),
            toml.getString("download.hash-format"),
            toml.getString("download.hash"),
            toml.getLong("update.curseforge.file-id"),
            toml.getLong("update.curseforge.project-id"));
    }
}
