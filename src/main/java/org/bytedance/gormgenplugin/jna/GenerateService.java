package org.bytedance.gormgenplugin.jna;

import com.intellij.openapi.diagnostic.Logger;
import com.sun.jna.Native;

public class GenerateService {

    private static final Logger LOG = Logger.getInstance(GenerateService.class);

    private static final Generator generator = Native.load("gormgen", Generator.class);;


    public String gormGen(String outPath, String url, String tables, String models) {
        GoString.ByValue genOutPath = new GoString.ByValue(outPath);
        GoString.ByValue genUrl = new GoString.ByValue(url);
        GoString.ByValue genTables = new GoString.ByValue(tables);
        GoString.ByValue genModels = new GoString.ByValue(models);

        System.out.println(genUrl.value);

        try {
            long exit = generator.generate(genOutPath, genUrl, genTables, genModels);
            if (exit != 0) {
                return "generate fail!";
            }
        } catch (Exception e) {
            LOG.error("generate code err: v", e);
            return e.getMessage();
        }
        return "";
    }

}
