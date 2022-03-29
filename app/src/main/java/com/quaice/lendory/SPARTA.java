package com.quaice.lendory;

public class SPARTA {
    private int m;
    private int length;

    public String SHIFR(String Message) {
        m = Message.length();
        this.length = Message.length();
        int n = (((Integer) (length - 1) / m) + 1);
        String s = new String();
        System.out.println(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                try {
                    s += Message.charAt(i + n * j);
                } catch (IndexOutOfBoundsException e) {
                    s += " ";
                }
            }
        }
        return s;
    }

    public String deSHIFR(String SPARTAMessage) {
        m = SPARTAMessage.length();
        int n = (((Integer) (length - 1) / m) + 1);
        char[] s = new char[SPARTAMessage.length()];
        int number = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                s[i + n * j] = SPARTAMessage.charAt(number);
                number++;
            }
        }
        String Message = "";
        for (int i = 0; i < s.length; i++) {
            Message += s[i];
        }
        return Message;
    }
}
