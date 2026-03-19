package me.weishu.reflection;

import android.content.Context;
import android.os.Build;

import java.lang.reflect.Method;

/**
 * Bypass Android hidden API restrictions.
 * Replacement for com.github.tiann:FreeReflection library.
 */
public final class Reflection {

    private Reflection() {}

    public static int unseal(Context context) {
        if (Build.VERSION.SDK_INT < 28) {
            // No restrictions before Android P
            return 0;
        }

        try {
            Method forName = Class.class.getDeclaredMethod("forName", String.class);
            Method getDeclaredMethod = Class.class.getDeclaredMethod(
                    "getDeclaredMethod", String.class, Class[].class);

            Class<?> vmRuntime = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
            Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntime, "getRuntime", null);
            Method setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(
                    vmRuntime, "setHiddenApiExemptions", new Class[]{String[].class});

            Object runtime = getRuntime.invoke(null);
            setHiddenApiExemptions.invoke(runtime, new Object[]{new String[]{"L"}});
            return 0;
        } catch (Throwable e) {
            return -1;
        }
    }
}
