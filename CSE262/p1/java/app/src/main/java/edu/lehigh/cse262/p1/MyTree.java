package edu.lehigh.cse262.p1;

import java.util.List;
import java.util.function.Function;

/**
 * A binary tree, implemented from scratch
 */
public class MyTree<T extends Comparable<T>> { 

    /**
     * A Node in the tree
     */
    private class Node {
        T value;
        Node left;
        Node right;
        Node(T value) {
            this.value = value;
            left = null;
            right = null;
        }
    }

    private Node root; // the root of the tree

    /**
     * Insert a value into the tree
     * 
     * @param value The value to insert
     */
    void insert(T value) {
        if (value == null) return;
        Node newNode = new Node(value);
        if (root == null) { // if the tree is empty, make this the root
            root = newNode;
        } else { // if the tree is not empty
            Node cur = root;
            while (true) {
                int cmp = value.compareTo(cur.value); // compare the value to the current node
                if (cmp == 0) { // if the value is already in the tree, we're done
                    return;
                } else if (cmp < 0) { // if the value is less than the current node, go left
                    if (cur.left == null) { // we have reached a leaf, so insert the new node here
                        cur.left = newNode;
                        return;
                    } else { // not an available leaf, so keep going
                        cur = cur.left;
                    }
                } else { // if the value is greater than the current node, go right
                    if (cur.right == null) { // we have reached a leaf, so insert the new node here
                        cur.right = newNode;
                        return;
                    } else { // not an available leaf, so keep going
                        cur = cur.right;
                    }
                }
            }
        }
    }

    /** Clear the tree */
    Node clear() {
        root = null;
        return root;
    }

    /**
     * Insert all of the elements from some list `l` into the tree
     *
     * @param l The list of elements to insert into the tree
     */
    void inslist(List<T> l) {
        for (T item : l) {
            insert(item); // making use of the insert method
        }
    }

    /**
     * Perform an in-order traversal, applying `func` to every element that is
     * visited
     * 
     * @param func A function to apply to each item
     */
    void inorder(Function<T, T> func) {
        inorder(root, func); // call the helper method
    }

    /**
     * Helper method to perform an in-order traversal, applying `func` to 
     * every element that is visited
     * 
     * @param n The current node
     * @param func A function to apply to each item
     */
    private void inorder(Node n, Function<T, T> func) {
        if (n != null) { // ensure that we have not attempted to traverse past the end of the tree
            inorder(n.left, func); // go left
            n.value = func.apply(n.value); // apply the function to the current node
            inorder(n.right, func); // go right
        }
    }

    /**
     * Perform a pre-order traversal, applying `func` to every element that is
     * visited
     * 
     * @param func A function to apply to each item
     */
    void preorder(Function<T, T> func) {
        preorder(root, func);
    }

    /**
     * Helper method to perform a pre-order traversal, applying `func` to 
     * every element that is visited
     * 
     * @param n The current node
     * @param func A function to apply to each item
     */
    private void preorder(Node n, Function<T, T> func) {
        if (n != null) { // ensure that we have not attempted to traveese past the end of the tree
            n.value = func.apply(n.value); // apply the function to the current node
            preorder(n.left, func); // go left 
            preorder(n.right, func); // go right
        }
    }
}