package jbyoshi.sponge.pickaxe.ore;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class CombinedOreRepository implements OreRepository {
    private final List<OreRepository> delegates;

    public CombinedOreRepository(OreRepository...delegates) {
        this.delegates = Arrays.asList(delegates);
    }

    @Override
    public Optional<? extends OreProject> findProjectById(String id) throws IOException {
        for (OreRepository repo : delegates) {
            Optional<? extends OreProject> project = repo.findProjectById(id);
            if (project.isPresent()) {
                return project;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<? extends OreProject> findProjectByOwnerAndName(String owner, String name) throws IOException {
        for (OreRepository repo : delegates) {
            Optional<? extends OreProject> project = repo.findProjectByOwnerAndName(owner, name);
            if (project.isPresent()) {
                return project;
            }
        }
        return Optional.empty();
    }
}
