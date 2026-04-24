package com.flashsale.seckill.captcha.domain;

public class Captcha {

    private final String expression;

    private final int answer;

    public Captcha(String expression, int answer) {
        this.expression = expression;
        this.answer = answer;
    }

    public boolean verify(int input) {
        return input == answer;
    }

    public String getExpression() {
        return expression;
    }

    public int getAnswer() {
        return answer;
    }
}
