import java.util.*;
public class DsaCalculator {
    static Scanner sc = new Scanner(System.in);
    static String exp;
    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.println("Welcome to DSACALCULATOR");
            System.out.print("Enter an expression (Enter -1 to exit): ");
            exp = sc.nextLine().replaceAll("\\s+", "");
            if (exp.equals("-1")) {
                System.out.println("Exiting....");
                return;
            } else if (!exp.matches("[0-9+\\-*/().]+")) {
                System.out.println("Expression contains invalid characters. Auto removing them....");
                exp = exp.replaceAll("[^0-9+\\-*/().]", "");
            }
            exp = fixConsecutiveOperators(exp);
            validate(exp);
        }
    }
    public static void validate(String exp) {
        if (exp.isEmpty()) {
            System.out.print("Expression cannot be empty. Please re-enter: ");
            exp = sc.nextLine().replaceAll("[^0-9+\\-*/().]", "");
            validate(exp);
        } else {
            validateStart(exp);
        }
    }
    public static void validateStart(String exp) {
        StringBuilder sb = new StringBuilder(exp);
        while (sb.length() > 0 && !(sb.charAt(0) == '(' || sb.charAt(0) == '-' || Character.isDigit(sb.charAt(0)))) {
            System.out.println("Expression starts with invalid character, auto removing it....");
            sb.deleteCharAt(0);
        }
        if (sb.length() == 0) {
            System.out.print("All characters removed. Please re-enter: ");
            validate(sc.nextLine().replaceAll("[^0-9+\\-*/().]", ""));
        } else {
            validateEnd(sb.toString());
        }
    }
    public static void validateEnd(String exp) {
        StringBuilder sb = new StringBuilder(exp);
        while (true) {
            char last = sb.charAt(sb.length() - 1);
            if (!(Character.isDigit(last) || last == ')')) {
                System.out.println("Expression is incomplete. Continue typing:");
                String str = sc.nextLine().replaceAll("\\s+", "");
                sb.append(str);
            } else {
                break;
            }
        }
        fixExpression(sb);
    }
    public static void fixExpression(StringBuilder sb) {
        fixExtraClosedParentheses(sb);
        fixOpenParenthesis(sb);
        System.out.println("\nFully corrected expression: " + sb);
        try {
            List<Double> even = new ArrayList<>();
            List<Double> odd = new ArrayList<>();

            // Tokenize the infix expression once
            List<Object> tokens = tokenizeInfix(sb.toString(), even, odd);

            // Convert to postfix for evaluation only
            List<String> postfix = infixToPostfix(sb.toString());
            
            // Prepare evalTokens from postfix tokens
            List<Object> evalTokens = new ArrayList<>();
            for (String token : postfix) {
                if (token.matches("\\d+(\\.\\d+)?")) {
                    evalTokens.add(Double.parseDouble(token));
                } else {
                    evalTokens.add(token);
                }
            }

            // No appending postfix tokens to 'tokens' to avoid duplicates!

            while (true) {
                System.out.println("\nChoose Representation:");
                System.out.println("1. ArrayList");
                System.out.println("2. LinkedList");
                System.out.println("3. Queue (with limited capacity)");
                System.out.println("4. Enter new expression");
                System.out.println("5. Exit");
                String choice = sc.nextLine();
                switch (choice) {
                    case "1":
                        handleArrayListMode(tokens, even, odd);
                        break;
                    case "2":
                        handleLinkedListMode(tokens, even, odd);
                        break;
                    case "3":
                        System.out.print("Input queue capacity: ");
                        int inCap = Integer.parseInt(sc.nextLine());
                        System.out.print("Even/Odd queue capacity: ");
                        int eoCap = Integer.parseInt(sc.nextLine());
                        handleQueueMode(tokens, even, odd, inCap, eoCap);
                        break;
                    case "4":
                        return;
                    case "5":
                        System.out.println("Exiting...");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("Evaluation failed: " + e.getMessage());
        }
    }
    public static void fixExtraClosedParentheses(StringBuilder sb) {
        int balance = 0;
        for (int i = 0; i < sb.length(); i++) {
            char ch = sb.charAt(i);
            if (ch == '(') balance++;
            else if (ch == ')') balance--;
            if (balance < 0) {
                System.out.println("\nUnmatched ')' at index " + i);
                int contextStart = Math.max(0, i - 10);
                int contextEnd = Math.min(sb.length(), i + 5);
                System.out.println("Subexpression: " + sb.substring(contextStart, contextEnd));
                while (true) {
                    System.out.print("Enter index to insert matching '(' (0 to " + i + "): ");
                    int insertIndex = Integer.parseInt(sc.nextLine());
                    if (insertIndex >= 0 && insertIndex <= i) {
                        sb.insert(insertIndex, '(');
                        System.out.println("Fixed: " + sb);
                        break;
                    }
                }
                fixExtraClosedParentheses(sb);
                return;
            }
        }
    }
    public static void fixOpenParenthesis(StringBuilder sb) {
        int openIndex = sb.indexOf("(", 0);
        while (openIndex != -1) {
            int balance = 1;
            int i = openIndex + 1;
            while (i < sb.length()) {
                if (sb.charAt(i) == '(') balance++;
                else if (sb.charAt(i) == ')') balance--;
                if (balance == 0) break;
                i++;
            }
            if (balance == 0) {
                openIndex = sb.indexOf("(", openIndex + 1);
                continue;
            }
            StringBuilder subExpr = new StringBuilder(sb.substring(openIndex));
            fixSubExpression(subExpr);
            sb.replace(openIndex, sb.length(), subExpr.toString());
            openIndex = sb.indexOf("(", openIndex + 1);
        }
    }
    public static void fixSubExpression(StringBuilder expr) {
        int balance = 0;
        int i = 0;
        while (i < expr.length()) {
            if (expr.charAt(i) == '(') balance++;
            else if (expr.charAt(i) == ')') balance--;
            if (balance == 0) return;
            i++;
        }
        System.out.println("\nUnmatched '(' detected.");
        while (balance > 0) {
            System.out.print("Enter index to insert ')'(consider indexing from open parenthesis): ");
            int index = Integer.parseInt(sc.nextLine());
            expr.insert(index, ')');
            balance--;
            System.out.println("Updated: " + expr);
        }
    }
    public static double evaluate(List<Object> tokens) throws Exception {
        Stack<Double> stack = new Stack<>();
        for (Object token : tokens) {
            if (token instanceof Double) {
                stack.push((Double) token);
            } else {
                String op = token.toString();
                if (stack.size() < 2) throw new Exception("Not enough operands");
                double b = stack.pop();
                double a = stack.pop();
                if (op.equals("/")) {
                    while (b == 0) {
                        System.out.println("Divide by zero: " + a + " / 0");
                        System.out.print("Enter replacement for 0: ");
                        b = Double.parseDouble(sc.nextLine());
                    }
                    stack.push(a / b);
                } else if (op.equals("*")) stack.push(a * b);
                else if (op.equals("+")) stack.push(a + b);
                else if (op.equals("-")) stack.push(a - b);
            }
        }
        return stack.pop();
    }
    private static List<String> infixToPostfix(String expr) throws Exception {
        Stack<Character> ops = new Stack<>();
        List<String> output = new ArrayList<>();
        StringBuilder num = new StringBuilder();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                num.append(c);
            } else {
                if (num.length() > 0) {
                    output.add(num.toString());
                    num.setLength(0);
                }
                if (c == '(') {
                    ops.push(c);
                } else if (c == ')') {
                    while (!ops.isEmpty() && ops.peek() != '(') {
                        output.add(String.valueOf(ops.pop()));
                    }
                    if (ops.isEmpty() || ops.pop() != '(') {
                        throw new Exception("Mismatched parentheses");
                    }
                } else if (isOperator(c)) {
                    while (!ops.isEmpty() && precedence(c) <= precedence(ops.peek()) && ops.peek() != '(') {
                        output.add(String.valueOf(ops.pop()));
                    }
                    ops.push(c);
                }
            }
        }
        if (num.length() > 0) {
            output.add(num.toString());
        }
        while (!ops.isEmpty()) {
            char top = ops.pop();
            if (top == '(' || top == ')') throw new Exception("Mismatched parentheses");
            output.add(String.valueOf(top));
        }

        return output;
    }
    private static boolean isOperator(char c) {
        return "+-*/%^".indexOf(c) != -1;
    }
    private static int precedence(char op) {
        return switch (op) {
            case '+', '-' -> 1;
            case '*', '/', '%' -> 2;
            case '^' -> 3;
            default -> 0;
        };
    }
    public static String fixConsecutiveOperators(String expr) {
        StringBuilder fixed = new StringBuilder();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if ("+-*/".indexOf(c) != -1) {
                fixed.append(c);
                i++;
                while (i < expr.length() && "+-*/".indexOf(expr.charAt(i)) != -1) i++;
            } else {
                fixed.append(c);
                i++;
            }
        }
        return fixed.toString();
    }
    // ---------------- Implementation Handlers ---------------- //
    private static void handleArrayListMode(List<Object> tokens, List<Double> even, List<Double> odd) throws Exception {
        ArrayList<Object> list = new ArrayList<>(tokens);
        System.out.println("Representation: " + list);  // infix list view
        double result = evaluate(convertToPostfix(list));  // ensure postfix for eval
        System.out.println("Result: " + result);
        System.out.println("Even Numbers: " + even);
        System.out.println("Odd Numbers: " + odd);
    }
    private static void handleLinkedListMode(List<Object> tokens, List<Double> even, List<Double> odd) throws Exception {
        LinkedList<Object> list = new LinkedList<>(tokens);
        printAsLinks(list, "Representation");  // use link-style print
        double result = evaluate(convertToPostfix(list));
        System.out.println("Result: " + result);
        printAsLinks(new ArrayList<>(even), "Even Numbers");
        printAsLinks(new ArrayList<>(odd), "Odd Numbers");
    }
    private static void handleQueueMode(List<Object> tokens, List<Double> even, List<Double> odd, int inCap, int eoCap) throws Exception {
        Queue<Object> queue = new LinkedList<>(tokens);
        LinkedList<Queue<Double>> inputQ = new LinkedList<>();
        LinkedList<Queue<Double>> evenQ = new LinkedList<>();
        LinkedList<Queue<Double>> oddQ = new LinkedList<>();
        for (Object t : tokens)
            if (t instanceof Double) addToQueueList(inputQ, (Double) t, inCap);
        even.forEach(n -> addToQueueList(evenQ, n, eoCap));
        odd.forEach(n -> addToQueueList(oddQ, n, eoCap));
        
        double result = evaluate(convertToPostfix(new ArrayList<>(queue)));
        System.out.println("Representation: " + queue);
        System.out.println("Result: " + result);

        System.out.println("Input Queues:"); printQueueList(inputQ);
        System.out.println("Even Queues:");  printQueueList(evenQ);
        System.out.println("Odd Queues:");   printQueueList(oddQ);
    }
    private static void addToQueueList(LinkedList<Queue<Double>> queues, Double val, int capacity) {
        for (Queue<Double> q : queues) {
            if (q.size() < capacity) {
                q.offer(val);
                return;
            }
        }
        Queue<Double> newQ = new LinkedList<>();
        newQ.offer(val);
        queues.add(newQ);
    }
    private static void printQueueList(LinkedList<Queue<Double>> queues) {
        int count = 1;
        for (Queue<Double> q : queues) {
            System.out.println("Queue " + count++ + ": " + q);
        }
    }
    private static void printAsLinks(List<?> list, String title) {
        System.out.print(title + ": ");
        for (Object o : list) {
            System.out.print(o + " -> ");
        }
        System.out.println("null");
    }
    public static List<Object> tokenizeInfix(String expr, List<Double> even, List<Double> odd) {
        List<Object> tokens = new ArrayList<>();
        StringBuilder num = new StringBuilder();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                num.append(c);
            } else {
                if (num.length() > 0) {
                    double val = Double.parseDouble(num.toString());
                    tokens.add(val);
                    if ((int) val % 2 == 0) even.add(val);
                    else odd.add(val);
                    num.setLength(0);
                }
                tokens.add(String.valueOf(c));
            }
        }
        if (num.length() > 0) {
            double val = Double.parseDouble(num.toString());
            tokens.add(val);
            if ((int) val % 2 == 0) even.add(val);
            else odd.add(val);
        }
        return tokens;
    }
    private static List<Object> convertToPostfix(List<Object> infix) throws Exception {
        List<Object> postfix = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        Map<String, Integer> precedence = Map.of("+", 1, "-", 1, "*", 2, "/", 2);
        for (Object token : infix) {
            if (token instanceof Double) {
                postfix.add(token);
            } else if (token.equals("(")) {
                stack.push((String) token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    postfix.add(stack.pop());
                }
                if (stack.isEmpty() || !stack.pop().equals("(")) throw new Exception("Mismatched parentheses");
            } else if (precedence.containsKey(token)) {
                while (!stack.isEmpty() && precedence.containsKey(stack.peek()) &&
                        precedence.get((String) token) <= precedence.get(stack.peek())) {
                    postfix.add(stack.pop());
                }
                stack.push((String) token);
            }
        }
        while (!stack.isEmpty()) {
            String op = stack.pop();
            if (op.equals("(") || op.equals(")")) throw new Exception("Mismatched parentheses");
            postfix.add(op);
        }
        return postfix;
    }
}
