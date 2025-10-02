package se233.chapter3.controller;

import java.io.File;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ConversionTask extends Task<Void> {
    private final String inputPath;
    private final String outputPath;
    private final String format;
    private final String quality;
    private final Integer sampleRate;
    private final String channels;

    // FIELDS FOR TRACKING OVERALL PROGRESS
    private final DoubleProperty totalProgress;
    private final long totalDuration;
    private final AtomicLong currentProgress;
    private long lastReportedProgress = 0;

    public ConversionTask(String inputPath, String outputPath, String format, String quality, Integer sampleRate, String channels,
                          DoubleProperty totalProgress, long totalDuration, AtomicLong currentProgress) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.format = format;
        this.quality = quality;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.totalProgress = totalProgress;
        this.totalDuration = totalDuration;
        this.currentProgress = currentProgress;
    }


    @Override
    protected Void call() throws ConversionException {
        try {
            // --- ย้ายโค้ดทั้งหมดมาไว้ใน try-block ---
            FFmpeg ffmpeg = new FFmpeg();
            FFprobe ffprobe = new FFprobe();
            FFmpegProbeResult probeResult = ffprobe.probe(inputPath);

            String fileExtension = format.toLowerCase();
            int channelCount = channels.equalsIgnoreCase("Stereo") ? 2 : 1;

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(probeResult)
                    .overrideOutputFiles(true);

            FFmpegOutputBuilder outputBuilder = builder.addOutput(Paths.get(outputPath, getOutputFilename(inputPath, fileExtension)).toString())
                    .setFormat(fileExtension)
                    .setAudioChannels(channelCount)
                    .setAudioSampleRate(sampleRate);

            switch (this.format.toUpperCase()) {
                case "MP3":
                case "M4A":
                    int bitrate = Integer.parseInt(quality.replaceAll("[^0-9]", ""));
                    outputBuilder.setAudioBitRate(bitrate * 1000L);
                    outputBuilder.setAudioCodec(getCodecForFormat(this.format));
                    break;
                case "WAV":
                    if ("16-bit PCM".equals(quality)) {
                        outputBuilder.setAudioCodec("pcm_s16le");
                    } else if ("24-bit PCM".equals(quality)) {
                        outputBuilder.setAudioCodec("pcm_s24le");
                    }
                    break;
                case "FLAC":
                    int compressionLevel = Integer.parseInt(quality.replaceAll("[^0-9]", ""));
                    outputBuilder.addExtraArgs("-compression_level", String.valueOf(compressionLevel));
                    outputBuilder.setAudioCodec(getCodecForFormat(this.format));
                    break;
                default:
                    outputBuilder.setAudioCodec("copy");
                    break;
            }

            outputBuilder.done();

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            FFmpegJob job = executor.createJob(builder, new ProgressListener() {
                @Override
                public void progress(Progress progress) {
                    long progressDelta = progress.out_time_ns - lastReportedProgress;
                    currentProgress.addAndGet(progressDelta);
                    lastReportedProgress = progress.out_time_ns;
                    totalProgress.set((double) currentProgress.get() / totalDuration);
                }
            });

            job.run();

            long finalProgress = (long)(probeResult.getFormat().duration * TimeUnit.SECONDS.toNanos(1));
            long progressDelta = finalProgress - lastReportedProgress;
            currentProgress.addAndGet(progressDelta);
            totalProgress.set((double) currentProgress.get() / totalDuration);

            return null;

        } catch (IOException e) {
            // ดักจับ IOException ที่เกิดตอนอ่านไฟล์ หรือ khởi tạo FFmpeg
            throw new ConversionException("Error reading or accessing the file: " + new File(inputPath).getName(), e);
        } catch (Exception e) {
            // ดักจับ Exception อื่นๆ ที่อาจเกิดตอนแปลงไฟล์
            throw new ConversionException("An unexpected error occurred during conversion: " + new File(inputPath).getName(), e);
        }
    }

    private String getCodecForFormat(String format) {
        switch (format.toUpperCase()) {
            case "MP3": return "libmp3lame";
            case "M4A": return "aac";
            case "FLAC": return "flac";
            // เราไม่ต้องการ case "WAV" ที่นี่แล้ว เพราะจัดการใน switch หลักไปแล้ว
            default: return "copy";
        }
    }

    private String getOutputFilename(String input, String newExtension) {
        String baseName = Paths.get(input).getFileName().toString();
        int dotIndex = baseName.lastIndexOf('.');
        if (dotIndex != -1) {
            baseName = baseName.substring(0, dotIndex);
        }
        return baseName + "." + newExtension;
    }
}