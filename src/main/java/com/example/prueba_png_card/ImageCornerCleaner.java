package com.example.prueba_png_card;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.zip.*;
import javax.imageio.ImageIO;
import java.awt.Color;

public class ImageCornerCleaner {

    @FunctionalInterface
    public interface ProgressCallback {
        void report(double progress);
    }

    public static void processZip(String zipInPath, String zipOutPath, int margin, int rT, int gT, int bT, ProgressCallback progressCallback) throws IOException {
        Path tempDir = Files.createTempDirectory("processed_images");

        // Contar total de entradas primero
        int total = 0;
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipInPath))) {
            while (zin.getNextEntry() != null) {
                total++;
            }
        }

        int[] count = {0};

        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipInPath))) {
            ZipEntry entry;

            while ((entry = zin.getNextEntry()) != null) {
                String name = entry.getName().toLowerCase();

                if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) {
                    BufferedImage image = ImageIO.read(zin);
                    if (image == null) continue;

                    BufferedImage processed = removeCornerWhites(image, margin, rT, gT, bT);

                    String outputName = Paths.get(entry.getName()).getFileName().toString();
                    outputName = outputName.replaceAll("\\.[^.]+$", "") + ".png";

                    // Asegurarse de que no se sobreescriban nombres duplicados
                    File outputFile = new File(tempDir.toFile(), outputName);
                    int index = 1;
                    while (outputFile.exists()) {
                        outputName = outputName.replace(".png", "_" + index + ".png");
                        outputFile = new File(tempDir.toFile(), outputName);
                        index++;
                    }

                    ImageIO.write(processed, "png", outputFile);
                }

                count[0]++;
                if (progressCallback != null) {
                    progressCallback.report((double) count[0] / total);
                }
            }
        }

        // Crear nuevo ZIP de salida
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipOutPath))) {
            Files.walk(tempDir)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            ZipEntry entry = new ZipEntry(path.getFileName().toString());
                            zout.putNextEntry(entry);
                            Files.copy(path, zout);
                            zout.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        // Eliminar archivos temporales
        deleteDirectory(tempDir.toFile());
        System.out.println("🎉 ZIP generado: " + zipOutPath);
    }

    private static BufferedImage removeCornerWhites(BufferedImage image, int margin, int rT, int gT, int bT) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        newImage.getGraphics().drawImage(image, 0, 0, null);

        for (int y = 0; y < margin; y++) {
            for (int x = 0; x < margin; x++) {
                clearIfWhite(newImage, x, y, rT, gT, bT);                           // top-left
                clearIfWhite(newImage, width - 1 - x, y, rT, gT, bT);              // top-right
                clearIfWhite(newImage, x, height - 1 - y, rT, gT, bT);             // bottom-left
                clearIfWhite(newImage, width - 1 - x, height - 1 - y, rT, gT, bT); // bottom-right
            }
        }

        return newImage;
    }

    private static void clearIfWhite(BufferedImage image, int x, int y, int rT, int gT, int bT) {
        int color = image.getRGB(x, y);
        Color c = new Color(color, true);
        if (c.getRed() > rT && c.getGreen() > gT && c.getBlue() > bT) {
            image.setRGB(x, y, 0x00FFFFFF); // transparente
        }
    }

    private static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                deleteDirectory(file);
            }
        }
        dir.delete();
    }
}
