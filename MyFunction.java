package com.teak.core.function;

/**
 * @author 柚mingle木
 * @version 1.0
 * @date 2023/1/16
 */
@FunctionalInterface
public interface MyFunction<T, R> {
    R action(T t);
}
