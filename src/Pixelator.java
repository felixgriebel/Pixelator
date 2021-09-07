import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import javax.imageio.ImageIO;


public class Pixelator {
    private static boolean wantGrey=false;

    public static void main(String[] args) throws IOException {
        String path="null";
        System.out.println("Write a filename");
        Scanner si = new Scanner(System.in);
        while (!Files.isReadable(Path.of(path))){
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
                int[] rgb=new int[5];
                raster.getPixel(i, j, rgb);
                pixel[i][j]=(rgb[0]<<16)^(rgb[1]<<8)^(rgb[2]);
            }
        }



        System.out.println("h:"+height+" w:"+width);
        List<Integer> liste = new ArrayList<>();
        liste.add(1);

        for (int i=2;i<500;i++){
            if (height%i==0&&width%i==0){
                liste.add(i);
            }
        }

        System.out.println("possible dividors are: "+liste.toString()+", please pick a dividor");

        int div=0;

        while(!liste.contains(div)){
            Scanner sc = new Scanner(System.in);

            try{
                div = sc.nextInt();
            }catch(Exception e){
                div=0;
            }
        }


        System.out.println("New Dimensions will be -> h:"+(height/div)+" w:"+(width/div));

        int newHeight = height/div;
        int newWidth = width/div;


        System.out.println("You want the Image to be grey?[y/n]:");
        if (new Scanner(System.in).next().toLowerCase().equals("y"))wantGrey=true;

        //transform pixel[][] to pixel2[][]

        int[][] pixel2 = new int[newWidth][newHeight];

        System.out.println("press 1 to remain the same pixel size");
        String s = new Scanner(System.in).next();

        if (s.equals("1")){
            for (int y=0;y<height;y=y+div){
                for (int x=0;x<width;x=x+div){
                    getValueSameSize(pixel,div,x,y);
                }
            }
            pixel2=pixel;
        }else{
            for (int y=0;y<height;y=y+div){
                for (int x=0;x<width;x=x+div){
                    pixel2[x/div][y/div]=getValue(pixel,div,x,y);
                }
            }
        }




        //TODO colors in pixel[][]

        //width und height modifizieren
        BufferedImage theImage = new BufferedImage(pixel2.length, pixel2[0].length, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < pixel2.length; i++) {
            for (int j = 0; j < pixel2[0].length; j++) {

                theImage.setRGB(i, j, pixel2[i][j]);
            }
        }

        System.out.println("Please enter a name for the output file: ");
        String name = new Scanner(System.in).next();

        File outputfile = new File(name+".png");
        try {
            ImageIO.write(theImage, "png", outputfile);
        } catch (IOException e1) {

        }
    }

    private static int getValue(int[][] field, int size, int currX, int currY){
        long r=0;
        long g=0;
        long b=0;


        int count=size*size;
        for (int y=currY;y<currY+size;y++){
            for (int x= currX;x<currX+size;x++){
                b=b+(field[x][y]&255);
                g=g+((field[x][y]>>8)&255);
                r=r+((field[x][y]>>16)&255);
            }
        }
        r/=count;
        g/=count;
        b/=count;

        if (wantGrey){
            int valx = (int)((0.299*r) + (0.587*g) + (0.114*b));
            return(int) ((valx<<16)^(valx<<8)^(valx));
        }

        return(int) ((r<<16)^(g<<8)^(b));

    }

    private static void getValueSameSize(int[][] field, int size, int currX, int currY){
        long r=0;
        long g=0;
        long b=0;


        int count=size*size;
        for (int y=currY;y<currY+size;y++){
            for (int x= currX;x<currX+size;x++){
                b=b+(field[x][y]&255);
                g=g+((field[x][y]>>8)&255);
                r=r+((field[x][y]>>16)&255);
            }
        }
        r/=count;
        g/=count;
        b/=count;

        int theValue=0;
        if (wantGrey){
            int valx = (int)((0.299*r) + (0.587*g) + (0.114*b));
            theValue = (int) ((valx<<16)^(valx<<8)^(valx));
        }

        theValue = (int) ((r<<16)^(g<<8)^(b));


        for (int y=currY;y<currY+size;y++){
            for (int x= currX;x<currX+size;x++){
                field[x][y]=theValue;
            }
        }


    }
}