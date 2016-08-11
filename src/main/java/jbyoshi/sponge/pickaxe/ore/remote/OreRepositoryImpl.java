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

import com.google.gson.Gson;
import com.google.inject.util.Types;
import jbyoshi.sponge.pickaxe.ore.OreProject;
import jbyoshi.sponge.pickaxe.ore.OreRepository;
import jbyoshi.sponge.pickaxe.ore.ssl.PickaxeSSLSocketFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class OreRepositoryImpl implements OreRepository {
    private static final Gson gson = new Gson();
    private final String urlBase;

    public OreRepositoryImpl(String urlBase) {
        this.urlBase = urlBase;
    }

    <T> Optional<T> request(String url, Type type) throws IOException {
        HttpURLConnection conn = request(url);
        if (conn.getResponseCode() == 404) {
            return Optional.empty();
        }
        try (Reader reader = new InputStreamReader(conn.getInputStream())) {
            return Optional.of(gson.fromJson(reader, type));
        }
    }

    HttpURLConnection request(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlBase + url).openConnection();
        PickaxeSSLSocketFactory.wrap(conn);
        return conn;
    }

    private Collection<OreProjectImpl> getProjects() throws IOException {
        List<OreProjectImpl> allProjects = this.<List<OreProjectImpl>>request("/api/v1/projects",
                Types.newParameterizedType(List.class, OreProjectImpl.class))
                .orElseThrow(() -> new IOException("URL not found"));
        for (OreProjectImpl project : allProjects) {
            project.repository = this;
        }
        return allProjects;
    }

    @Override
    public Optional<? extends OreProject> findProjectById(String id) throws IOException {
        Optional<OreProjectImpl> project = request("/api/v1/projects/" + id, OreProjectImpl.class);
        project.ifPresent(p -> p.repository = this);
        return project;
    }

    @Override
    public Optional<? extends OreProject> findProjectByOwnerAndName(String owner, String name) throws IOException {
        return getProjects().stream().filter(p -> p.getAuthor().equals(owner) && p.getName().equals(name))
                .findAny();
    }
}
