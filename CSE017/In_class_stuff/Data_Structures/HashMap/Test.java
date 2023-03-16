public class Test {
    public static void main(String[] args) {
        HashMap<String, String> states = new HashMap<>(10);
        states.put("PA", "Pennsylvania");
        states.put("NY", "New York");
        states.put("MA", "Massachusetts");
        states.put("CA", "California");
        states.put("NJ", "New Jersey");
        states.put("OH", "Ohio");
        states.put("NM", "New Mexico");
        states.put("WA", "Washington");
        System.out.println(states);
        System.out.println("Code NJ is for " + states.get("NJ"));
        System.out.println("NY is in the map? " + states.containsKey("NY"));
        states.remove("MA");
        System.out.println(states);
        states.clear();
        System.out.println(states);
    }
}
