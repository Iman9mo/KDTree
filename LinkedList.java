package final_project;

import java.util.Arrays;

public class LinkedList {

    Node head;
    int size = 0;

    static class Node {

        float[] data;
        Node next;

        // Constructor
        Node(float[] d) {
            data = d;
            next = null;
        }
    }

    public void insert(float[] data) {
        Node new_node = new Node(data);
        new_node.next = null;

        if (this.head == null) {
            this.head = new_node;
        } else {
            Node last = this.head;
            while (last.next != null) {
                last = last.next;
            }
            last.next = new_node;
        }
        size++;
    }

    public  void printList(LinkedList list) {
        Node currNode = list.head;
        System.out.print("\nLinkedList: ");
        while (currNode != null) {
            System.out.print(Arrays.toString(currNode.data) + " ");
            currNode = currNode.next;
        }
        System.out.println("\n");
    }

}
