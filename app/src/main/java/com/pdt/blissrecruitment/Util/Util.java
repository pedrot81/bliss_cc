package com.pdt.blissrecruitment.Util;

/**
 * Created by pdt on 28/07/2017.
 */

public class Util {
    private static final String SCHEME = "blissrecruitment";
    private static final String HOST = "questions";
    private static final String PARAM_QUESTION_ID = "question_id";
    private static final String PARAM_QUESTION_FILTER = "question_filter";

    public static String buildQuestionUrl(int questionId) {
        String id = String.valueOf(questionId);
        StringBuilder builder = new StringBuilder();
        builder.append(SCHEME)
                .append("://")
                .append(HOST)
                .append("?")
                .append(PARAM_QUESTION_ID)
                .append("=")
                .append(id);
        return builder.toString();
    }

    public static String buildQuestionFilterUrl(String filter) {
        StringBuilder builder = new StringBuilder();
        builder.append(SCHEME)
                .append("://")
                .append(HOST)
                .append("?")
                .append(PARAM_QUESTION_FILTER)
                .append("=")
                .append(filter.trim());
        return builder.toString();
    }
}
