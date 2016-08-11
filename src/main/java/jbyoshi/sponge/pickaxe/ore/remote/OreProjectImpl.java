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
package jbyoshi.sponge.pickaxe.ore.remote;

import jbyoshi.sponge.pickaxe.ore.Download;
import jbyoshi.sponge.pickaxe.ore.OreProject;
import jbyoshi.sponge.pickaxe.ore.OreVersion;

import java.io.IOException;
import java.util.Optional;

final class OreProjectImpl implements OreProject {
    transient OreRepositoryImpl repository;
    @SuppressWarnings("unused")
    private String pluginId;
    @SuppressWarnings("unused")
    private String owner;
    @SuppressWarnings("unused")
    private String name;
    @SuppressWarnings("unused")
    private RecommendedVersion recommended;

    @Override
    public String getPluginId() {
        return pluginId;
    }

    @Override
    public String getAuthor() {
        return owner;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OreVersion getRecommendedVersion() {
        recommended.project = this;
        return recommended;
    }

    @Override
    public Optional<? extends OreVersion> getVersion(String name) throws IOException {
        Optional<OreVersionImpl> version = repository.request("/api/v1/projects/" + pluginId + "/versions/" + name,
                OreVersionImpl.class);
        version.ifPresent(v -> v.project = this);
        return version;
    }

    private static final class RecommendedVersion implements OreVersion {
        @SuppressWarnings("unused")
        private String version;
        private transient OreProjectImpl project;

        @Override
        public String getVersion() {
            return version;
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

    Download download(OreVersion version) throws IOException {
        return new Download(repository.request("/" + owner + "/" + name + "/versions/" + version.getVersion() + "/jar"));
    }
}
