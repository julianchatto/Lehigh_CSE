public class BST<E extends Comparable<E>> {
    private TreeNode root;
    private int size;

    /***
     * Private class for a Tree node
     */
    private class TreeNode {
        E value;
        TreeNode left;
        TreeNode right;
        /***
         * Constructor with one parameter
         * @param val for the value of the object
         * sets value to val, and left and right node to null
         */
        TreeNode(E val) {
            value = val;
            left = right = null;
        }
    }  

    /***
     * Default constructor
     * sets root to null and size to 0
     */
     // O(1)
    BST(){
        root = null;
        size = 0;
    }
    /***
     * Method to return the size of the BST
     * @return size of the BST
     */
    // O(1)
    public int size() { 
        return size;
    }
    /***
     * Method to determine if the BST is empty
     * @return true if empty, false otherwise
     */
    // O(1)
    public boolean isEmpty() { 
        return (size == 0);
    }

    /***
     * Method to clear the BST
     * Sets root to null and size to 0
     */
     // O(1)
    public void clear() {
        root = null;
        size = 0;
    }  

    /***
     * Method to detrime if the BST contains a specified object
     * @param value the object being searched for
     * @return the number of iterations to find value
     */
    // O(log n) on average (worst case O(n))
    public int contains(E value) { 
        TreeNode node = root;
        int iterations = 0;
        while (node != null) {
            iterations++;
            if( value.compareTo(node.value) < 0) {
                node = node.left; // move left
            } else if (value.compareTo(node.value)> 0) {
                node = node.right; // move right
            } else {
                return iterations; // found
            }
        }
        return iterations;
    }

    /***
     * MEthod to add value to the BST
     * @param value the object being added
     * @return the number of iterations to add value to BST
     */
    // O(log n) on average (worst case O(n))
    public int add(E value) { 
        int iterations = 0;
        if (root == null) { // first node to be inserted
            root = new TreeNode(value);
        } else {
            TreeNode parent, node;
            parent = null; node = root;
            while (node != null) {// Looking for a leaf node
                iterations++;
                parent = node;
                if(value.compareTo(node.value) < 0) {
                    node = node.left; 
                } else if (value.compareTo(node.value) > 0) {
                    node = node.right; 
                } else {
                    return iterations; // duplicates are not allowed
                }
            }
            if (value.compareTo(parent.value)< 0) {
                parent.left = new TreeNode(value); // used to be item???
            } else {
                parent.right = new TreeNode(value);
            }
        }
        size++;
        return iterations;
    }

    /***
     * Method to remove value from BST
     * @param value the object being removed
     * @return the number of iterations to remove value
     */
    // O(log n) on average (worst case O(n))
    public int remove(E value) { 
        int iterations = 0;
        TreeNode parent, node;
        parent = null; node = root;
        // Find value first
        while (node != null) {
            iterations++;
            if (value.compareTo(node.value) < 0) {
                parent = node;
                node = node.left;
            } else if (value.compareTo(node.value) > 0) {
                parent = node;
                node = node.right;
            } else { 
                break; // value found
            }
        }
        if (node == null) { // value not in the tree
            return iterations;
        } 

        // Case 1: node has no children
        if(node.left == null && node.right == null){
            if(parent == null){ // delete root
                root = null;
            } else{
                changeChild(parent, node, null);
            }
        } else if(node.left == null) { // case 2: node has one right child
            if (parent == null){ // delete root
                root = node.right;
            } else{
                changeChild(parent, node, node.right);
            }
        } else if(node.right == null) { // case 2: node has one left child
            if (parent == null){ // delete root
                root = node.left;
            } else {
                changeChild(parent, node, node.left);
            }
        } else { // case 3: node has two children
            TreeNode rightMostParent = node;
            TreeNode rightMost = node.left;
            // go right on the left subtree
            while (rightMost.right != null) {
                iterations++;
                rightMostParent = rightMost;
                rightMost = rightMost.right;
            }
            // copy the value of rigthMost to node
            node.value = rightMost.value;
            //delete rigthMost
            changeChild(rightMostParent, rightMost, rightMost.left);
        }
        size--;
        return iterations;
    }

    /***
     * Method to change the child of a node
     * @param parent for the parent node
     * @param node for the node
     * @param newChild for the new child being added
     */
    // O(1)
    private void changeChild(TreeNode parent, TreeNode node, TreeNode newChild) { 
        if(parent.left == node) {
            parent.left = newChild;
        } else {
            parent.right = newChild;
        }
    }

    /***
     * Method to print BST in Order
     */
    // O(n)
    public void inorder() { 
        inorder(root);
    }
    /***
     * Helper method to print BST inorder
     * @param node the current node
     */
     // O(n)
    private void inorder(TreeNode node) {
        if (node != null) {
            inorder(node.left);
            System.out.print(node.value + " ");
            inorder(node.right);
        }
    }
    /***
     * Method to print BST in preorder
     */
    // O(n)
    public void preorder() { 
        preorder(root);
    }
    /***
     * Helper method to print BST in preorder
     * @param node for the current node
     */
    // O(n)
    private void preorder(TreeNode node) { 
        if (node != null) {
            System.out.print(node.value + " ");
            preorder(node.left);
            preorder(node.right);
        }
    }

    /***
     * Method to print BST in postorder
     */
    // O(n)
    public void postorder() { 
        postorder(root);
    }

    /***
     * helper Method to print BST in postorder 
     * @param node for the current node
     */
    // O(n)
    private void postorder(TreeNode node) { 
        if (node != null) {
            postorder(node.left);
            postorder(node.right);
            System.out.print(node.value + " ");
        }
    }
    
    /***
     * Method to get the height of BST
     * @return  the height of BST
     */
    // O(n)
    public int height() { 
        return height(root);
    }

    /*** 
     * Helper method to get the height of BST
     * @param node the current node
     * @return the height of the node
     */
    // O(n)
    public int height(TreeNode node) {
        if (node != null) {
            int lHeight = height(node.left);
            int rHeight = height(node.right);
            return 1 + Math.max(lHeight, rHeight);
        } 
        return 0;        
    }

    /***
     * Method to determine if the BST is balanced
     * @return true if balanced, false otherwise
     */
    // O(n^2)
    public boolean isBalanced() {
        return isBalanced(root);
        
    }

    /***
     * Method to determine if the tree isBalanced
     * @param node for the current node in the tree
     * @return true if the node is balanced, false otherwise
     */
    // O(n^2)
    public boolean isBalanced(TreeNode node) {
        if (node == null) {
            return true;
        }

        int lHeight = height(node.left); // O(n)
        int rHeight = height(node.right); // O(n)
        int diff = Math.abs(rHeight-lHeight);
        if(diff > 1) {
            return false;
        }
        return isBalanced(node.left) && isBalanced(node.right);
    }
}