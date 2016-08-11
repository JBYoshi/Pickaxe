package jbyoshi.sponge.pickaxe.ore;

import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public final class OrePluginInstallation implements OreVersion {
    private final OreProject project;
    private final PluginContainer container;

    private OrePluginInstallation(OreProject project, PluginContainer container) {
        this.project = project;
        this.container = container;
    }

    @Override
    public String getVersion() {
        return container.getVersion().orElse("unknown");
    }

    @Override
    public OreProject getProject() {
        return project;
    }

    public Path getSource() {
        return container.getSource().orElseThrow(AssertionError::new);
    }

    @SuppressWarnings("unchecked")
    public Object getInstance() {
        return ((Optional<Object>) container.getInstance()).orElse(container);
    }

    @Override
    public Optional<Download> download() throws IOException {
        Optional<? extends OreVersion> version = project.getVersion(getVersion());
        if (version.isPresent()) {
            return version.get().download();
        }
        return Optional.empty();
    }

    static Optional<OrePluginInstallation> of(OreProject project) {
        return project.getContainer().filter(e -> e.getSource().isPresent())
                .map(e -> new OrePluginInstallation(project, e));
    }
}
