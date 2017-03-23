package slf4jtest;

public interface Predicate<T> {
    boolean matches(T row);
}
