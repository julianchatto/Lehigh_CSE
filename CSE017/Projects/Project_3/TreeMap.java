import java.util.Comparator;

public class TreeMap<K, V> {
    private TreeNode root;
    private int size;
    private Comparator<K> comp;
	public static int iterations;

    /**
	 * Inner class used for the BST nodes
	 */
	private class TreeNode{
		K key;
		V value;
		TreeNode left;
		TreeNode right;
		TreeNode(K key, V val){
			this.key = key;
			value = val;
			left = right = null;
		}
	}

    public TreeMap(Comparator<K> c) {
        comp = c;
        size = 0;
		iterations = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        root = null; 
		size = 0;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public V get(K key) {
		iterations = 0;
		TreeNode node = root;
		while (node != null) {
			iterations++;
			if(comp.compare(key, node.key) < 0) {
				node = node.left;
            } else if (comp.compare(key, node.key)> 0) {
				node = node.right;
            } else {
				return node.value;
            }
		}
		return null;
    }

    public boolean add(K key, V val) {
		if (root == null) {
			root = new TreeNode(key, val);
		} else {
			TreeNode parent, node;
			parent = null; node = root;
			while (node != null) {
				parent = node;
				if (comp.compare(key, node.key) < 0) {
					node = node.left; 
				}
				else if (comp.compare(key, node.key) > 0) {
					node = node.right; 
				}
				else { // found; change value
					node.value = val;
					return true;
				}
			}
			
			if (comp.compare(key, parent.key) < 0) {
				parent.left = new TreeNode(key, val);
			} else {
				parent.right = new TreeNode(key, val);
			}
			
		} 
		size++;
		return true; 
    }

    public boolean remove(K key) {
		TreeNode parent, node;
		parent = null; node = root;
		// Find value first
		while (node != null) {
			if (comp.compare(key, node.key) < 0) {
				parent = node;
				node = node.left;
			}
			else if (comp.compare(key, node.key) > 0) {
				parent = node;
				node = node.right;
			}
			else {
				break;
			}
		}
		if (node == null)
			return false;

		// Case 1: node has no children
		if(node.left == null && node.right == null){
			if(parent == null){
				root = null;
			}
			else{
				changeChild(parent, node, null);
			}
		}
		//case 2: node has one right child
		else if(node.left == null){
			if (parent == null){
				root = node.right;
			}
			else{
				changeChild(parent, node, node.right);
			}
		}
		//case 2: node has one left child
		else if(node.right == null){
			if (parent == null){
				root = node.left;
			}
			else{
				changeChild(parent, node, node.left);
			}
		}
		//case 3: node has two children
		else {
			TreeNode rightMostParent = node;
			TreeNode rightMost = node.left;
			while (rightMost.right != null) {
				rightMostParent = rightMost;
				rightMost = rightMost.right;
			}
			node.value = rightMost.value;
			changeChild(rightMostParent, rightMost, 
					rightMost.left);
		}
		size--;
		return true;
    }

	/**
	 * Private method used by the remove method
	 * to update the links from parent to child
	 * @param parent of the node being deleted
	 * @param node the node being deleted
	 * @param newChild the node that will replace node as the child of parent
	 */
	private void changeChild(TreeNode parent, TreeNode node, TreeNode newChild) {
		if(parent.left == node) {
			parent.left = newChild;
		} else {
			parent.right = newChild;
		}
	}
    public void inorder() {
		inorder(root);
	}
	private void inorder(TreeNode node) {
		if (node != null) {
			inorder(node.left);
			System.out.print(node.value + " ");
			inorder(node.right);
		}
	}

    public void preorder() {
		preorder(root);
	}
	private void preorder(TreeNode node) {
		if (node != null) {
			System.out.print(node.value + " ");
			preorder(node.left);
			preorder(node.right);
		}
	}

    public void postorder() {
		postorder(root);
	}
	private void postorder(TreeNode node)  {
		if (node != null) {
			postorder(node.left);
			postorder(node.right);
			System.out.print(node.value + " ");	
		}
	}
	public int getIterations() {
		return iterations;
	}
}

