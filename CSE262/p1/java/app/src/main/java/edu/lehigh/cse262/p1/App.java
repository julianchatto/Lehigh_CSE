package edu.lehigh.cse262.p1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * App is the entry point into our program. You will probably want to add fields
 * and methods to App so that you can test your code.
 */
public class App {
    public static void main(String[] args) {
        testMyMap();
        testMyReverse();
        testMyTree();
        testPrimeDivisors();
        testReadList();
    }

    /*
     * Test the MyMap class
     */
    private static void testMyMap() {
        List<Integer> list = Arrays.asList( 1, 2, 3, 4, 5, null);
        Function<Integer, Integer> func = x -> x * 2;
        List<Integer> result = new MyMap<Integer>().map(list, func);
        if (result.equals(Arrays.asList(2, 4, 6, 8, 10, null))) {
            System.out.println("MyMap test passed");
        } else {
            System.out.println("MyMap test failed");
        }
    }

    /*
     * Test the MyReverse class
     */
    private static void testMyReverse() {
        List<Integer> list = Arrays.asList( 1, 2, 3, 4, 5, null);
        MyReverse<Integer> myReverse = new MyReverse<>();
        List<Integer> result = myReverse.reverse(list);
        if (result.equals(Arrays.asList(null, 5, 4, 3, 2, 1))) {
            System.out.println("MyReverse test passed");
        } else {
            System.out.println("MyReverse test failed");
        }
    }
    
    /*
     * Test the MyTree class
     */
    private static void testMyTree() {
        MyTree<Integer> tree = new MyTree<>();

        /* Test insert */
        tree.insert(5);
        tree.insert(3);
        tree.insert(2);
        tree.insert(null); // ensure null values are ignored
        tree.insert(10);
        tree.insert(4);


        /* Test inorder and preorder */
        List<Integer> inOrder = new ArrayList<>();    
        tree.inorder(value -> {
            inOrder.add(value);
            return value;
        });
        
        List<Integer> preOrder = new ArrayList<>();
        tree.preorder(value -> {
            preOrder.add(value);
            return value;
        });

        /* Ensure the order follows inorder/preorder */
        if (inOrder.equals(Arrays.asList(2, 3, 4, 5, 10))) {
            System.out.println("MyTree test passed");
        } else {
            System.out.println("MyTree test failed");
        }

        if (preOrder.equals(Arrays.asList(5, 3, 2, 4, 10))) {
            System.out.println("MyTree test passed");
        } else {
            System.out.println("MyTree test failed");
        }


        /* Test clear */
        tree.clear();

        /* Test inslist using preorder and inorder */
        tree.inslist(Arrays.asList(20, 25, 23, 100, -19));

        // clear previous arrays
        inOrder.clear();
        preOrder.clear();

        tree.inorder(value -> {
            inOrder.add(value);
            return value;
        });
        tree.preorder(value -> {
            preOrder.add(value);
            return value;
        });

        /* Ensure the order follows inorder/preorder */
        if (inOrder.equals(Arrays.asList(-19, 20, 23, 25, 100))) {
            System.out.println("MyTree test passed");
        } else {
            System.out.println("MyTree test failed");
        }

        if (preOrder.equals(Arrays.asList(20, -19, 25, 23, 100))) {
            System.out.println("MyTree test passed");
        } else {
            System.out.println("MyTree test failed");
        }

    }

    /* 
     * Test the PrimeDivisors class
     */
    private static void testPrimeDivisors() {
        PrimeDivisors pd = new PrimeDivisors();
        
        // Test for 12
        List<Integer> res = pd.computeDivisors(12);
        if (res.equals(Arrays.asList(2, 3))) {
            System.out.println("PrimeDivisors test passed");
        } else {
            System.out.println("PrimeDivisors test failed");
        }

        // Test for 2
        res = pd.computeDivisors(2);
        if (res.equals(Arrays.asList(2))) {
            System.out.println("PrimeDivisors test passed");
        } else {
            System.out.println("PrimeDivisors test failed");
        }

        // Test for 1
        res = pd.computeDivisors(1);
        if (res.equals(Arrays.asList())) {
            System.out.println("PrimeDivisors test passed");
        } else {
            System.out.println("PrimeDivisors test failed");
        }

        // test for negatives
        res = pd.computeDivisors(-1);
        if (res.equals(Arrays.asList())) {
            System.out.println("PrimeDivisors test passed");
        } else {
            System.out.println("PrimeDivisors test failed");
        }
    }

    /*
     * Test the ReadList class
     */
    private static void testReadList() {
        System.out.println("Enter some integers, then press ctrl + z + enter to end");
        List<Integer> result = new ReadList<Integer>().read(s -> Integer.parseInt(s));
        System.out.println(result);
    }
}
