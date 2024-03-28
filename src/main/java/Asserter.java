public class Asserter {

    public static void assertEquals(int expected, int actual){
        if(actual != expected){
            throw new RuntimeException();
        }

        System.out.println("Тест прошел!");
    }
}
