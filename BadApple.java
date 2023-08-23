import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BadApple {

    public static void main(String[] args) throws IOException {
        String badFramesPath = "badframes/";
        String framesPath = "frames/";
        List<BufferedImage> fadingImages = loadFadingImages(framesPath);

        File[] badFramesFiles = new File(badFramesPath).listFiles();

        int[][] fadingIndexes = new int[48][36];

        for (File frameFile : badFramesFiles) {
            BufferedImage badFrame = ImageIO.read(new File(frameFile.getAbsolutePath()));
            List<Point> whiteCoordinates = new ArrayList<>();

            for (int x = 8; x <= 55; x++) {
                for (int y = 14; y <= 49; y++) {
                    Color pixelColor = new Color(badFrame.getRGB(x, y));
                    Color targetColor = new Color(253, 253, 253);

                    if (isCloseToWhite(pixelColor, targetColor)) {
                        whiteCoordinates.add(new Point(x-8, y-14));
                    }
                }
            }

            BufferedImage newFrame = createNewFrame(48 * 34, 36 * 34);

            for (int x = 0; x < 48; x++) {
                for (int y = 0; y < 36; y++) {
                    int scaledX = x * 34;
                    int scaledY = y * 34;

                    Point p = new Point(x, y);
                    if (whiteCoordinates.contains(p)) {
                        fadingIndexes[x][y] = 1;
                    } else if (fadingIndexes[x][y] > 0) {
                        fadingIndexes[x][y]++;
                        if (fadingIndexes[x][y] >= 14) {
                            fadingIndexes[x][y] = 0;
                        }
                    }

                    if (fadingIndexes[x][y] > 0) {
                        BufferedImage fadingImage = fadingImages.get(fadingIndexes[x][y]);
                        newFrame.createGraphics().drawImage(fadingImage, scaledX, scaledY, null);
                    }
                }
            }

            String resultFileName = "result_" + frameFile.getName();
            ImageIO.write(newFrame, "png", new File(resultFileName));
            System.out.println("Created " + resultFileName);
        }
    }

    private static List<BufferedImage> loadFadingImages(String framesPath) {
        List<BufferedImage> fadingImages = new ArrayList<>();
        for (int i = 1; i <= 14; i++) {
            String fileName = "frames/" + i + ".png";
            try {
                fadingImages.add(ImageIO.read(new File(fileName)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fadingImages;
    }

    private static boolean isCloseToWhite(Color color, Color targetColor) {
        int redDiff = Math.abs(color.getRed() - targetColor.getRed());
        int greenDiff = Math.abs(color.getGreen() - targetColor.getGreen());
        int blueDiff = Math.abs(color.getBlue() - targetColor.getBlue());

        return redDiff < 3 && greenDiff < 3 && blueDiff < 3;
    }

    private static BufferedImage createNewFrame(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }
}
