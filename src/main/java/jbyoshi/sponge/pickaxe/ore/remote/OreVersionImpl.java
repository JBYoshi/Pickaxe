package jbyoshi.sponge.pickaxe.ore.remote;

import jbyoshi.sponge.pickaxe.ore.Download;
import jbyoshi.sponge.pickaxe.ore.OreProject;
import jbyoshi.sponge.pickaxe.ore.OreVersion;

import java.io.IOException;
import java.util.Optional;

final class OreVersionImpl implements OreVersion {
    @SuppressWarnings("unused")
    private String name;

    transient OreProjectImpl project;

    @Override
    public String getVersion() {
        return name;
    }

    @Override
    public OreProject getProject() {
        return project;
    }

    @Override
    public Optional<Download> download() throws IOException {
        return Optional.of(project.download(this));
    }
}
