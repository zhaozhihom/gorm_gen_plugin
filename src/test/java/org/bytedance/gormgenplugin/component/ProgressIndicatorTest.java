package org.bytedance.gormgenplugin.component;

import com.intellij.openapi.progress.EmptyProgressIndicator;
import org.junit.Test;

public class ProgressIndicatorTest {

    @Test
    public void testProgressIndicator() {
        EmptyProgressIndicator progressIndicator = new EmptyProgressIndicator();
        progressIndicator.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        progressIndicator.cancel();
    }

}
