package jbyoshi.sponge.pickaxe.ore;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public final class Download {
    private final String name;
    private final InputStream inputStream;
    public Download(HttpURLConnection connection) throws IOException {
        this.inputStream = connection.getInputStream();
        String contentDisposition = connection.getHeaderField("Content-Disposition");
        findFile: {
            /* TODO make this work
            for (String part : contentDisposition.split(";")) {
                if (part.trim().startsWith("filename*")) {
                    part = part.trim().substring("filename*".length()).trim();
                    if (!part.startsWith("=")) {
                        break;
                    }
                    part = part.substring(1).trim();
                    name = ???;
                    break findFile;
                }
            }
            */
            for (String part : contentDisposition.split(";")) {
                if (part.trim().startsWith("filename")) {
                    part = part.trim().substring("filename".length()).trim();
                    if (!part.startsWith("=")) {
                        break;
                    }
                    part = part.substring(1).trim();
                    if (part.startsWith("\"")) {
                        part = part.substring(1);
                        StringBuilder sb = new StringBuilder();
                        for (char c : part.toCharArray()) {
                            if (c == '"') {
                                name = sb.toString();
                                break findFile;
                            }
                            sb.append(c);
                        }
                    }
                    name = part;
                    break findFile;
                }
            }
            String file = connection.getURL().getPath();
            name = file.substring(file.lastIndexOf('/') + 1);
        }
    }

    public String getName() {
        return name;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
