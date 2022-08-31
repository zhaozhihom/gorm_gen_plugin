package org.zhihom.gormgenplugin.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class GoSlice extends Structure {
    public static class ByValue extends GoSlice implements Structure.ByValue {}
    public Pointer data;
    public long len;
    public long cap;
    protected List getFieldOrder(){
        return Arrays.asList(new String[]{"data","len","cap"});
    }
}