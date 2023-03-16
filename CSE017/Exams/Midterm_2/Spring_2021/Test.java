public class Test {
    public static void main(String[] args) {
        ShapeAL crescent = new ShapeAL();
        ShapeLL hexagon = new ShapeLL();

        
        crescent.add(new Pair<>(30, 50));
        crescent.add(new Pair<>(25, 40));
        crescent.add(new Pair<>(25, 30));
        crescent.add(new Pair<>(30, 20));
        crescent.add(new Pair<>(40, 10));

        hexagon.add(new Pair<>(50,60));
        hexagon.add(new Pair<>(40,40));
        hexagon.add(new Pair<>(50,20));
        hexagon.add(new Pair<>(70,20));
        hexagon.add(new Pair<>(90,40));
        hexagon.add(new Pair<>(70,60));
        hexagon.add(new Pair<>(50,60));
        
        System.out.println(crescent.toString());
        
        System.out.println("Crescent point (50, 60) found? " + crescent.containsPoint(new Pair<>(50, 60)));
        System.out.println("Crescent is closed? " + crescent.isClosed());
        System.out.println(hexagon.toString());
        
        System.out.println("Hexagon point (50, 60) found? " + hexagon.containsPoint(new Pair<>(50, 60)));
        System.out.println("Hexagon is closed? " + hexagon.isClosed());


        

    }
    
}
