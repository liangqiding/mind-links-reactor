package com.mind.links.logger.handler.aopLog;


public interface MyTest<T> {

     T accept(T t) ;

//    default MyTest<T> andThen(MyTest<? super T> after) {
//        Objects.requireNonNull(after);
//        return (T t) -> { accept(t); after.accept(t); };
//    }
}
