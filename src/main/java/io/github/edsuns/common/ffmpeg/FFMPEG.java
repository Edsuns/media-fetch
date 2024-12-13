package io.github.edsuns.common.ffmpeg;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author edsuns@qq.com
 * @since 2024/1/20 11:15
 */
@ParametersAreNonnullByDefault
public class FFMPEG {
    public static class Proxy {
        private final String host;
        private final int port;

        public Proxy(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }

    @Nullable
    protected Proxy proxy;
    protected String bin;
    protected boolean inheritIO = false;

    public void setProxy(@Nullable Proxy proxy) {
        this.proxy = proxy;
    }

    public void setBin(String bin) {
        if (!Files.isDirectory(Path.of(bin))) {
            throw new IllegalArgumentException("not a directory");
        }
        this.bin = bin.endsWith("/") || bin.endsWith("\\") ? bin : bin + File.separator;
    }

    public void setInheritIO(boolean inheritIO) {
        this.inheritIO = inheritIO;
    }

    public static String readString(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        in.transferTo(out);
        return out.toString(StandardCharsets.UTF_8);
    }

    protected ProcessBuilder ffmpeg(String... commands) {
        boolean hasProxy = proxy != null;
        int destPos = hasProxy ? 6 : 4;
        String[] cmd = new String[commands.length + destPos];
        cmd[0] = bin != null ? bin + ffmpeg() : ffmpeg();
        cmd[1] = "-v";
        cmd[2] = "error";
        cmd[3] = "-y";
        if (hasProxy) {
            cmd[4] = "-http_proxy";
            cmd[5] = "http://" + proxy.host + ":" + proxy.port;
        }
        System.arraycopy(commands, 0, cmd, destPos, commands.length);
        return new ProcessBuilder(cmd);
    }

    protected ProcessBuilder ffprobe(String... commands) {
        boolean hasProxy = proxy != null;
        int destPos = hasProxy ? 5 : 3;
        String[] cmd = new String[commands.length + destPos];
        cmd[0] = bin != null ? bin + ffprobe() : ffprobe();
        cmd[1] = "-v";
        cmd[2] = "error";
        if (hasProxy) {
            cmd[3] = "-http_proxy";
            cmd[4] = "http://" + proxy.host + ":" + proxy.port;
        }
        System.arraycopy(commands, 0, cmd, destPos, commands.length);
        return new ProcessBuilder(cmd);
    }

    private String ffmpeg() {
        return isUnixSystem() ? "ffmpeg" : "ffmpeg.exe";
    }

    private String ffprobe() {
        return isUnixSystem() ? "ffprobe" : "ffprobe.exe";
    }

    private static boolean isUnixSystem() {
        return File.separatorChar == '/';
    }

    private static String getFileExtension(String file) {
        int i = file.lastIndexOf('.');
        if (i != -1 && i + 1 < file.length()) {
            return file.substring(i + 1);
        }
        return "";
    }
}
