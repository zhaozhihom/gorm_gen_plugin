package org.bytedance.gormgenplugin.jna;

import com.sun.jna.Library;

public interface Generator extends Library {

    long generate(GoString.ByValue outPath, GoString.ByValue url, GoString.ByValue tables, GoString.ByValue models, GoString.ByValue goModulePath);

}
