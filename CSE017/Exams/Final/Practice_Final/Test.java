public class Test {
    public static void main(String[] args) {
        double result = sumSeries(3);
        System.out.println(result);
    }


    public static double sumSeries(int num) {
        if (num == 0) {
            return 1;
        }       
        return (double) num/(2*num + 1) + sumSeries(num - 1);      
    }

} 