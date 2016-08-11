package jbyoshi.sponge.pickaxe.ore;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;

import java.io.IOException;
import java.util.Optional;

public interface OreProject extends TextRepresentable {
    String getPluginId();

    String getAuthor();

    String getName();

    default Optional<OrePluginInstallation> getInstallation() {
        return OrePluginInstallation.of(this);
    }

    OreVersion getRecommendedVersion();

    Optional<? extends OreVersion> getVersion(String name) throws IOException;

    default Optional<PluginContainer> getContainer() {
        return Sponge.getPluginManager().getPlugin(getPluginId());
    }

    @Override
    default Text toText() {
        return Text.of(getName(), " by ", getAuthor());
    }
}
