package com.bluemsun.util;

public interface IPasswordChecker
{
    static boolean isComplex(String password) {
        int type1 = 0, type2 = 0, type3 = 0, type4 = 0, type5 = 0;
        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if (Character.isDigit(ch)) {
                type1 = 1;
            } else if (Character.isUpperCase(ch)) {
                type2 = 1;
            } else if (Character.isLowerCase(ch)) {
                type3 = 1;
            } else if (ch == '_') {
                type4 = 1;
            } else {
                type5 = 1;
            }
        }
        return type1 + type2 + type3 + type4 >= 3 && type5 == 0;
    }

    static boolean isSpace(String... strs) {
        boolean ans = false;
        for (String str : strs) {
            ans = ans || str.indexOf(' ') != -1;
        }
        return ans;
    }

    static boolean isEmpty(String... strs) {
        boolean ans = false;
        for (String str : strs) {
            ans = ans || str == null || str.isEmpty();
        }
        return ans;
    }

    static boolean isTooLong(String... strs) {
        for (String str : strs) {
            if (str.length() > 20) {
                return true;
            }
        }
        return false;
    }
}
