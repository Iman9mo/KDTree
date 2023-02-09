package final_project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class KDTree {
    private final int k;
    private int visited;
    private int counter;

    private double bestDistance;
    private KDNode root, best;

    private LinkedList list;

    private PriorityQueue queue;

    private int[] labels;

    KDTree(int k) {
        this.k = k;
        this.root = null;
        this.best = null;
        this.visited = 0;
        this.bestDistance = 0;
    }

    KDTree createTree(float[][] points) throws FileNotFoundException {
        counter = 0;
        Scanner scanner = new Scanner(new File("train_labels.csv"));
        scanner.useDelimiter(",|\r\n|\n");
        labels = new int[points.length];
        for (int i = 0; i < points.length; i++)
            labels[i] = scanner.nextInt();
        for (float[] point : points) this.insert(point);
        return this;
    }

    void insert(float[] point) {
        this.root = insert(root, point, 0);
    }

    KDNode insert(KDNode root, float[] point, int depth) {
        if (root == null) {
            root = new KDNode(point, labels[counter]);
            counter++;
            return root;
        }
        int cd = depth % k;
        if (point[cd] <= root.getCoordinates()[cd])
            root.setLeft(insert(root.getLeft(), point, depth + 1));
        else
            root.setRight(insert(root.getRight(), point, depth + 1));
        return root;
    }

    public float[] findNearest(float[] target) {
        if (root == null)
            throw new IllegalStateException("Tree is empty!");
        best = null;
        visited = 0;
        bestDistance = 0;
        nearest(root, new KDNode(target, -1), 0);
        return best.getCoordinates();
    }

    public int visited() {
        return visited;
    }

    public double distance() {
        return Math.sqrt(bestDistance);
    }

    private void nearest(KDNode root, KDNode target, int index) {
        if (root == null)
            return;
        visited++;
        float d = root.distance(target);
        if ((best == null || d < bestDistance)) {
            bestDistance = d;
            best = root;
        }
        if (bestDistance == 0)
            return;
        float dx = root.getCoordinates()[index] - target.getCoordinates()[index];
        index = (index + 1) % k;
        nearest(dx > 0 ? root.getLeft() : root.getRight(), target, index);
        if (dx * dx >= bestDistance)
            return;
        nearest(dx > 0 ? root.getRight() : root.getLeft(), target, index);
    }

    KDNode[] findMNearest(float[] point, int m) {
        queue = new PriorityQueue(m);
        KDNode[] res;
        findMNearest(this.root, new KDNode(point, -1), 0);
        res = queue.getArray();
        return res;
    }

    void findMNearest(KDNode root, KDNode target, int index) {
        if (root == null)
            return;
        int cd = index % k;
        if(target.getCoordinates()[cd] > root.getCoordinates()[cd]) {
            findMNearest(root.getRight(), target, index + 1);
            if(!queue.isFull() || target.distance(root) < queue.getArray()[queue.size()-1].distance(target))
                queue.insert(root, target);
            if(!queue.isFull() || Math.abs(target.getCoordinates()[cd] - root.getCoordinates()[cd]) < queue.getArray()[queue.size()-1].distance(target))
                findMNearest(root.getLeft(), target, index + 1);
        }

        else {
            findMNearest(root.getLeft(), target, index + 1);
            if(!queue.isFull() || target.distance(root) < queue.getArray()[queue.size()-1].distance(target))
                queue.insert(root, target);
            if(!queue.isFull() || Math.abs(target.getCoordinates()[cd] - root.getCoordinates()[cd]) < queue.getArray()[queue.size()-1].distance(target))
                findMNearest(root.getRight(), target, index + 1);
        }
    }

    float[][] searchRange(float[] lower_bounds, float[] upper_bounds) {
        list = new LinkedList();
        searchRange(root, lower_bounds, upper_bounds, 0);
        float[][] res = new float[list.size][k];
        for (int i = 0; i < list.size; i++) {
            res[i] = list.head.data;
            list.head = list.head.next;
        }
        return res;
    }

    void searchRange(KDNode root, float[] lower_bounds, float[] upper_bounds, int depth) {
        if (root == null)
            return;
        boolean flag = true;
        for (int i = 0; i < lower_bounds.length; i++) {
            if (root.getCoordinates()[i] < lower_bounds[i] || root.getCoordinates()[i] > upper_bounds[i]) {
                flag = false;
                break;
            }
        }
        int cd = depth % k;
        if (flag) {
            this.list.insert(root.getCoordinates());
            searchRange(root.getRight(), lower_bounds, upper_bounds, depth + 1);
            searchRange(root.getLeft(), lower_bounds, upper_bounds, depth + 1);
        } else {
            if (root.getCoordinates()[cd] < lower_bounds[cd])
                searchRange(root.getRight(), lower_bounds, upper_bounds, depth + 1);
            else if (root.getCoordinates()[cd] > upper_bounds[cd])
                searchRange(root.getLeft(), lower_bounds, upper_bounds, depth + 1);
            else {
                searchRange(root.getRight(), lower_bounds, upper_bounds, depth + 1);
                searchRange(root.getLeft(), lower_bounds, upper_bounds, depth + 1);
            }
        }
    }

    boolean pointExists(float[] point) {
        return search(root, point, 0);
    }

    boolean search(KDNode root, float[] point, int depth) {
        if (root == null)
            return false;
        if (arePointsSame(root.getCoordinates(), point))
            return true;
        int cd = depth % k;
        if (point[cd] <= root.getCoordinates()[cd])
            return search(root.getLeft(), point, depth + 1);
        return search(root.getRight(), point, depth + 1);
    }

    boolean arePointsSame(float[] point1, float[] point2) {
        for (int i = 0; i < k; ++i)
            if (point1[i] != point2[i])
                return false;
        return true;
    }

    boolean deletePoint(float[] point) {
        if (!pointExists(point))
            return false;
        delete(root, point, 0);
        return true;
    }

    KDNode delete(KDNode root, float[] point, int depth) {
        if (root == null)
            return null;
        int cd = depth % k;
        if (arePointsSame(root.getCoordinates(), point)) {
            if (root.getRight() != null) {
                KDNode min = findMin(root.getRight(), cd);
                copyPoint(root.getCoordinates(), min.getCoordinates());
                root.setRight(delete(root.getRight(), min.getCoordinates(), depth + 1));
            } else if (root.getLeft() != null) {
                KDNode min = findMin(root.getLeft(), cd);
                copyPoint(root.getCoordinates(), min.getCoordinates());
                root.setRight(delete(root.getLeft(), min.getCoordinates(), depth + 1));
            } else {
                root = null;
                return null;
            }
            return root;
        }
        if (point[cd] <= root.getCoordinates()[cd])
            root.setLeft(delete(root.getLeft(), point, depth + 1));
        else
            root.setRight(delete(root.getRight(), point, depth + 1));
        return root;
    }

    void copyPoint(float[] p1, float[] p2) {
        if (k >= 0)
            System.arraycopy(p2, 0, p1, 0, k);
    }

    KDNode findMin(KDNode root, int d) {
        return findMin(root, d, 0);
    }

    KDNode findMin(KDNode root, int d, int depth) {
        if (root == null)
            return null;
        int cd = depth % k;
        if (cd == d) {
            if (root.getLeft() == null)
                return root;
            return findMin(root.getLeft(), d, depth + 1);
        }
        return minNode(root, findMin(root.getLeft(), d, depth + 1), findMin(root.getRight(), d, depth + 1), d);
    }

    KDNode minNode(KDNode x, KDNode y, KDNode z, int d) {
        KDNode res = x;
        if (y != null && y.getCoordinates()[d] < res.getCoordinates()[d])
            res = y;
        if (z != null && z.getCoordinates()[d] < res.getCoordinates()[d])
            res = z;
        return res;
    }

    public void inOrder(KDNode node) {
        if (node == null)
            return;
        inOrder(node.getLeft());
        System.out.println(Arrays.toString(node.getCoordinates()));
        inOrder(node.getRight());
    }

    public KDNode getRoot() {
        return root;
    }
}

class Test {
    public static void main(String[] args) throws FileNotFoundException {
        KDTree kdTree = new KDTree(3);
        kdTree.createTree(new float[][]{{57, 71, 99}, {59, 57, 65}, {75, 24, 23}, {65, 60, 80},
                {78, 23, 12}, {57, 38, 18}, {11, 68, 88}, {81, 5, 76}, {50, 57, 83}, {94, 78, 83},
                {20, 79, 1}, {67, 8, 7}});

//        kdTree.inOrder(kdTree.getRoot());
//        System.out.println();
        float[] target = {4, 24, 30};
        System.out.println(Arrays.toString(kdTree.findNearest(target)));
        System.out.println(kdTree.deletePoint(new float[]{57, 38, 18}));
        System.out.println(Arrays.toString(kdTree.findNearest(target)));
        System.out.println(kdTree.deletePoint(new float[]{20, 79, 1}));
        System.out.println(Arrays.toString(kdTree.findNearest(target)));
//        System.out.println();
//        kdTree.inOrder(kdTree.getRoot());
    }
}
