package final_project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class KNNClassifier {
    private final int k;
     final KDTree kdTree;

    KNNClassifier(float[][] train_data, int k) throws FileNotFoundException {
        this.k = k;
        kdTree = new KDTree(784);
        kdTree.createTree(train_data);
    }

    int classify(float[] data_point) {
        KDNode[] neighbors = kdTree.findMNearest(data_point, k);
        float[] labels = new float[k];
        for (int i = 0; i < k; i++)
            labels[i] = neighbors[i].getLabel();
        return getPopularElement(labels);
    }

    int[] classifyAll(float[][] data_points) {
        int[] res = new int[data_points.length];
        for (int i = 0; i < data_points.length; i++)
            res[i] = classify(data_points[i]);
        return res;
    }

    float accuracy(int[] labels_true, int[] labels_predicted) {
        int correct = 0;
        for (int i = 0; i < labels_predicted.length; i++) {
            if (labels_predicted[i] == labels_true[i])
                correct++;
        }
        return (float) 1.0 * correct / labels_predicted.length;
    }

    public int getPopularElement(float[] a) {
        int count = 1, tempCount;
        float popular = a[0];
        float temp;
        for (int i = 0; i < (a.length - 1); i++) {
            temp = a[i];
            tempCount = 0;
            for (int j = 1; j < a.length; j++) {
                if (temp == a[j])
                    tempCount++;
            }
            if (tempCount > count) {
                popular = temp;
                count = tempCount;
            }
        }
        //System.out.println(Arrays.toString(a) + "   " + popular);
        return (int) popular;
    }

    public static void main(String[] args) throws FileNotFoundException
    {
        float[][] train = new float[10000][784];
        Scanner scanner = new Scanner(new File("train.csv"));
        scanner.useDelimiter(",|\r\n|\n");
        for (int i = 0; i < 10000; i++) {
            for (int j = 0; j < 784; j++)
                train[i][j] = scanner.nextFloat();
            scanner.nextFloat();
        }

        int[] test_labels = new int[200];
        Scanner scanner1 = new Scanner(new File("test_labels.csv"));
        scanner1.useDelimiter(",|\r\n|\n");
        for (int i = 0; i < 200; i++)
            test_labels[i] = scanner1.nextInt();

        float[][] test = new float[200][784];
        Scanner scanner2 = new Scanner(new File("test.csv"));
        scanner2.useDelimiter(",|\r\n|\n");
        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 784; j++)
                test[i][j] = scanner2.nextFloat();
            scanner2.nextFloat();
        }


        KNNClassifier knnClassifier = new KNNClassifier(train, 5);
        System.out.println(Arrays.toString(knnClassifier.kdTree.findMNearest(test[0], 10)));
        //System.out.println(Arrays.toString(test_labels) + "\n" + Arrays.toString(knnClassifier.classifyAll(test)));
        //System.out.println(knnClassifier.accuracy(test_labels, knnClassifier.classifyAll(test)));
        //System.out.println(knnClassifier.getPopularElement(new float[] {2, 1, 2, 0, 1, 2}));
    }
}
