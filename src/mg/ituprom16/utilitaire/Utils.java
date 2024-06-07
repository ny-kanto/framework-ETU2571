package mg.ituprom16.utilitaire;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Vector;
import mg.ituprom16.annotations.Get;

public class Utils {
    public static String modifierClassPath(String classpath) {
        classpath = classpath.substring(1);
        classpath = classpath.replace("%20", " ");
        return classpath;
    }

    public static void scanClass(Class<?> annotClass, HashMap<String, Mapping> map) throws Exception {
        Method[] methods = annotClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isAnnotationPresent(Get.class)) {
                Get getAnnot = methods[i].getAnnotation(Get.class);
                if (map.containsKey(getAnnot.value())) {
                    throw new Exception("Url bdb mitovy anarana !!!");
                } else {
                    map.put(getAnnot.value(), new Mapping(annotClass.getName(), methods[i].getName()));
                }
            }
        }
    }

    public static void scanListClasses(Vector<Class<?>> classes, HashMap<String, Mapping> map) throws Exception {
        for (int i = 0; i < classes.size(); i++) {
            scanClass(classes.get(i), map);
        }
    }

    public static Vector<Class<?>> getAllClassAnnoted(String path, Class<? extends Annotation> annotation)
            throws Exception {
        Vector<Class<?>> classAnnotedList = new Vector<Class<?>>();
        try { 
            File classPathDirectory = new File(path);
            for (File file : classPathDirectory.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".class")) {
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    Class<?> class1 = Thread.currentThread().getContextClassLoader()
                            .loadClass(path.split("classes/")[1].replace("/", ".") + className);
                    if (class1.isAnnotationPresent(annotation)) {
                        classAnnotedList.add(class1);
                    }
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return classAnnotedList;
    }

    public static Object invokedMethod(HashMap<String, Mapping> map, String urlValue) throws Exception {
        Object toReturn = new Object();
            if (map.containsKey(urlValue)) {
                Mapping mapping = map.get(urlValue);
                Class<?> myClass = Class.forName(mapping.getClassName());
                Object myObject = myClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                Method myMethod = myClass.getDeclaredMethod(mapping.getMethodName(), new Class[0]);

                toReturn = myMethod.invoke(myObject, new Object[0]);
            } else {
                throw new IllegalArgumentException("URL non reconnu");
            } 

        return toReturn;
    }
}
