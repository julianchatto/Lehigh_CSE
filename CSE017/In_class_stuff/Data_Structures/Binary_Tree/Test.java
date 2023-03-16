public class Test {
    public static void main(String[] args){
        BST<String> bst = new BST<>();
        bst.add("Kiwi");
        bst.add("Strawberry");
        bst.add("Apple");
        bst.add("Banana");
        bst.add("Orange");
        bst.add("Lemon");
        bst.add("Watermelon");
        bst.inorder();
        System.out.println("");
        bst.remove("Banana");
        System.out.println(bst.contains("Banana"));
        bst.inorder();
        bst.remove("Orange");
        System.out.println("");
        bst.inorder();
        bst.remove("Kiwi");
        System.out.println("");
        bst.inorder();
        System.out.println("");
    }
}
