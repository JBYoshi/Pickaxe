package jbyoshi.sponge.pickaxe.ore.local;

import jbyoshi.sponge.pickaxe.ore.Download;
import jbyoshi.sponge.pickaxe.ore.OreProject;
import jbyoshi.sponge.pickaxe.ore.OreVersion;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

final class LocalOreProject implements OreProject {
    private final PluginContainer container;

    LocalOreProject(PluginContainer container) {
        this.container = container;
    }

    @Override
    public String getPluginId() {
        return container.getId();
    }

    @Override
    public String getAuthor() {
        List<String> authors = container.getAuthors();
        if (authors.isEmpty()) {
            return "unknown";
        }
        return authors.get(0);
    }

    @Override
    public String getName() {
        return container.getName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public OreVersion getRecommendedVersion() {
        return ((Optional<OreVersion>) (Optional<?>) getInstallation()).orElse(new NotInstalledVersion());
    }

    @Override
    public Optional<? extends OreVersion> getVersion(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<PluginContainer> getContainer() {
        return Optional.of(container);
    }

    private final class NotInstalledVersion implements OreVersion {
        @Override
        public String getVersion() {
            return container.getVersion().orElse("unknown");
        }

        @Override
        public OreProject getProject() {
            return LocalOreProject.this;
        }

        @Override
        public Optional<Download> download() throws IOException {
            return Optional.empty();
        }
    }
}
