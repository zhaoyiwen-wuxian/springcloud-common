/*
package com.trench.cofig.adapter;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.status.StatusLogger;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.MDC;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;

public class TtlMDCAdapter implements MDCAdapter {
    private static Logger LOGGER = StatusLogger.getLogger();

    private final ThreadLocalMapOfStacks mapOfStacks = new ThreadLocalMapOfStacks();

    private static TtlMDCAdapter mtcMDCAdapter;

    static {
        mtcMDCAdapter = new TtlMDCAdapter();
        MDCAdapter mdcAdapter = MDC.getMDCAdapter();
        mdcAdapter = mtcMDCAdapter;
    }
    public static MDCAdapter getInstance() {
        return mtcMDCAdapter;
    }

    @Override
    public void put(final String key, final String val) {
        ThreadContext.put(key, val);
    }

    @Override
    public String get(final String key) {
        return ThreadContext.get(key);
    }

    @Override
    public void remove(final String key) {
        ThreadContext.remove(key);
    }

    @Override
    public void clear() {
        ThreadContext.clearMap();
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        return ThreadContext.getContext();
    }

    @Override
    public void setContextMap(final Map<String, String> map) {
        ThreadContext.clearMap();
        ThreadContext.putAll(map);
    }

    public void pushByKey(String key, String value) {
        if (key == null) {
            ThreadContext.push(value);
        } else {
            final String oldValue = mapOfStacks.peekByKey(key);
            if (!Objects.equals(ThreadContext.get(key), oldValue)) {
                LOGGER.warn("The key {} was used in both the string and stack-valued MDC.", key);
            }
            mapOfStacks.pushByKey(key, value);
            ThreadContext.put(key, value);
        }
    }

    public String popByKey(String key) {
        if (key == null) {
            return ThreadContext.getDepth() > 0 ? ThreadContext.pop() : null;
        }
        final String value = mapOfStacks.popByKey(key);
        if (!Objects.equals(ThreadContext.get(key), value)) {
            LOGGER.warn("The key {} was used in both the string and stack-valued MDC.", key);
        }
        ThreadContext.put(key, mapOfStacks.peekByKey(key));
        return value;
    }

    public Deque<String> getCopyOfDequeByKey(String key) {
        if (key == null) {
            final ThreadContext.ContextStack stack = ThreadContext.getImmutableStack();
            final Deque<String> copy = new ArrayDeque<>(stack.size());
            stack.forEach(copy::push);
            return copy;
        }
        return mapOfStacks.getCopyOfDequeByKey(key);
    }

    public void clearDequeByKey(String key) {
        if (key == null) {
            ThreadContext.clearStack();
        } else {
            mapOfStacks.clearByKey(key);
            ThreadContext.put(key, null);
        }
    }

    private static class ThreadLocalMapOfStacks {

        private final ThreadLocal<Map<String, Deque<String>>> tlMapOfStacks = new TransmittableThreadLocal<>();

        public void pushByKey(String key, String value) {
            tlMapOfStacks.get()
                    .computeIfAbsent(key, ignored -> new ArrayDeque<>())
                    .push(value);
        }

        public String popByKey(String key) {
            final Deque<String> deque = tlMapOfStacks.get().get(key);
            return deque != null ? deque.poll() : null;
        }

        public Deque<String> getCopyOfDequeByKey(String key) {
            final Deque<String> deque = tlMapOfStacks.get().get(key);
            return deque != null ? new ArrayDeque<>(deque) : null;
        }

        public void clearByKey(String key) {
            final Deque<String> deque = tlMapOfStacks.get().get(key);
            if (deque != null) {
                deque.clear();
            }
        }

        public String peekByKey(String key) {
            final Deque<String> deque = tlMapOfStacks.get().get(key);
            return deque != null ? deque.peek() : null;
        }
    }
}
*/
