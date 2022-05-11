package com.example.calculator;


import android.util.Log;

import java.util.Stack;

public class RPN {

    public double calculateExpression(String expression) throws Exception {

        RPNCalculator calculator = new RPNCalculator();
        RPNParser parser = new RPNParser();

        String expRPN = parser.parseExpressionToRPN(expression);

        return calculator.calculate(expRPN);

    }

    public static class RPNParser {

        private Stack<Character> stack;
        private String output = "";
        private boolean lastIsNum;
        private String separator = ",";

        RPNParser() {
            stack = new Stack<>();
        }

        public void setSeparator(String separator) {
            this.separator = separator;
        }

        public String parseExpressionToRPN(String exp) throws Exception {
            for (int i = 0; i < exp.length(); i++) {
                if (addSym(exp.charAt(i)) == -1) throw new Exception("Bad Operation: " + exp.charAt(i));
            }

            while (!stack.empty()) output += ' ' + String.valueOf(stack.pop());

            return output;
        }

        private int addSym(char sym) {
            if (!lastIsNum && sym == '-') {
                output += sym;
                lastIsNum = true;
            } else if (isNumeric(sym) || sym == separator.charAt(0) || sym == 'E') {
                if (sym == separator.charAt(0)) sym = '.';
                if (!lastIsNum) output += ' ';
                output += sym;
                lastIsNum = true;
            } else if (priority(sym) == -1 && !isNumeric(sym)) return -1;
            else if (!stack.empty() && priority(stack.lastElement()) >= priority(sym)){
                output += ' ' + String.valueOf(stack.pop());
                stack.add(sym);
                lastIsNum = false;
            } else if (stack.empty() || (priority(stack.lastElement()) < priority(sym))) {
                stack.add(sym);
                lastIsNum = false;
            }

            return 0;
        }

        private boolean isNumeric(char sym) {
            return sym >= '0' && sym <= '9';
        }


        private int priority(char op) {

            switch (op) {
                case '+':
                case '-': return 1;
                case '*':
                case '/': return 2;
                case '^': return 3;
                case '√': return 4;
                case '%': return 5;
                default: return -1;
            }

        }
    }

    public static class RPNCalculator {

        private static final String TAG = "RPNCalculatorLogTag";

        public double calculate(String expressionInRPN) throws Exception {

            String[] operands = expressionInRPN.split(" ");
            Stack<String> stack = new Stack<>();

                for (String operand : operands) {
                    if (!operand.equals("")) {
                        if (isNumeric(operand)) stack.push(operand);
                        else {
                            double n1 = Double.parseDouble(stack.pop());
                            double n2 = 0.0;
                            if (!isUnary(operand)) n2 = Double.parseDouble(stack.pop());
                            else if (!stack.isEmpty()) n2 = Double.parseDouble(stack.lastElement());
                                switch (operand.charAt(0)) {
                                    case '+':
                                        stack.push(String.valueOf(n2 + n1));
                                        break;
                                    case '-':
                                        stack.push(String.valueOf(n2 - n1));
                                        break;
                                    case '*':
                                        stack.push(String.valueOf(n2 * n1));
                                        break;
                                    case '/':
                                        stack.push(String.valueOf(n2 / n1));
                                        break;
                                    case '^':
                                        stack.push(String.valueOf(Math.pow(n2, n1)));
                                        break;
                                    case '√':
                                        stack.push(String.valueOf(Math.sqrt(n1)));
                                        break;
                                    case '%':
                                        stack.push(String.valueOf(n2/100*n1));
                                        break;
                                }
                        }
                    }
                }
                
                Double d = 0.0;
                try {
                    d = Double.parseDouble(stack.lastElement());
                } catch (Exception e) {
                    throw new Exception("Bad operation");
                }

                return d;
        }

        private boolean isNumeric(String sym) {
            try {
                Double.parseDouble(sym);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        private boolean isUnary(String sym) {
            return sym.equals("√");
        }
    }


}
