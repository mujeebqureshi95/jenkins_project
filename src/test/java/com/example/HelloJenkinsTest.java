package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloJenkinsTest {
    @Test
    public void testAdd() {
        assertEquals(5, HelloJenkins.add(2, 3));
    }
}
