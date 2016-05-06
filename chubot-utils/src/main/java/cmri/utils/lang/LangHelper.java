package cmri.utils.lang;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by zhuyin on 8/24/15.
 */
public class LangHelper {
    public static void addJar2ClassLoader(String jarPath) throws IOException {
        File file = new File(jarPath);
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | MalformedURLException e) {
            throw new IOException("fail to add " + jarPath + " to system class loader", e);
        }
    }
}
