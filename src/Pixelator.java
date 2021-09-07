import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Pixelator {
    private static boolean wantGrey = false;
    private static int filter = 0;

    public static void main(String[] args) throws IOException {
        String path = "null";
        System.out.println("Write a filename");
        Scanner si = new Scanner(System.in);
        while (!Files.isReadable(Path.of(path))) {
            path = si.next();
        }


        File file = new File(path);


        BufferedImage img = ImageIO.read(file);
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] pixel = new int[width][height];
        Raster raster = img.getData();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int[] rgb = new int[5];
                raster.getPixel(i, j, rgb);
                pixel[i][j] = (rgb[0] << 16) ^ (rgb[1] << 8) ^ (rgb[2]);
            }
        }


        System.out.println("h:" + height + " w:" + width);
        List<Integer> liste = new ArrayList<>();
        liste.add(1);

        for (int i = 2; i < 500; i++) {
            if (height % i == 0 && width % i == 0) {
                liste.add(i);
            }
        }

        System.out.println("possible dividors are: " + liste + ", please pick a dividor");

        int div = 0;

        while (!liste.contains(div)) {
            Scanner sc = new Scanner(System.in);

            try {
                div = sc.nextInt();
            } catch (Exception e) {
                div = 0;
            }
        }


        System.out.println("New Dimensions will be -> h:" + (height / div) + " w:" + (width / div));

        int newHeight = height / div;
        int newWidth = width / div;


        System.out.println("You want the Image to be grey?[y/n]:");
        if (new Scanner(System.in).next().equalsIgnoreCase("y")) wantGrey = true;

        //transform pixel[][] to pixel2[][]

        int[][] pixel2 = new int[newWidth][newHeight];

        System.out.println("press a number to add a filter");
        String fil = new Scanner(System.in).next();
        switch (fil) {
            case "1" -> {
                filter = 1;
            }
            case "2" -> {
                filter = 2;
            }
            case "3" -> {
                filter = 3;
            }
        }

        System.out.println("Do you want to remain the same pixel size?[y/n]");
        String s = new Scanner(System.in).next().toLowerCase();

        if (s.equals("y")) {
            for (int y = 0; y < height; y = y + div) {
                for (int x = 0; x < width; x = x + div) {
                    getValueSameSize(pixel, div, x, y);
                }
            }
            pixel2 = pixel;
        } else {
            for (int y = 0; y < height; y = y + div) {
                for (int x = 0; x < width; x = x + div) {
                    pixel2[x / div][y / div] = getValue(pixel, div, x, y);
                }
            }
        }


        //width und height modifizieren
        BufferedImage theImage = new BufferedImage(pixel2.length, pixel2[0].length, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < pixel2.length; i++) {
            for (int j = 0; j < pixel2[0].length; j++) {

                theImage.setRGB(i, j, pixel2[i][j]);
            }
        }

        System.out.println("Please enter a name for the output file: ");
        String name = new Scanner(System.in).next();

        File outputfile = new File(name + ".png");
        try {
            ImageIO.write(theImage, "png", outputfile);
        } catch (IOException e1) {

        }
    }

    private static int getValue(int[][] field, int size, int currX, int currY) {
        long r = 0;
        long g = 0;
        long b = 0;


        int count = size * size;
        for (int y = currY; y < currY + size; y++) {
            for (int x = currX; x < currX + size; x++) {
                b = b + (field[x][y] & 255);
                g = g + ((field[x][y] >> 8) & 255);
                r = r + ((field[x][y] >> 16) & 255);
            }
        }
        r /= count;
        g /= count;
        b /= count;

        if (wantGrey) {
            int valx = (int) ((0.299 * r) + (0.587 * g) + (0.114 * b));
            return (valx << 16) ^ (valx << 8) ^ (valx);
        }

        return (int) ((r << 16) ^ (g << 8) ^ (b));

    }

    private static void getValueSameSize(int[][] field, int size, int currX, int currY) {
        long r = 0;
        long g = 0;
        long b = 0;


        int count = size * size;
        for (int y = currY; y < currY + size; y++) {
            for (int x = currX; x < currX + size; x++) {
                b = b + (field[x][y] & 255);
                g = g + ((field[x][y] >> 8) & 255);
                r = r + ((field[x][y] >> 16) & 255);
            }
        }
        r /= count;
        g /= count;
        b /= count;

        int theValue = 0;
        if (wantGrey) {
            int valx = (int) ((0.299 * r) + (0.587 * g) + (0.114 * b));
            theValue = (valx << 16) ^ (valx << 8) ^ (valx);

        } else {

            if (filter == 1) {
                if ((currX + (currY * 7)) % 3 == 0) {
                    r = (r + (currY * 5) + (currX * 2)) % 256;
                }
                if ((currX + (currY * 7)) % 3 == 1) {
                    g = (g + (currY * 5) + (currX * 2)) % 256;
                }
                if ((currX + (currY * 7)) % 3 == 2) {
                    b = (b + (currY * 5) + (currX * 2)) % 256;
                }
            }
            if (filter == 2) {
                while (r != 255 && g != 255 && b != 255) {
                    r++;
                    g++;
                    b++;
                }
            }

            if (filter == 3) {
                if ((currX + (currY * 5)) % 3 == 0) {
                    r = (r + (currY * 7) - (currX * 9)) % 256;
                }
                if ((currX + (currY * 5)) % 3 == 1) {
                    g = (g + (currY * 7) - (currX * 9)) % 256;
                }
                if ((currX + (currY * 5)) % 3 == 2) {
                    b = (b + (currY * 7) - (currX * 9)) % 256;
                }
            }

            theValue = (int) ((r << 16) ^ (g << 8) ^ (b));
        }


        for (int y = currY; y < currY + size; y++) {
            for (int x = currX; x < currX + size; x++) {
                field[x][y] = theValue;
            }
        }


    }
}