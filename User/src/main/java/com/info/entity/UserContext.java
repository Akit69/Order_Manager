package com.info.entity;

public class UserContext {
    private static final ThreadLocal<Integer> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();

    public static void set(Integer userId, String username) {
        USER_ID.set(userId);
        USERNAME.set(username);
    }

    public static Integer getUserId() {
        return USER_ID.get();
    }

    public static String getUsername() {
        return USERNAME.get();
    }

    // 必须清理，否则线程池复用会导致内存泄漏
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
    }
}