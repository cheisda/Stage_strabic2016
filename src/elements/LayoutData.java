package elements;

/**
 * Class representing layout data and attached to a vertex
 */
public class LayoutData {

    // coordinates
    private float x;
    private float y;
    private float z;

    // shifting data
    private float dx;
    private float dy;
    private float dz;

    public LayoutData(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dx = 0;
        this.dy = 0;
        this.dz = 0;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }

    public float getDz() {
        return dz;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public void setDz(float dz) {
        this.dz = dz;
    }

    @Override
    public String toString() {
        return "LayoutData{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", dx=" + dx +
                ", dy=" + dy +
                ", dz=" + dz +
                '}';
    }
}
