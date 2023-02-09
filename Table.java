package final_project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Table {
    private final KDTree kdTree;

    Table(float[][] records) throws FileNotFoundException {
        kdTree = new KDTree(records[0].length);
        kdTree.createTree(records);
    }

    float[][] search(float[] lower_bounds, float[] upper_bounds) {
        return kdTree.searchRange(lower_bounds, upper_bounds);
    }
}

class TestTable {
    public static void main(String[] args) throws FileNotFoundException {
        float[][] table = new float[100][5];
        Scanner scanner = new Scanner(new File("data.csv"));
        scanner.useDelimiter(",|\r\n|\n");
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 5; j++)
                table[i][j] = scanner.nextFloat();
        }
        Table table1 = new Table(table);
        System.out.println(Arrays.deepToString(table1.search(new float[]{20, 15, 10, 15, 10}, new float[]{50, 90, 80, 40, 85})));
    }
}
