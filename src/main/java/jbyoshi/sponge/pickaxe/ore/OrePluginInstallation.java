/*
 * Copyright (c) 2016 JBYoshi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
