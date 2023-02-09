package final_project;

public class PriorityQueue {
    private final int MAX;
    private final KDNode[] Array;
    private int itemCount;

    public PriorityQueue(int size) {
        MAX = size;
        Array = new KDNode[MAX];
        itemCount = 0;
    }

    public void insert(KDNode data, KDNode target) {
        if (!isFull()) {
            if (itemCount == 0) {
                Array[itemCount++] = data;
            } else {
                int i;
                for (i = itemCount - 1; i >= 0; i--) {
                    if (data.distance(target) <= Array[i].distance(target)) {
                        Array[i + 1] = Array[i];
                    } else {
                        break;
                    }
                }
                Array[i + 1] = data;
                itemCount++;
            }
        }
        else {
            if (data.distance(target) < Array[itemCount - 1].distance(target)) {
                remove();
                insert(data, target);
            }
        }
    }

    public void remove() {
        if(!isEmpty()){
            itemCount--;
        }
    }

    public KDNode peek() {
        return Array[itemCount - 1];
    }

    public boolean isEmpty() {
        return itemCount == 0;
    }

    public boolean isFull() {
        return itemCount == MAX;
    }

    public int size() {
        return itemCount;
    }

    public KDNode[] getArray() {
        return Array;
    }
}