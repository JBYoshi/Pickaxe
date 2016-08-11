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
