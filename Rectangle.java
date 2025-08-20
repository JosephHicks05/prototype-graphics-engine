public class Rectangle implements Entity {

    private double topLeftX;
    private double topLeftY;
    private int width;
    private int height;
    private int dxPxPerFrame;
    private int dyPxPerFrame;
    private boolean collidesWithEntities;
    private boolean collidesWithScreenBorders;
    private int collisionLayer;

    private int color;
    private int renderLayer;
    private Screen parent;

    private static final int DEFAULT_COLOR = 0xffff00;

    public Rectangle(int topLeftX, int topLeftY, int width, int height) { //TODO rearrange constructors to have width and height first, most important
        this(topLeftX, topLeftY, width, height, 0, 0);
    }

    public Rectangle(int topLeftX, int topLeftY, int width, int height, int dxPxPerFrame, int dyPxPerFrame) {
        this(topLeftX, topLeftY, width, height, dyPxPerFrame, dxPxPerFrame, DEFAULT_COLOR, 0);
    }

    public Rectangle(int topLeftX, int topLeftY, int width, int height, int dxPxPerFrame, int dyPxPerFrame, int color, int renderLayer) {
        if (width < 1) {
            throw new IllegalArgumentException("rectangles must have a width of at least 1.");
        }
        if (height < 1) {
            throw new IllegalArgumentException("rectangles must have a height of at least 1.");
        }
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.width = width;
        this.height = height;
        this.dxPxPerFrame = dxPxPerFrame;
        this.dyPxPerFrame = dyPxPerFrame;
        this.color = color;
        this.renderLayer = renderLayer;
    }

    public void updatePosition() {
        double deltaTimeSec = parent.getDeltaTimeMs() / 1000.0;

        topLeftX += dxPxPerFrame * deltaTimeSec;
        topLeftY += dyPxPerFrame * deltaTimeSec;

        if (collidesWithScreenBorders) {
            checkScreenBorderCollisions(parent.getWindowWidth(), parent.getWindowHeight());
        }
    }

    private void checkScreenBorderCollisions(int windowWidth, int windowHeight) {
        int topLeftXInt = (int) topLeftX;
        int topLeftYInt = (int) topLeftY;

        if (topLeftXInt + width > windowWidth) {
            dxPxPerFrame = -Math.abs(dxPxPerFrame);
        }
        else if (topLeftXInt < 0) {
            dxPxPerFrame = Math.abs(dxPxPerFrame);
        }
        if (topLeftYInt + height > windowHeight) {
            dyPxPerFrame = -Math.abs(dyPxPerFrame);
        }
        else if (topLeftYInt < 0) {
            dyPxPerFrame = Math.abs(dyPxPerFrame);
        }
    }

    public boolean collidesWith(Entity other) {
        if (other instanceof Rectangle) {
            Rectangle otherRectangle = (Rectangle) other;

            return  getTopLeftX() < otherRectangle.getTopLeftX() + otherRectangle.width &&
                    getTopLeftX() + width > otherRectangle.getTopLeftX() &&
                    getTopLeftY() < otherRectangle.getTopLeftY() + otherRectangle.height &&
                    getTopLeftY() + height > otherRectangle.getTopLeftY();
        }
        else if (other instanceof Circle) {
            return ((Circle) other).collidesWith(this);
        }
        else {
            throw new IllegalArgumentException("the rectangle class doesn't know how to check for a collision with some type of entity.");
        }
    }

    public void resolveCollision(Entity other) {
        if (other instanceof Rectangle) {
            Rectangle otherRectangle = (Rectangle) other;
            // TODO implement

            
        }
        else if (other instanceof Circle) {
            ((Circle) other).resolveCollision(this);
        }
        else {
            throw new IllegalArgumentException("the rectangle class doesn't know how to resolve a collision with some type of entity.");
        }
    }

    public boolean covers(int pixel) {
        return true;
    }

    public int[] getBoundingBoxPixels() {
        int[] boundingBoxPixels = new int[width * height];

        int windowWidth = parent.getWindowWidth();
        int windowHeight = parent.getWindowHeight();
        int topLeftXInt = (int) topLeftX;
        int topLeftYInt = (int) topLeftY;

        for (int row = topLeftXInt; row < topLeftXInt + width; row++) {
            for (int col = topLeftYInt; col < topLeftYInt + height; col++) {
                int pixel = col * windowWidth + row;
                boolean colBound = col >= 0 && col < windowHeight;
                boolean rowBound = row >= 0 && row < windowWidth;
                if (pixel >= 0 && pixel < windowHeight * windowWidth && colBound && rowBound) {
                    boundingBoxPixels[(col - (topLeftYInt)) + ((row - (topLeftXInt)) * height)]
                    = col * windowWidth + row;
                }
            }
        }
        return boundingBoxPixels;
    }

    public int getRenderLayer() {
        return renderLayer;
    }

    public int getCollisionLayer() {
        return collisionLayer;
    }

    public int getTopLeftX() {
        return (int) topLeftX;
    }

    public int getTopLeftY() {
        return (int) topLeftY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDxPxPerFrame() {
        return dxPxPerFrame;
    }

    public int getDyPxPerFrame() {
        return dyPxPerFrame;
    }

    public boolean collidesWithEntities() {
        return collidesWithEntities;
    }

    public boolean collidesWithScreenBorders() {
        return collidesWithScreenBorders;
    }

    public int getColor() {
        return color;
    }

    public void setParent(Screen parent) {
        this.parent = parent;
    }

    public void setTopLeftX(int topLeftX) {
        this.topLeftX = topLeftX;
    }

    public void setTopLeftY(int topLeftY) {
        this.topLeftY = topLeftY;
    }

    public void setDxPxPerFrame(int dx) {
        this.dxPxPerFrame = dx;
    }

    public void setDyPxPerFrame(int dy) {
        this.dyPxPerFrame = dy;
    }

    public void setCollidesWithScreenBorders(boolean collidesWithScreenBorders) {
        this.collidesWithScreenBorders = collidesWithScreenBorders;
    }

    public void setCollidesWithEntities(boolean collidesWithEntities) {
        this.collidesWithEntities = collidesWithEntities;
    }
    
    public void setColor(int color) {
        this.color = color;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Rectangle)) {return false;}

        Rectangle otherRectangle = (Rectangle) other;

        return otherRectangle.height == height && otherRectangle.width == width
                && otherRectangle.color == color;
    }

    public String toString() {
        return String.format("Rectangle with width: %d, height: %d, top left x: %d, top left y: %d" +
                "dx: %d, dy: %d, and color: 0x%06x", width, height, (int) topLeftX, (int) topLeftY, dxPxPerFrame, dyPxPerFrame, color);
    }

    public int compareTo(Entity other) {
        return renderLayer - other.getRenderLayer();
    }
}
