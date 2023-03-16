/**
 * Class TreeMap that implements a binary search tree data structure
 */
import java.util.ArrayList;

public class TreeMap<K extends Comparable<K>, V> {
    private TreeNode root;
    private int size;
	public static int iterations;

    /**
	 * Inner class used for the tree nodes
	 */
	private class TreeNode{
        MapEntry<K, V> value;
		TreeNode left;
		TreeNode right;
		TreeNode(K key, V val){
			value = new MapEntry<>(key, val);
			left = right = null;
		}
	}

    public TreeMap() {
        size = 0;
        root = null;
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
			if(key.compareTo(node.value.getKey()) < 0) {
				node = node.left;
            } else if (key.compareTo(node.value.getKey()) > 0) {
				node = node.right;
            } else {
				return node.value.getValue();
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
				if (key.compareTo(node.value.getKey()) < 0) {
					node = node.left; 
				}
				else if (key.compareTo(node.value.getKey()) > 0) {
					node = node.right; 
				}
				else { // found; change value
					node.value.setValue(val);
					return true;
				}
			}
			
			if (key.compareTo(parent.value.getKey()) < 0) {
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
		// Find node first
		while (node != null) {
			if (key.compareTo(node.value.getKey()) < 0) {
				parent = node;
				node = node.left;
			}
			else if (key.compareTo(node.value.getKey()) > 0) {
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
			node.value.setValue(rightMost.value.getValue());
            node.value.setKey(rightMost.value.getKey());
			changeChild(rightMostParent, rightMost, rightMost.left);
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
	/**
	 * Method to return the values of the nodes in the tree
	 * @return array list of the values of the nodes traversed in order
	 */
    // Time complexity: O(n) visit every node
	public ArrayList<V> values() {
        ArrayList<V> list = new ArrayList<>();
		return values(list, root);
	}

    public ArrayList<V> values(ArrayList<V> list, TreeNode node) {
		// NON RECURSVIE METHOD
		// if (node != null) {
		// 	inorder(node.left);
		// 	list.add(node.value.getValue());
		// 	inorder(node.right);
		// }
		// return list;

		// RECURSIVE 
		if (node == null) {
			
			return list;
		}
		if (node.left == null) {
			list.add(node.value.getValue());
			return values(list, node.right);
		} else {
			return values(list, node.right);
		}
		
        
    }
}

