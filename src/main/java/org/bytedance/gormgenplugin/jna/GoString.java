package org.bytedance.gormgenplugin.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GoString extends Structure {
    public static class ByValue extends GoString implements Structure.ByValue {
        public ByValue() {
        }

        public ByValue(String str) {
            // golang 中 string 没有 null
            this.value = str == null ? "" : str;
            this.n = this.value.getBytes(StandardCharsets.UTF_8).length;
        }
    }
    public String value;
    public long n;

    @Override
    protected List<String> getFieldOrder(){
        return Arrays.asList("value","n");
    }

}
