import java.lang.reflect.*;
import java.util.*;

public class TestRunner {

    public static void run(Class<?> tClass){
        final Object tObject = initTestObj(tClass);

        List<Method> declaredMethods = new ArrayList<>(Arrays.stream(tClass.getDeclaredMethods()).toList());

        activateBeforeAll(declaredMethods, tObject);

        activateTest(declaredMethods, tObject);

        activateAfterAll(declaredMethods, tObject);
    }

    private static Object initTestObj(Class<?> tClass){
        try {
            Constructor<?> noArgsConstructor = tClass.getConstructor();
            return noArgsConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Нет конструктора по умолчанию!");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Не удалось создать объект тест класса!");
        }
    }

    private static void activateTest(List<Method> methods, Object object){
        List<Method> testMethods = findTestMethods(methods);
        testMethods = sortTestMethods(testMethods);

        for(Method tMethod : testMethods){
            List<Method> beforeEachMethods = new ArrayList<>(findBeforeEach(methods));
            List<Method> afterEachMethods = new ArrayList<>(findAfterEach(methods));

            if (tMethod.getAnnotation(Test.class) != null){
                try {
                    activateBeforeEach(beforeEachMethods, object);

                    if(tMethod.getParameterCount() == 2){
                        Test ann = tMethod.getAnnotation(Test.class);
                        int expected = ann.result();

                        try {
                            int actual = Integer.parseInt(tMethod.invoke(object, 2, 2).toString()); // я тут магические цифры вставил, но принцип думаю показал)
                            Asserter.assertEquals(expected, actual);
                            activateAfterEach(afterEachMethods, object);
                            continue;
                        } catch (RuntimeException e) {
                            throw new RuntimeException("тест не прошел");
                        }

                    }


                    tMethod.invoke(object);


                    activateAfterEach(afterEachMethods, object);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static boolean checkAccess(Method method){
        return method.accessFlags().contains(AccessFlag.PRIVATE);
    }

    private static void activateBeforeAll(List<Method> methods, Object object){
        for(Method method : methods){

            if(method.accessFlags().contains(AccessFlag.PRIVATE)){
                continue;
            }

            if(method.getAnnotation(BeforeAll.class) != null){
                try {
                    method.invoke(object);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static List<Method> findBeforeEach(List<Method> methods){
        List<Method> res = new ArrayList<>();

        for(Method method: methods){
            if(checkAccess(method)){
                continue;
            }

            if(method.getAnnotation(BeforeEach.class) != null){
                res.add(method);
            }
        }

        return res;
    }

    private static void activateBeforeEach(List<Method> methods, Object object){
        for(Method method : methods){
            try {
                method.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static List<Method> findAfterEach(List<Method> methods){
        List<Method> res = new ArrayList<>();

        for(Method method: methods){
            if(checkAccess(method)){
                continue;
            }

            if(method.getAnnotation(AfterEach.class) != null){
                res.add(method);
            }
        }

        return res;
    }

    private static void activateAfterEach(List<Method> methods, Object object){
        for(Method method : methods){
            try {
                method.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void activateAfterAll(List<Method> methods, Object object){
        for(Method method : methods){

            if(checkAccess(method)){
                continue;
            }

            if(method.getAnnotation(AfterAll.class) != null){
                try {
                    method.invoke(object);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static List<Method> findTestMethods(List<Method> methods){
        List<Method> res = new ArrayList<>();

        for(Method method : methods){
            if(checkAccess(method)){
                continue;
            }

            if(method.getAnnotation(Test.class) != null){
                res.add(method);
            }
        }

        return res;
    }

    private static List<Method> sortTestMethods(List<Method> methods) {
        LinkedList<Method> res = new LinkedList<>();

        for (Method method : methods) {
            Test anno = method.getAnnotation(Test.class);
            int annOrder = anno.order();

            ListIterator<Method> iterator = res.listIterator();
            while (iterator.hasNext()) {
                Method currentMethod = iterator.next();
                Test currentAnno = currentMethod.getAnnotation(Test.class);
                if (currentAnno.order() > annOrder) {
                    iterator.previous();
                    iterator.add(method);
                    break;
                }
            }

            if (!iterator.hasNext()) {
                res.add(method);
            }
        }

        return res;
    }
}
