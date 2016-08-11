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
