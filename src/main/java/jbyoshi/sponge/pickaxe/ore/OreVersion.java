package jbyoshi.sponge.pickaxe.ore;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;

import java.io.IOException;
import java.util.Optional;

public interface OreVersion extends TextRepresentable {
    String getVersion();

    OreProject getProject();

    Optional<Download> download() throws IOException;

    @Override
    default Text toText() {
        return Text.of(getProject(), " ", getVersion());
    }
}
