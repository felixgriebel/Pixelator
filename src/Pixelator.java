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
    //private static boolean wantGrey = false;
    private static int filter = 0;

    public static void main(String[] args) throws IOException {
        transformPic();
    }


    public static void transformPic() throws IOException {
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


        //transform pixel[][] to pixel2[][]

        int[][] pixel2 = new int[newWidth][newHeight];

        System.out.println("press a number to add a filter");
        String fil = new Scanner(System.in).next();
        switch (fil) {
            case "1" -> filter = 1;
            case "2" -> filter = 2;
            case "3" -> filter = 3;
            case "4" -> filter = 4;
            case "5" -> filter = 5;
            case "6" -> filter = 6;
            case "7" -> filter = 7;
        }



        if (filter!=5) {
            for (int y = 0; y < height; y = y + div) {
                for (int x = 0; x < width; x = x + div) {
                    getValueSameSize(pixel, div, x, y);
                }
            }
            pixel2 = pixel;


            System.out.println("Change row?");
            try{
                int ent = new Scanner(System.in).nextInt();
                switch(ent){
                    case 0 ->{
                        Scanner sin = new Scanner(System.in);

                        int start=0;
                        int end=0;
                        System.out.println("from:");
                        start=sin.nextInt();
                        System.out.println("to (excluded):");
                        end=sin.nextInt();
                        switchArrDis(pixel2,start,end);

                    }
                    case 1 ->{
                        switchArr(pixel2);
                    }
                    case 2 ->{
                        switchArr2(pixel2);
                    }
                    case 3 ->{
                        switchArr3(pixel2);
                    }
                    case 4 ->{
                        switchArr4(pixel2);
                    }
                    case 5 ->{
                        switchArr5(pixel2);
                    }
                    case 6 ->{
                        switchArrVert(pixel2);
                    }
                }
            }catch(Exception ignored){

            }


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

        File outputfile = new File( "output.png");
        try {
            ImageIO.write(theImage, "png", outputfile);
        } catch (IOException ignored) {
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

        if (filter==4) {
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

        int theValue;
        if (filter==4) {
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

            if(filter==6){
                if (r>g&&r>b)r=0;
            }
            if (filter == 7) {
                if ((currX + (currY * 13)) % 3 == 0) {
                    r = (r + (currY * 21) - (currX * 5)) % 256;
                }
                if ((currX + (currY * 103)) % 3 == 1) {
                    g = (g + (currY * 51) - (currX * 3)) % 256;
                }
                if ((currX + (currY * 21)) % 3 == 2) {
                    b = (b + (currY * 23) - (currX * 2)) % 256;
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

    public static void createRandom() {
        Scanner sc = new Scanner(System.in);
        int height = 0;
        while (height == 0) {
            System.out.println("Please input the height:");
            try {
                height = sc.nextInt();
                if (height < 0) height = 0;
            } catch (Exception ignored) {
            }
        }

        int width = 0;
        while (width == 0) {
            System.out.println("Please input the width:");
            try {
                width = sc.nextInt();
                if (width < 0) width = 0;
            } catch (Exception ignored) {
            }
        }

        int r = -1;
        while (r == -1) {
            System.out.println("r:");
            try {
                r = sc.nextInt();
                if (r > 255 || r < 0) r = -1;
            } catch (Exception ignored) {
            }
        }

        int g = -1;
        while (g == -1) {
            System.out.println("g:");
            try {
                g = sc.nextInt();
                if (g > 255 || g < 0) g = -1;
            } catch (Exception ignored) {
            }
        }

        int b = -1;
        while (b == -1) {
            System.out.println("b:");
            try {
                b = sc.nextInt();
                if (b > 255 || b < 0) b = -1;
            } catch (Exception ignored) {
            }
        }

        int[][] field = new int[width][height];


        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[0].length; y++) {
                int red = (int) (Math.random() * r);
                int green = (int) (Math.random() * g);
                int blue = (int) (Math.random() * b);

                field[x][y] = (red << 16) ^ (green << 8) ^ (blue);
            }
        }


        BufferedImage theImage = new BufferedImage(field.length, field[0].length, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {

                theImage.setRGB(i, j, field[i][j]);
            }
        }


        File outputfile = new File("out.png");
        try {
            ImageIO.write(theImage, "png", outputfile);
        } catch (IOException ignored) {
        }
    }
    public static void createRainbow() {
        Scanner sc = new Scanner(System.in);
        int height = 0;
        while (height == 0) {
            System.out.println("Please input the height:");
            try {
                height = sc.nextInt();
                if (height < 0) height = 0;
            } catch (Exception ignored) {
            }
        }

        int width = 0;
        while (width == 0) {
            System.out.println("Please input the width:");
            try {
                width = sc.nextInt();
                if (width < 0) width = 0;
            } catch (Exception ignored) {
            }
        }


        int[][] field = new int[width][height];


        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[0].length; y++) {

                switch((x*("df"+y+"sd"+y).hashCode()%11)){
                    case 0 ->{
                        field[x][y]=(255<<16)^(0<<8)^(0);
                    }
                    case 1 ->{
                        field[x][y]=(234<<16)^(68<<8)^(17);
                    }
                    case 2 ->{
                        field[x][y]=(238<<16)^(138<<8)^(1);
                    }
                    case 3 ->{//gelb
                        field[x][y]=(255<<16)^(255<<8)^(0);
                    }
                    case 4 ->{
                        field[x][y]=(138<<16)^(250<<8)^(88);
                    }
                    case 5 ->{//green
                        field[x][y]=(42<<16)^(176<<8)^(40);
                    }
                    case 6 ->{
                        field[x][y]=(1<<16)^(255<<8)^(251);
                    }
                    case 7 ->{
                        field[x][y]=(64<<16)^(163<<8)^(250);
                    }
                    case 8 ->{//blue
                        field[x][y]=(5<<16)^(40<<8)^(126);
                    }
                    case 9 ->{
                        field[x][y]=(51<<16)^(0<<8)^(134);
                    }
                    case 10 ->{
                        field[x][y]=(255<<16)^(0<<8)^(255);
                    }
                }
            }
        }


        BufferedImage theImage = new BufferedImage(field.length, field[0].length, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {

                theImage.setRGB(i, j, field[i][j]);
            }
        }


        File outputfile = new File("out.png");
        try {
            ImageIO.write(theImage, "png", outputfile);
        } catch (IOException ignored) {
        }
    }





    /** SWITCH ARRAY METHODS WITH DIFFERENT WIDTH
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * **/
    private static void switchArr(int[][] field) {
        for (int x = 0; x < field.length; x++) {
            if (x % 2 == 0) {
                int[] temp = new int[field[x].length];

                for (int y = 0; y < field[x].length; y++) {
                    temp[(field[x].length - 1) - y] = field[x][y];
                }
                field[x] = temp;
            }
        }
    }

    private static void switchArr2(int[][] field) {
        for (int x = 0; x < field.length; x++) {
            if (x % 4==0||x % 4==1) {
                int[] temp = new int[field[x].length];

                for (int y = 0; y < field[x].length; y++) {
                    temp[(field[x].length - 1) - y] = field[x][y];
                }
                field[x] = temp;
            }
        }
    }
    private static void switchArr3(int[][] field) {
        for (int x = 0; x < field.length; x++) {
            if (x % 6==0||x % 6==1||x % 6==2) {
                int[] temp = new int[field[x].length];

                for (int y = 0; y < field[x].length; y++) {
                    temp[(field[x].length - 1) - y] = field[x][y];
                }
                field[x] = temp;
            }
        }
    }
    private static void switchArr4(int[][] field) {
        for (int x = 0; x < field.length; x++) {
            if (x % 8==0||x % 8==1||x % 8==2||x % 8==3) {
                int[] temp = new int[field[x].length];

                for (int y = 0; y < field[x].length; y++) {
                    temp[(field[x].length - 1) - y] = field[x][y];
                }
                field[x] = temp;
            }
        }
    }
    private static void switchArr5(int[][] field) {
        for (int x = 0; x < field.length; x++) {
            if (x % 10==0||x % 10==1||x % 10==2||x % 10==3||x % 10==4) {
                int[] temp = new int[field[x].length];

                for (int y = 0; y < field[x].length; y++) {
                    temp[(field[x].length - 1) - y] = field[x][y];
                }
                field[x] = temp;
            }
        }
    }

    private static void switchArrDis(int[][] field, int start, int end) {
        for (int x = start; x < end; x++) {
                int[] temp = new int[field[x].length];

                for (int y = 0; y < field[x].length; y++) {
                    temp[(field[x].length - 1) - y] = field[x][y];
                }
                field[x] = temp;

        }
    }

    private static void switchArrVert(int[][] field) {
        System.out.println(field[0][0]);
        for (int y = 0; y < field[0].length; y++) {
            if (y % 2 == 0) {



                for (int x = 0; x <= field.length/2; x++) {
                    int temp=field[x][y];
                    field[x][y]=field[(field.length-1)-x][y];
                    field[(field.length-1)-x][y]=temp;

                }

            }
        }
        System.out.println(field[0][0]);
    }
}