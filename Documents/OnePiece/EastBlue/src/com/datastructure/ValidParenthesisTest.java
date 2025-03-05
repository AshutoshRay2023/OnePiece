package com.datastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidParenthesisTest {

        private final ValidParanthesis validParanthesis=new ValidParanthesis();

        @Test
        public void testValidCases(){
                assertTrue(validParanthesis.isValidParanthesis("{}{}{}"));
        }

        @Test
        public void testInvalidTestCases(){
                assertFalse(validParanthesis.isValidParanthesis("{{}{}(}"));
        }
}
