package io.github.edsuns.common.ffmpeg;

import java.io.IOException;

/**
 * @author edsuns@qq.com
 * @since 2024/1/20 17:18
 */
public class FFMPEGForVideo extends FFMPEG {

    public double getDurationSeconds(String url) throws IOException, InterruptedException {
        ProcessBuilder command = ffprobe("-select_streams", "v:0",
                "-show_entries", "stream=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                url);
        if (inheritIO) {
            command.inheritIO();
        }
        Process process = command.start();
        process.waitFor();
        String err = readString(process.getErrorStream());
        if (!err.isBlank()) {
            throw new IllegalStateException(err);
        }
        return Double.parseDouble(readString(process.getInputStream()));
    }

    public Process download(String url, String outFile) throws IOException {
        ProcessBuilder command = ffmpeg("-i", url, "-c", "copy", outFile);
        if (inheritIO) {
            command.inheritIO();
        }
        return command.start();
    }
}
