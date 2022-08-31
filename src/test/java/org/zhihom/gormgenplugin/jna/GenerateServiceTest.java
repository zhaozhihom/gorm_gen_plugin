package org.zhihom.gormgenplugin.jna;

import org.junit.Before;
import org.junit.Test;

public class GenerateServiceTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void gormGen() {
        new GenerateService().gormGen("dal/query", "root:@(127.0.0.1:3306)/gorm_test", "user_role,users", ",", "");
    }
}