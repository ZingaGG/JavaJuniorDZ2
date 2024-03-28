//1. Создать аннотации BeforeEach, BeforeAll, AfterEach, AfterAll
//2. Доработать класс TestRunner так, что
//2.1 Перед всеми тестами запускаеются методы, над которыми стоит BeforeAll
//2.2 Перед каждым тестом запускаются методы, над которыми стоит BeforeEach
//2.3 Запускаются все тест-методы (это уже реализовано)
//2.4 После каждого теста запускаются методы, над которыми стоит AfterEach
//2.5 После всех тестов запускаются методы, над которыми стоит AfterAll
//        Другими словами, BeforeAll -> BeforeEach -> Test1 -> AfterEach -> BeforeEach -> Test2 -> AfterEach -> AfterAll
//
//3.* Доработать аннотацию Test: добавить параметр int order,
//по котрому нужно отсортировать тест-методы (от меньшего к большему) и запустить в нужном порядке.
//Значение order по умолчанию - 0
//        4.** Создать класс Asserter для проверки результатов внутри теста с методами:
//        4.1 assertEquals(int expected, int actual)
//Идеи реализации: внутри Asserter'а кидать исключения, которые перехвываются в тесте.
//Из TestRunner можно возвращать какой-то объект, описывающий результат тестирования.


public class Main {
    public static void main(String[] args) {
        TestRunner.run(Main.class);
        System.out.println();
    }
    @Test(order = 1)
    void test1() {
        System.out.println("test1");
    }
    @Test(order = 2)
    void test2() {
        System.out.println("test2");
    }
    @Test(order = 3)
    void test3() {
        System.out.println("test3");
    }


   @BeforeAll
    void beforeAll(){
       System.out.println("Before All");
   }

   @BeforeEach
    void beforeEach(){
       System.out.println("Before Each");
   }

   @AfterAll
    void afterAll(){
       System.out.println("After All");
   }

    @AfterEach
    void afterEach(){
        System.out.println("After Each");
    }
    @Test(order = 3,result = 4) // 4 - тест пройдет, остальные не пройдут! © Гендальф
    int test4(int a, int b){
        return a+b;
    }
}
