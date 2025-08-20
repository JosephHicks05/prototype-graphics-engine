public class Circle implements Entity {

    private int radius;
    private double positionX;
    private double positionY;
    private int dxPxPerFrame;
    private int dyPxPerFrame;
    private boolean collidesWithScreenBorders;
    private boolean collidesWithEntities;
    private int collisionLayer;

    private int color;
    private int renderLayer;
    private Screen parent;

    private static final int DEFAULT_COLOR = 0xffffff;

    public Circle(int radius, int positionX, int positionY) {
        this(radius, positionX, positionY, 0, 0);
    }

    public Circle(int radius, int positionX, int positionY, int dxPxPerFrame, int dyPxPerFrame) {
        this(radius, positionX, positionY, dxPxPerFrame, dyPxPerFrame, DEFAULT_COLOR, 0);
    }

    public Circle(int radius, int positionX, int positionY, int dxPxPerFrame, int dyPxPerFrame, int color, int renderLayer) {
        if (radius < 1) {
            throw new IllegalArgumentException("circles must have a radius of at least 1.");
        }
        this.radius = radius;
        this.positionX = positionX;
        this.positionY = positionY;
        this.dxPxPerFrame = dxPxPerFrame;
        this.dyPxPerFrame = dyPxPerFrame;
        this.color = color;
        this.renderLayer = renderLayer;
    }

    public void updatePosition() {
        double deltaTimeSec = parent.getDeltaTimeMs() / 1000.0;

        positionX += dxPxPerFrame * deltaTimeSec;
        positionY += dyPxPerFrame * deltaTimeSec;

        if (collidesWithScreenBorders) {
            checkWallCollisions(parent.getWindowWidth(), parent.getWindowHeight());
        }
    }

    private void checkWallCollisions(int windowWidth, int windowHeight) {
        int positionXInt = (int) positionX; // cast to int for faster comparisons
        int positionYInt = (int) positionY;

        if (positionXInt + radius > windowWidth) {
            dxPxPerFrame = -Math.abs(dxPxPerFrame);
        }
        else if (positionXInt - radius < 0) {
            dxPxPerFrame = Math.abs(dxPxPerFrame);
        }
        if (positionYInt + radius > windowHeight) {
            dyPxPerFrame = -Math.abs(dyPxPerFrame);
        }
        else if (positionYInt - radius < 0) {
            dyPxPerFrame = Math.abs(dyPxPerFrame);
        }
    }

    public int getCollisionLayer() {
        return collisionLayer;
    }

    public boolean collidesWith(Entity other) {
        if (other instanceof Circle) {
            Circle otherCircle = (Circle) other;
            
            return  getPositionX() - radius < otherCircle.getPositionX() + otherCircle.radius &&
                    getPositionX() + radius > otherCircle.getPositionX() - otherCircle.radius &&
                    getPositionY() - radius < otherCircle.getPositionY() + otherCircle.radius &&
                    getPositionY() + radius > otherCircle.getPositionY() - otherCircle.radius &&
                    (otherCircle.getPositionY() - getPositionY()) * (otherCircle.getPositionY() - getPositionY())
                    + (otherCircle.getPositionX() - getPositionX()) * (otherCircle.getPositionX() - getPositionX())
                    < (radius + otherCircle.radius) * (radius + otherCircle.radius);
        }

        else if (other instanceof Rectangle) { // TODO better
            Rectangle otherRectangle = (Rectangle) other;

            return getPositionX() - radius < otherRectangle.getTopLeftX() + otherRectangle.getWidth() &&
                    getPositionX() + radius > otherRectangle.getTopLeftX() &&
                    getPositionY() - radius < otherRectangle.getTopLeftY() + otherRectangle.getHeight() &&
                    getPositionY() + radius > otherRectangle.getTopLeftY() &&
                    (((getPositionX() > otherRectangle.getTopLeftX() &&
                    getPositionX() < otherRectangle.getTopLeftX() + otherRectangle.getWidth()) ||
                    (getPositionY() > otherRectangle.getTopLeftY() &&
                    getPositionY() < otherRectangle.getTopLeftY() + otherRectangle.getHeight())) ||
                    ((otherRectangle.getTopLeftY() - getPositionY()) * (otherRectangle.getTopLeftY() - getPositionY())
                    + (otherRectangle.getTopLeftX() - getPositionX()) * (otherRectangle.getTopLeftX() - getPositionX())
                    < radius * radius) ||
                    (otherRectangle.getTopLeftY() + otherRectangle.getHeight() - getPositionY()) * (otherRectangle.getTopLeftY() + otherRectangle.getHeight() - getPositionY())
                    + (otherRectangle.getTopLeftX() - getPositionX()) * (otherRectangle.getTopLeftX() - getPositionX())
                    < radius * radius ||
                    (otherRectangle.getTopLeftY() - getPositionY()) * (otherRectangle.getTopLeftY() - getPositionY())
                    + (otherRectangle.getTopLeftX() + otherRectangle.getWidth() - getPositionX()) * (otherRectangle.getTopLeftX() + otherRectangle.getWidth() - getPositionX())
                    < radius * radius ||
                    (otherRectangle.getTopLeftY() + otherRectangle.getHeight() - getPositionY()) * (otherRectangle.getTopLeftY() + otherRectangle.getHeight() - getPositionY())
                    + (otherRectangle.getTopLeftX() + otherRectangle.getWidth() - getPositionX()) * (otherRectangle.getTopLeftX() + otherRectangle.getWidth() - getPositionX())
                    < radius * radius);
        }
        else {
            throw new IllegalArgumentException("the circle class doesn't know how to check for a collision with some type of entity.");
        }
    }

    public void resolveCollision(Entity other) {
        if (other instanceof Circle) {
            Circle otherCircle = (Circle) other;

            //TODO implement
        }
        else if (other instanceof Rectangle) {
            Rectangle otherRectangle = (Rectangle) other;

            //TODO implement


        }
        else {
            throw new IllegalArgumentException("the circle class doesn't know how to resolve a collision with some type of entity.");
        }
    }

    public boolean covers(int pixel) {
        int distanceX = (pixel % parent.getWindowWidth() - (int) positionX);

        int distanceY = (pixel / parent.getWindowWidth() - (int) positionY);

        return Math.abs(distanceX) + Math.abs(distanceY) < radius ||
                Math.sqrt(distanceX * distanceX + distanceY * distanceY) < radius;
    }

    public int[] getBoundingBoxPixels() {
        int[] boundingBoxPixels = new int[radius * radius * 4];

        int windowWidth = parent.getWindowWidth();
        int windowHeight = parent.getWindowHeight();
        int positionXInt = (int) positionX;
        int positionYInt = (int) positionY;

        for (int row = positionXInt - radius; row < positionXInt + radius; row++) {
            for (int col = positionYInt - radius; col < positionYInt + radius; col++) {
                int pixel = col * windowWidth + row;
                boolean colBound = col >= 0 && col < windowHeight;
                boolean rowBound = row >= 0 && row < windowWidth;
                if (pixel >= 0 && pixel < windowHeight * windowWidth && colBound && rowBound) {
                    boundingBoxPixels[(col - (positionYInt - radius)) + ((row - (positionXInt - radius)) * radius * 2)]
                    = col * windowWidth + row;
                }
            }
        }
        return boundingBoxPixels;
    }

    public int getRenderLayer() {
        return renderLayer;
    }

    public int getRadius() {
        return radius;
    }

    public int getPositionX() {
        return (int) positionX;
    }

    public int getPositionY() {
        return (int) positionY;
    }

    public int getDxPxPerFrame() {
        return dxPxPerFrame;
    }

    public int getDyPxPerFrame() {
        return dyPxPerFrame;
    }
    
    public boolean collidesWithScreenBorders() {
        return collidesWithScreenBorders;
    }

    public boolean collidesWithEntities() {
        return collidesWithEntities;
    }
 
    public int getColor() {
        return color;
    }

    public void setParent(Screen parent) {
        this.parent = parent;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionX;
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

    public void setCollisionLayer(int collisionLayer) {
        this.collisionLayer = collisionLayer;
    }
    
    public void setColor(int color) {
        this.color = color;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Circle)) {return false;}

        Circle otherCircle = (Circle) other;

        return otherCircle.radius == radius && otherCircle.color == color;
    }

    public String toString() {
        return String.format("Circle with radius: %d, position x: %d, position y: %d, " +
                "dx: %d, dy: %d, and color: 0x%06x", radius, (int) positionX, (int) positionY, dxPxPerFrame, dyPxPerFrame, color);
    }

    public int compareTo(Entity other) {
        return renderLayer - other.getRenderLayer();
    }
}
