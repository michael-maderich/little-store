package com.littlestore.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public class ImageUtils {

    /**
     * Returns true if the uploaded image file contains any pixel with alpha < 255.
     */
    public static boolean hasTransparency(MultipartFile file) throws IOException {
        // 1) Read the upload into a BufferedImage
        BufferedImage img = ImageIO.read(file.getInputStream());
        if (img == null) {
            throw new IOException("Failed to decode image (unsupported format or corrupted).");
        }

        // 2) Quick check: if it doesn't support alpha, it can’t have transparency
        if (!img.getColorModel().hasAlpha()) {
            return false;
        }

        // 3) Scan pixels (or a subsampled set) for any alpha < 255
        final int width  = img.getWidth();
        final int height = img.getHeight();

        // If the image is very large, you might sample every Nth pixel instead of every one
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = img.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;
                if (alpha < 255) {
                    return true;  // found a transparent (or semi‐transparent) pixel
                }
            }
        }

        return false;  // no transparent pixels found
    }
}
