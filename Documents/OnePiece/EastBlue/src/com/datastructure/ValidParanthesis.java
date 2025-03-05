package com.datastructure;

import java.util.Stack;

public class ValidParanthesis {
    public boolean isValidParanthesis(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == '{' || c == '[' || c == '(') {
                stack.push(c);
            } else {
                if (stack.isEmpty() ||
                        (c == ')' && stack.pop() != '(') ||
                        (c == '}' && stack.pop() != '{') ||
                        (c == ']' && stack.pop() != '[')) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }


    public static void main(String[] args) {
        ValidParanthesis validParanthesis=new ValidParanthesis();
        System.out.println(validParanthesis.isValidParanthesis("{}{{}}"));
    }
}