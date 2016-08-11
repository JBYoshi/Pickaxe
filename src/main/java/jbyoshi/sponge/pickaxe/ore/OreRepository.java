package jbyoshi.sponge.pickaxe.ore;

import java.io.IOException;
import java.util.Optional;

public interface OreRepository {
    Optional<? extends OreProject> findProjectById(String id) throws IOException;

    Optional<? extends OreProject> findProjectByOwnerAndName(String owner, String name) throws IOException;
}
