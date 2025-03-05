package com.datastructure;

public class Stack {
    private int maxSize;
    private int top;
    private int[] stackArray;

    public Stack(int size){
        this.maxSize=size;
        this.top=-1;
        this.stackArray=new int[maxSize];
    }

    public int pop(int num){
        if(top==-1){
            return -1;
        }
        return stackArray[top--];
    }

    public void push(int num){
        if(top==maxSize-1){
            return;
        }
        stackArray[++top]=num;
    }
    public int peek(){
        if(top==-1){
            return -1;
        }
        return stackArray[top];
    }

    public void printStack() {
        if (top==-1) {
            System.out.println("Stack is empty.");
            return;
        }
        System.out.println("Stack elements:");
        for (int i = top; i >= 0; i--) {
            System.out.println(stackArray[i]);
        }
    }
}
