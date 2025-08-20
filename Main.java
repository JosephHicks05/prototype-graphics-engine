import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Screen screen = new Screen("example", 0x000033, 800, 600);

        Random rand = new Random();
        rand.setSeed(0);

        screen.start();

        
        for (int i = 0; i < 200; i++) {
            Rectangle toAdd = new Rectangle(rand.nextInt(20, 200), rand.nextInt(20, 200),
                    rand.nextInt(20, 40), rand.nextInt(20, 40), 
                    rand.nextInt(600), rand.nextInt(600), rand.nextInt(0xffffff), 3);
            toAdd.setCollidesWithScreenBorders(true);
            //toAdd.setCollidesWithEntities(true);
            screen.addEntity(toAdd);
        }
        
        for (int i = 0; i < 1500; i++) {
            Circle toAdd = new Circle(rand.nextInt(5, 15), rand.nextInt(20, 400), rand.nextInt(20, 400),
                    rand.nextInt(600), rand.nextInt(600), rand.nextInt(0xffffff), 1);
            toAdd.setCollidesWithScreenBorders(true);
            //toAdd.setCollidesWithEntities(true);
            screen.addEntity(toAdd);
        }

        Circle toAdd = new Circle(50, 100, 100, 100, 100, 0x000000, 5);
        toAdd.setCollidesWithScreenBorders(true);
        screen.addEntity(toAdd);


        screen.enableDebugPrinting();
        screen.setTargetFps(60);
    }
}
