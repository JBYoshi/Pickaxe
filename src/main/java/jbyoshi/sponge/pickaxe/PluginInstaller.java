package jbyoshi.sponge.pickaxe;

import jbyoshi.sponge.pickaxe.ore.Download;
import jbyoshi.sponge.pickaxe.ore.OreProject;
import jbyoshi.sponge.pickaxe.ore.OrePluginInstallation;
import jbyoshi.sponge.pickaxe.ore.OreVersion;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

final class PluginInstaller {
    private final Path pluginsDir;

    private final Map<OreProject, PendingInstall> toInstall = Collections.synchronizedMap(new HashMap<>());
    private final Set<OrePluginInstallation> toUninstall = Collections.synchronizedSet(new HashSet<>());
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    PluginInstaller(Path pluginsDir) {
        this.pluginsDir = pluginsDir;
    }

    private final class PendingInstall {
        private final OreVersion version;
        private final Path sourceFile;
        private final Path destFile;

        private PendingInstall(OreVersion version, Path sourceFile, Path destFile) {
            this.version = version;
            this.sourceFile = sourceFile;
            this.destFile = destFile;
        }
    }

    void installAsync(OreVersion version, Download download) throws IOException {
        lock.readLock().lock();
        try (InputStream in = download.getInputStream()) {
            Path tempFile = Files.createTempFile("pickaxe-download-" + download.getName() + "-", ".jar.partial");
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            toInstall.put(version.getProject(), new PendingInstall(version, tempFile,
                    pluginsDir.resolve(download.getName())));
            version.getProject().getInstallation().ifPresent(toUninstall::add);
        } finally {
            lock.readLock().unlock();
        }
    }

    void uninstall(OrePluginInstallation plugin) {
        toUninstall.add(plugin);
        toInstall.remove(plugin.getProject());
    }

    Map<OreProject, OreVersion> getToInstall() {
        return Arrays.stream(toInstall.values().toArray()).map(PendingInstall.class::cast).map(e -> e.version)
                .collect(Collectors.toMap(OreVersion::getProject, Function.identity()));
    }

    Set<OrePluginInstallation> getToUninstall() {
        return toUninstall;
    }

    void execute() throws IOException {
        lock.writeLock().lock();
        try {
            Set<URLClassLoader> closedClassLoaders = new HashSet<>();
            Set<Path> toDelete = new HashSet<>();
            for (OrePluginInstallation plugin : toUninstall) {
                Path source = plugin.getSource();
                @SuppressWarnings("unchecked")
                ClassLoader loader = plugin.getInstance().getClass().getClassLoader();
                if (loader instanceof URLClassLoader) {
                    URLClassLoader ucl = (URLClassLoader) loader;
                    if (closedClassLoaders.add(ucl)) {
                        loader.getResources("*");
                        ucl.close();
                    }
                }
                toDelete.add(source.toAbsolutePath());
            }
            for (PendingInstall install : toInstall.values()) {
                toDelete.remove(install.destFile.toAbsolutePath());
                Files.move(install.sourceFile, install.destFile);
            }
            for (Path path : toDelete) {
                Files.delete(path);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
