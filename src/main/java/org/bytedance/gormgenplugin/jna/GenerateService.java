package org.bytedance.gormgenplugin.jna;

import com.intellij.openapi.diagnostic.Logger;
import com.sun.jna.Native;

import java.util.Map;

public class GenerateService {

    private static final Logger LOG = Logger.getInstance(GenerateService.class);

    private static final Generator generator = Native.load("gormgen", Generator.class);;


    public String gormGen(String outPath, String url, String tables, String models, String goModulePath) {
        Map<String, String> env = System.getenv();
        env.forEach((k, v) -> System.out.println(k + ":" + v));

        GoString.ByValue genOutPath = new GoString.ByValue(outPath);
        GoString.ByValue genUrl = new GoString.ByValue(url);
        GoString.ByValue genTables = new GoString.ByValue(tables);
        GoString.ByValue genModels = new GoString.ByValue(models);
        GoString.ByValue genGoModulePath = new GoString.ByValue(goModulePath);

        try {
            LOG.info("---------- start generate ------------");
            long exit = generator.generate(genOutPath, genUrl, genTables, genModels, genGoModulePath);
            LOG.info("---------- end generate ------------");
            if (exit != 0) {
                return "generate fail!";
            }
        } catch (Error e) {
            LOG.error("generate code err:", e);
            return e.getMessage();
        }
        return "";
    }

}
