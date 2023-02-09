package final_project;

public class KDNode {
    private final int label;
    private final float[] coordinates;
    private KDNode right;
    private KDNode left;

    public KDNode(float[] coordinates, int label) {
        this.coordinates = coordinates;
        this.label = label;
    }

    public void setRight(KDNode right) {
        this.right = right;
    }

    public void setLeft(KDNode left) {
        this.left = left;
    }

    public float[] getCoordinates() {
        return coordinates;
    }

    public KDNode getRight() {
        return right;
    }

    public KDNode getLeft() {
        return left;
    }

    public int getLabel() {
        return label;
    }

    float distance(KDNode node) {
        float dist = 0;
        for (int i = 0; i < coordinates.length; ++i) {
            double d = coordinates[i] - node.getCoordinates()[i];
            dist += d * d;
        }
        return dist;
    }
}
