package org.javawebstack.abstractdata.json;

import org.javawebstack.abstractdata.*;

import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class JsonParser {

    public AbstractElement parse(String json) throws ParseException {
        char[] primChars = json.toCharArray();
        List<Character> chars = new ArrayList<>(primChars.length);
        for(int i=0; i<primChars.length; i++)
            chars.add(primChars[i]);
        Deque<Character> stack = new ArrayDeque<>(chars);
        AbstractElement parsed;
        try {
            parsed = parse(stack);
        } catch (NullPointerException ex) {
            throw new ParseException("Unexpected character <EOF>", primChars.length);
        }
        if(parsed == null) {
            int line = 1;
            int pos = 1;
            for(int i=0; i<primChars.length - stack.size(); i++) {
                if(primChars[i] == '\n') {
                    line++;
                    pos = 1;
                }
                pos++;
            }
            throw new ParseException("Unexpected character '" + stack.pop() + "' at line " + line + " pos " + pos, primChars.length - stack.size());
        }
        return parsed;
    }

    private AbstractElement parse(Deque<Character> stack) {
        popWhitespace(stack);
        switch (stack.peek()) {
            case '"':
                return parseString(stack);
            case 't': {
                stack.pop();
                if(stack.peek() != 'r')
                    return null;
                stack.pop();
                if(stack.peek() != 'u')
                    return null;
                stack.pop();
                if(stack.peek() != 'e')
                    return null;
                stack.pop();
                return new AbstractPrimitive(true);
            }
            case 'f': {
                stack.pop();
                if(stack.peek() != 'a')
                    return null;
                stack.pop();
                if(stack.peek() != 'l')
                    return null;
                stack.pop();
                if(stack.peek() != 's')
                    return null;
                stack.pop();
                if(stack.peek() != 'e')
                    return null;
                stack.pop();
                return new AbstractPrimitive(false);
            }
            case 'n': {
                stack.pop();
                if(stack.peek() != 'u')
                    return null;
                stack.pop();
                if(stack.peek() != 'l')
                    return null;
                stack.pop();
                if(stack.peek() != 'l')
                    return null;
                stack.pop();
                return AbstractNull.INSTANCE;
            }
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '-':
            case '.':
                return parseNumber(stack);
            case '{':
                return parseObject(stack);
            case '[':
                return parseArray(stack);
            default:
                return null;
        }
    }

    private void popWhitespace(Deque<Character> stack) {
        final String WHITESPACE = " \t\f\b\r\n";
        while (WHITESPACE.contains("" + stack.peek()))
            stack.pop();
    }

    private AbstractPrimitive parseNumber(Deque<Character> stack) {
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(stack.peek()) || stack.peek() == '.' || stack.peek() == '-' || stack.peek() == 'E' || stack.peek() == 'e')
            sb.append(stack.pop());
        String s = sb.toString();
        if(s.contains(".")) {
            return new AbstractPrimitive(Double.parseDouble(s));
        } else {
            long l = Long.parseLong(s);
            if(l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE)
                return new AbstractPrimitive((int) l);
            return new AbstractPrimitive(l);
        }
    }

    private AbstractPrimitive parseString(Deque<Character> stack) {
        stack.pop();
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = stack.pop();
            if(c == '"')
                break;
            if(c == '\\') {
                c = stack.pop();
                switch (c) {
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case '0':
                        sb.append('\0');
                        break;
                    case '\\':
                    case '"':
                    case '/':
                        sb.append(c);
                        break;
                    case 'u':
                        sb.append((char) Integer.parseInt("" + stack.pop() + stack.pop() + stack.pop() + stack.pop(), 16));
                        break;
                }
                continue;
            }
            sb.append(c);
        }
        return new AbstractPrimitive(sb.toString());
    }

    private AbstractObject parseObject(Deque<Character> stack) {
        stack.pop();
        AbstractObject object = new AbstractObject();
        while (true) {
            popWhitespace(stack);
            if(stack.peek() == '}') {
                stack.pop();
                break;
            }
            AbstractPrimitive key = parseString(stack);
            if(key == null)
                return null;
            popWhitespace(stack);
            if(stack.peek() != ':')
                return null;
            stack.pop();
            popWhitespace(stack);
            AbstractElement value = parse(stack);
            if(value == null)
                return null;
            object.set(key.string(), value);
            popWhitespace(stack);
            if(stack.peek() == ',')
                stack.pop();
        }
        return object;
    }

    private AbstractArray parseArray(Deque<Character> stack) {
        stack.pop();
        AbstractArray array = new AbstractArray();
        while (true) {
            popWhitespace(stack);
            if(stack.peek() == ']') {
                stack.pop();
                break;
            }
            AbstractElement value = parse(stack);
            if(value == null)
                return null;
            array.add(value);
            popWhitespace(stack);
            if(stack.peek() == ',')
                stack.pop();
        }
        return array;
    }

}
