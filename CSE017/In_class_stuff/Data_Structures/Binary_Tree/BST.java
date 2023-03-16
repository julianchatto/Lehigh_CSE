public class BST<E extends Comparable<E>> {
    private TreeNode root;
    private int size;

    private class TreeNode {
        E value;
        TreeNode left;
        TreeNode right;

        TreeNode(E val) {
            value = val;
            left = right = null;
        }
    }  

    BST(){ // O(1)
        root = null;
        size = 0;
    }
    public int size() { // O(1)
        return size;
    }
    public boolean isEmpty() { // O(1)
        return (size == 0);
    }
    public void clear() { // O(1)
        root = null;
        size = 0;
    }  

    // search method
    public boolean contains(E value) { // O(log n) on average (worst case O(n))
        TreeNode node = root;
        while (node != null) {
            if( value.compareTo(node.value) < 0) {
                node = node.left; // move left
            } else if (value.compareTo(node.value)> 0) {
                node = node.right; // move right
            } else {
                return true; // found
            }
        }
        return false;
    }

    // Method add()
    public boolean add(E value) { // O(log n) on average (worst case O(n))
        if (root == null) { // first node to be inserted
            root = new TreeNode(value);
        } else {
            TreeNode parent, node;
            parent = null; node = root;
            while (node != null) {// Looking for a leaf node
                parent = node;
                if(value.compareTo(node.value) < 0) {
                    node = node.left; 
                } else if (value.compareTo(node.value) > 0) {
                    node = node.right; 
                } else {
                    return false; // duplicates are not allowed
                }
            }
            if (value.compareTo(parent.value)< 0) {
                parent.left = new TreeNode(value); // used to be item???
            } else {
                parent.right = new TreeNode(value);
            }
        }
        size++;
        return true;
    }

    // Method remove()
    public boolean remove(E value) { // O(log n) on average (worst case O(n))
        TreeNode parent, node;
        parent = null; node = root;
        // Find value first
        while (node != null) {
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
            return false;
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
                rightMostParent = rightMost;
                rightMost = rightMost.right;
            }
            // copy the value of rigthMost to node
            node.value = rightMost.value;
            //delete rigthMost
            changeChild(rightMostParent, rightMost, rightMost.left);
        }
        size--;
        return true;
    }
    private void changeChild(TreeNode parent, TreeNode node, TreeNode newChild) { // O(1)
        if(parent.left == node) {
            parent.left = newChild;
        } else {
            parent.right = newChild;
        }
    }

    // Recursive method inorder()
    public void inorder() { // O(n)
        inorder(root);
    }
    private void inorder(TreeNode node) { // O(n)
        if (node != null) {
            inorder(node.left);
            System.out.print(node.value + " ");
            inorder(node.right);
        }
    }
    // Recursive method preorder()
    public void preorder() { // O(n)
        preorder(root);
    }
    private void preorder(TreeNode node) { // O(n)
        if (node != null) {
            System.out.print(node.value + " ");
            preorder(node.left);
            preorder(node.right);
        }
    }

    // Recursive method postorder()
    public void postorder() { // O(n)
        postorder(root);
    }
    private void postorder(TreeNode node) { // O(n)
        if (node != null) {
        postorder(node.left);
        postorder(node.right);
        System.out.print(node.value + " ");
        }
    }
    
}