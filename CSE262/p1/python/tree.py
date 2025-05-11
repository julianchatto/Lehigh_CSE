# tree: A binary tree, implemented as a class
#
# The tree should support the following methods:
# - ins(x)      - Insert the value x into the tree
# - clear()     - Reset the tree to empty
# - inslist(l)  - Insert all the elements from list `l` into the tree
# - display()   - Use `display` to print the tree
# - inorder(f)  - Traverse the tree using an in-order traversal, applying
#                 function `f` to the value in each non-null position
# - preorder(f) - Traverse the tree using a pre-order traversal, applying
#                 function `f` to the value in each non-null position

# node class to rperesent node in a BST
class Node:
    def __init__(self, val):
        self.v = val
        self.l = None
        self.r = None

class tree:
    def __init__(self):
        self.root = None #root of new tree orginally empty 

    # insert method to insert value into tree
    def ins(self, x):

        # create new node from value if needed, insert into correct position 
        def insert(node, x):
            if node is None:
                return Node(x)
            if x < node.v: # insert in left subtree
                node.l = insert(node.l, x)
            else: # insert in right subtree 
                node.r = insert(node.r, x)
            return node
        # ensure modified tree correctly stored 
        self.root = insert(self.root, x)
        
    # clear tree, clear root so tree is no longer existent (can't be tracked via a root)
    # no need to recursively delete each root due to python's garbage collection method 
    def clear(self):
        self.root = None

    # insert all the elements from list `l` into the tree using a for loop 
    def inslist(self, l):
        for x in l:
            self.ins(x)

    # display tree, "level" and "prefix" used for output styling purposes 
    def display(self):
        def _display(node, level = 0, prefix = "root: "):
            if node is not None:
                # multiply by 4 for visualization purposes (indent left and right children when displaying)
                print(" " * (level * 4) + prefix + str(node.v))
                if node.l or node.r:
                    _display(node.l, level + 1, "L: ")
                    _display(node.r, level + 1, "R: ")
        _display(self.root)
    
    # apply function via inorder traversal 
    def inorder(self, f):
        def _inorder(node):
            if node is not None:
                # apply to left subtrees
                _inorder(node.l)
                f(node.v)
                # apply to right subtrees 
                _inorder(node.r)
        _inorder(self.root)

    # apply function via preorder traversal 
    def preorder(self, f):
        def _preorder(node):
            if node is not None:
                # apply to node then apply to left then right subtrees 
                f(node.v)
                _preorder(node.l)
                _preorder(node.r)
        _preorder(self.root)

# testing
def test_tree():
    # insert elements and check in-order traversal
    t = tree()
    t.inslist([5, 3, 7, 2, 4, 6, 8])
    result = []
    t.inorder(lambda x: result.append(x))
    assert result == [2, 3, 4, 5, 6, 7, 8]
    
    # check pre-order traversal
    result = []
    t.preorder(lambda x: result.append(x))
    assert result == [5, 3, 2, 4, 7, 6, 8]
    
    # clear tree and check if empty
    t.clear()
    result = []
    t.inorder(lambda x: result.append(x))
    assert result == []
    
    # insert a single element and check structure
    t.ins(10)
    result = []
    t.inorder(lambda x: result.append(x))
    assert result == [10]
    
    print("All tests passed!")

test_tree()
