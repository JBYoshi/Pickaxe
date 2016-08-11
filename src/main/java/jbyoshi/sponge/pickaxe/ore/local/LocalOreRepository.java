package jbyoshi.sponge.pickaxe.ore.local;

import jbyoshi.sponge.pickaxe.ore.OreProject;
import jbyoshi.sponge.pickaxe.ore.OreRepository;
import jbyoshi.sponge.pickaxe.ore.OreVersion;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class LocalOreRepository implements OreRepository {
    public static final LocalOreRepository INSTANCE = new LocalOreRepository();
    private final Map<PluginContainer, LocalOreProject> projects;

    private LocalOreRepository() {
        this.projects = Sponge.getPluginManager().getPlugins().stream().collect(Collectors.toMap(Function.identity(),
                LocalOreProject::new));
    }

    @Override
    public Optional<? extends OreProject> findProjectById(String id) throws IOException {
        return Sponge.getPluginManager().getPlugin(id).map(projects::get);
    }

    @Override
    public Optional<? extends OreProject> findProjectByOwnerAndName(String owner, String name) {
        return Optional.empty();
    }
}
