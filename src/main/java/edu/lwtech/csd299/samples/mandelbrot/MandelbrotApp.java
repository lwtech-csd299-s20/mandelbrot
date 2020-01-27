package edu.lwtech.csd299.samples.mandelbrot;

import java.awt.Color;
import edu.princeton.cs.introcs.*;

public class MandelbrotApp {

    static final int CANVAS_SIZE = 500;

    static final int PLUS_SIGN = 61;
    static final int MINUS_SIGN = 45;

    static final int UP_ARROW = 38;
    static final int DOWN_ARROW = 40;
    static final int RIGHT_ARROW = 39;
    static final int LEFT_ARROW = 37;

    static final double SHIFT_PERCENTAGE = 0.10;

    static double zoomFactor = 5.0;

    static double viewportX = -0.5;
    static double viewportY = 0.0;
    static double viewportSize = 2.0;

    //TODO: improve name of pic?
    static Picture pic = new Picture(CANVAS_SIZE, CANVAS_SIZE);
    static int numRedraws = 0;

    // ----------------------------------------------------------------
    public static void main(String[] args) {

        StdDraw.setCanvasSize(CANVAS_SIZE, CANVAS_SIZE);
        drawMandelbrotView(viewportX, viewportY, viewportSize);

        savePic();

        while (true) {

            // Draw the image that drawMandelbrotView() just created and stored on the hard disk.
            //   (stdlib library requires images to come in from the hard disk.)
            loadPic();

            // Draw zoom box
            drawZoomBox();

            // Was the mouse clicked?
            if (StdDraw.mousePressed()) {
                // Yes, zoom in and redraw
                zoomViewport();
                drawMandelbrotView(viewportX, viewportY, viewportSize);
                savePic();
            }

            // Check for "+" and "-" and zoom accordingly
            if (StdDraw.isKeyPressed(PLUS_SIGN)) {
                zoomFactor = zoomIn(zoomFactor);
            }
            if (StdDraw.isKeyPressed(MINUS_SIGN)) {
                zoomFactor = zoomOut(zoomFactor);
            }

            // Check for movement keys and move viewport accordingly
            if (StdDraw.isKeyPressed(UP_ARROW)) {
                viewportY += (viewportSize * SHIFT_PERCENTAGE);
                // Redraw the picture
                drawMandelbrotView(viewportX, viewportY, viewportSize);
                savePic();
            } else if (StdDraw.isKeyPressed(DOWN_ARROW)) {
                viewportY -= (viewportSize * SHIFT_PERCENTAGE);
                // Redraw the picture
                drawMandelbrotView(viewportX, viewportY, viewportSize);
                savePic();
            } else if (StdDraw.isKeyPressed(RIGHT_ARROW)) {
                viewportX += (viewportSize * SHIFT_PERCENTAGE);
                // Redraw the picture
                drawMandelbrotView(viewportX, viewportY, viewportSize);
                savePic();
            } else if (StdDraw.isKeyPressed(LEFT_ARROW)) {
                viewportX -= (viewportSize * SHIFT_PERCENTAGE);
                // Redraw the picture
                drawMandelbrotView(viewportX, viewportY, viewportSize);
                savePic();
            }

            StdDraw.show(50);
        }
    }

    private static int mandelbrotIterationCount(Complex zInitial, int max) {

        // Do the traditional Mandelbrot calculation:
        //  i.e., Compute up to "max" moves in 3-D space and see if any of them
        //  move outside of our given radius (2.0).  Try "max" times, then give up.
        Complex z = zInitial;
        for (int turn = max; turn > 0; turn--) {
            if (z.abs() > 2.0) {
                return turn;
            }
            z = z.times(z).plus(zInitial);
        }
        return 0;
    }
    
    private static void savePic() {
        String tempDir = System.getProperty("java.io.tmpdir");
        if (!tempDir.endsWith("/")) tempDir += "/";
        
        String filename = tempDir + "pic" + numRedraws + ".jpg";
        System.out.println("Saving " + filename);        
        pic.save(filename);
    }
    
    private static void loadPic() {
        String tempDir = System.getProperty("java.io.tmpdir");
        if (!tempDir.endsWith("/")) tempDir += "/";
        StdDraw.picture(0.5, 0.5, tempDir + "pic" + numRedraws + ".jpg");
    }

    private static void drawMandelbrotView(double viewportX, double viewportY, double viewportSize) {

        for (int i = 0; i < CANVAS_SIZE; i++) {
            for (int j = 0; j < CANVAS_SIZE; j++) {

                double x = viewportX - (viewportSize / 2) + ((viewportSize * i) / CANVAS_SIZE);
                double y = viewportY - (viewportSize / 2) + ((viewportSize * j) / CANVAS_SIZE);
                Complex z = new Complex(x, y);

                Color c = colorizerRainbow(mandelbrotIterationCount(z, 255));
                pic.set(i, (CANVAS_SIZE - 1) - j, c);
            }
        }
        
        // Increment redraw counter to insure filename uniqueness
        numRedraws++;       
    }

    private static void drawZoomBox() {
        StdDraw.setPenColor(0, 200, 0);
        StdDraw.setPenRadius(0.01);
        StdDraw.rectangle(StdDraw.mouseX(), StdDraw.mouseY(), 1 / (2 * zoomFactor), 1 / (2 * zoomFactor));
    }

    private static void zoomViewport() {
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();
        viewportX = scaled(x, viewportX - (viewportSize / 2), viewportX + (viewportSize / 2));
        viewportY = scaled(y, viewportY - (viewportSize / 2), viewportY + (viewportSize / 2));
        viewportSize = viewportSize / zoomFactor;
    }

    private static double scaled(double x, double start, double end) {
        double range = end - start;
        double scaledX = range * x;
        return start + scaledX;
    }

    public static double zoomIn(double zoomFactor) {
        return zoomFactor * 1.1;
    }

    public static double zoomOut(double zoomFactor) {
        if (zoomFactor > 1.5) {
            return zoomFactor * 0.9;
        } else {
            return zoomFactor;
        }
    }

    // =============================================================
    // Coloring Algorithms

    // private static Color colorizerSimple(int t) {
    //     return new Color(t, t, t);
    // }

    // private static Color colorizerBeetleJuice(int t) {
    //     Color colorOut;

    //     if (t % 2 == 0) {
    //         colorOut = new Color(t, t, t);
    //     } else {
    //         colorOut = new Color(255 - t, 255 - t, 255 - t);
    //     }
    //     return colorOut;
    // }

    private static Color colorizerRainbow(int t) {
        Color colorOut;

        if (t % 7 == 0) {
            colorOut = StdDraw.BLACK;
        } else if (t % 6 == 0) {
            colorOut = StdDraw.BLUE;
        } else if (t % 5 == 0) {
            colorOut = StdDraw.GREEN;
        } else if (t % 4 == 0) {
            colorOut = StdDraw.YELLOW;
        } else if (t % 3 == 0) {
            colorOut = StdDraw.ORANGE;
        } else if (t % 2 == 0) {
            colorOut = StdDraw.RED;
        } else {
            colorOut = StdDraw.MAGENTA;
        }

        return colorOut;
    }

}
