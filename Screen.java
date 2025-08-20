import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;

public class Screen extends JFrame implements Runnable {
    
    private boolean running;
    private Thread thread;
    private BufferedImage image;
    private int[] pixels;
    private ArrayList<Entity> entities;
    private int backgroundColor;
    private int windowHeight;
    private int windowWidth;
    private boolean newEntityAdded;

    private long timeStartedNs;
    private long updateTimeTotalNs;
    private long renderTimeTotalNs;
    private long lastTimeNs;
    private int targetFps;
    private int lastDelayMs;
    private boolean debugPrintingEnabled;
    private double deltaTimeMs;
    private int totalFrames;

    private static final int DEFAULT_BACKGORUND_COLOR = 0x151525;
    private static final String DEFAULT_WINDOW_TITLE = "game window";
    public static final int DEFAULT_WINDOW_WIDTH = 800;
    public static final int DEFAULT_WINDOW_HEIGHT = 600;
    private static final int DEFAULT_TARGET_FPS = 60;

    public Screen() {
        this(DEFAULT_WINDOW_TITLE, DEFAULT_BACKGORUND_COLOR);
    }

    public Screen(String windowTitle, int backgroundColor) {
        this(windowTitle, backgroundColor, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
    }

    public Screen(String windowTitle, int backgroundColor, int windowWidth, int windowHeight) {
        setTitle(windowTitle);
        setSize(windowWidth, windowHeight);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        this.backgroundColor = backgroundColor;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        targetFps = DEFAULT_TARGET_FPS;
        debugPrintingEnabled = false;
        entities = new ArrayList<>();
        image = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    public synchronized void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        timeStartedNs = System.nanoTime();
        lastTimeNs = timeStartedNs;

        while(running) {
            update();
            render();
            try {
                Thread.sleep(getFrameDelay());
            } catch (IllegalArgumentException rushing) {}
            catch (InterruptedException interupt) {
                System.out.println("interupted in run loop");
                interupt.printStackTrace();
            }
        }
    }

    private void update() {
        handleDebugInfo();

        if (newEntityAdded) {
            synchronized(this) {
                Collections.sort(entities);
            }
            newEntityAdded = false;
        }

        synchronized (this) {
            for (Entity entity : entities) {
                entity.updatePosition();
            }
        }

        for (int checkingEntityIndex = 0; checkingEntityIndex < entities.size(); checkingEntityIndex++) {
            Entity checking = entities.get(checkingEntityIndex);
            if (!checking.collidesWithEntities()) {continue;}

            for (int checkedEntityIndex = checkingEntityIndex + 1; checkedEntityIndex < entities.size(); checkedEntityIndex++) {
                Entity checked = entities.get(checkedEntityIndex);
                if(checked.getCollisionLayer() > checking.getCollisionLayer()) {break;}

                if(!checked.collidesWithEntities() || !checking.collidesWith(checked)) {continue;}

                //System.out.println("collisonj");
                checking.resolveCollision(checked);
            }
        }


        updateTimeTotalNs += System.nanoTime() - lastTimeNs;
    }

    private void handleDebugInfo() {
        long currentTimeNs = System.nanoTime();

        deltaTimeMs = (currentTimeNs - lastTimeNs) / 1000000.0;

        lastTimeNs = currentTimeNs;

        if (!debugPrintingEnabled) {return;}

        totalFrames++;
        if (totalFrames % 100 == 0) {
            
            System.out.printf("fps: %.0f\naverage fps: %.1f\naverage update time per frame: %.2f ms\n" +
                    "average render time per frame: %.2f ms\n"
                    , 1000 / deltaTimeMs, totalFrames / ((currentTimeNs - timeStartedNs) / 1000000000.0),
                    (updateTimeTotalNs / 1000000.0) / totalFrames, (renderTimeTotalNs / 1000000.0) / totalFrames);
        }
    }

    private int getFrameDelay() {
        int frameComputeTimeMs = (int) deltaTimeMs - lastDelayMs;
        return lastDelayMs = (int) (1000.0 / targetFps) - frameComputeTimeMs;
    }

    private void render() {
        long startRenderTimeNs = System.nanoTime();


        BufferStrategy bs = getBufferStrategy();

        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        drawScreen();
        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

        bs.show();


        renderTimeTotalNs += System.nanoTime() - startRenderTimeNs;
    }

    private void drawScreen() {
        drawBackGround();
        drawEntities();
    }

    private void drawBackGround() {
        for (int i = 0; i < windowWidth * windowHeight; i++) {
            pixels[i] = backgroundColor;
        }
    }

    private synchronized void drawEntities() {
        // for (int i = 0; i < windowWidth * windowHeight; i++) {
        //     for (Entity entity : entities) {
        //         int color = entity.getColor();
        //         if (entity.covers(i)) {
        //             pixels[i] = color;
        //         }
        //     }
        // }
        for (Entity entity : entities) {
            int color = entity.getColor();
            for (int pixel : entity.getBoundingBoxPixels()) {
                // pixels[pixel] = 0xffffff;
                if (entity.covers(pixel)) {
                    pixels[pixel] = color;
                }
            }
        }
    }

    public void addEntity(Entity toAdd) {
        synchronized(this) {
            entities.add(toAdd);
            toAdd.setParent(this);
            newEntityAdded = true;
        }
    }

    public void enableDebugPrinting() {
        debugPrintingEnabled = true;
    }

    public void disableDebugPrinting() {
        debugPrintingEnabled = false;
    }

    public void setTargetFps(int targetFps) {
        this.targetFps = targetFps;
    }

    public double getDeltaTimeMs() {
        return deltaTimeMs;
    }
    
    public int getWindowHeight() {
        return windowHeight;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int color) {
        backgroundColor = color;
    }
}
