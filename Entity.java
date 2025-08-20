public interface Entity extends Comparable<Entity> {

    public int getColor(); //TODO make this stuff protected
    public void updatePosition();
    public boolean covers(int pixel);
    public int[] getBoundingBoxPixels();
    public void setParent(Screen parent);
    public int getRenderLayer();
    public int getCollisionLayer();
    public boolean collidesWithEntities();
    public boolean collidesWith(Entity other);
    public void resolveCollision(Entity other);

}
