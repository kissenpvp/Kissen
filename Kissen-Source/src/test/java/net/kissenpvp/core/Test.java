package net.kissenpvp.core;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Test
{

    /**
     * Method <code>test</code> is a generic method that abstracts
     * the testing process of an evaluation. It uses an evaluator
     * to perform some form of evaluation on the result of
     * tester's execution.
     *
     * <p>
     * It takes two parameters evaluator and tester, with evaluator
     * capable of evaluating some kind of result and tester capable
     * of executing request by evaluator and return a result.
     * </p>
     *
     * <p>
     * The purpose of this method is to evaluate the result of an
     * execution in a type-agnostic manner. By decoupling the evaluation
     * and testing process, we can write tests that are independent of
     * the evaluation mechanism.
     * </p>
     *
     * @param evaluator This is a functional interface that is capable
     *                  of evaluating the result from execution. It
     *                  provides a way to define the behavior of
     *                  evaluation through lambda expressions or method
     *                  references.
     * @param tester    This is the object on which the execution and
     *                  testing happens. It's execute method is invoked
     *                  with the request from evaluator. The result then
     *                  feeds back to evaluators for evaluation.
     * @param <R>       the type of the request object that the tester will
     *                  execute.
     * @param <T>       the type of the result after tester's execution,
     *                  and what evaluator will evaluate.
     */
    protected <R, T> void test(@NotNull Eval<R, T> evaluator, @NotNull Tester<R, T> tester)
    {
        evaluator.evaluateResult(tester.execute(evaluator.request()));
    }

    /**
     * The record <code>Eval</code> is designed as a holder for request, expected result and a
     * validator. It uses the Java record feature that not only acts as a data carrier but
     * also provides logic to evaluate results.
     *
     * <p>
     * A Eval record encapsulates the details of a request that needs be executed and
     * validated. It also contains a Validator which is capable of evaluating the
     * expected and actual result of the request.
     * </p>
     *
     * @param request The request which is to be validated.
     * @param expect The expected result of the request.
     * @param validator The validator which is used to validate the request's result.
     * @param <R> The type of the request.
     * @param <E> The type of the expected result.
     */
    public record Eval<R, E>(@NotNull R request, @NotNull E expect, @NotNull Validator<R, E> validator)
    {
        /**
         * Factory method for creating a new instance of Eval.
         *
         * @param request The request which is to be validated.
         * @param expect The expected result of the request.
         * @param validator The validator which is used to validate the request's result.
         * @param <R> The type of the request.
         * @param <E> The type of the expected result.
         * @return A new instance of Eval.
         */
        @Contract("_, _, _ -> new") public static <R, E> @NotNull Eval<R, E> eval(@NotNull R request,
                @NotNull E expect, @NotNull Validator<R, E> validator)
        {
            return new Eval<>(request, expect, validator);
        }

        /**
         * Method for evaluating a request's result. It uses an instance of the Validator passed
         * at the construction time to perform the evaluation.
         *
         * @param result The result of the request which is to be evaluated.
         */
        public void evaluateResult(@NotNull E result)
        {
            validator().evaluate(request(), expect(), result);
        }
    }

    /**
     * Interface <code>Tester</code> is designed as a FunctionalInterface
     * to execute a particular request. The execute method will execute the
     * request and return the result which the Validator can use to evaluate.
     *
     * @param <R> The type of the request which the tester will execute.
     * @param <E> The type of the result after tester's execution.
     */
    @FunctionalInterface
    protected interface Tester<R, E>
    {
        /**
         * Executes a given request and returns the result of the execution.
         *
         * @param request The request which is to be executed.
         * @return The result of the execution of the request.
         */
        @NotNull E execute(@NotNull R request);
    }


    @FunctionalInterface protected interface Validator<R, E>
    {
        /**
         * This method returns a {@link Validator} that performs an equality check
         * between the expected and actual results using the {@link Assertions#assertEquals} method from JUnit.
         * The contract of this method is to always return a new instance every time it is called.
         *
         * <p>
         * The {@code @Contract(pure = true)} annotation indicates that this method is pure,
         * which means that the returned {@link Validator} doesn't depend on any mutable state
         * and doesn't produce any side effects. The "pureness" of this method allows for safe
         * usage in any context, even in a multi-threaded environment.
         * </p>
         * <p>
         * Example Usage:
         * </p>
         * <pre>{@code
         * // Define a sample request
         * MyRequest request = new SomeRequest();
         *
         * // Define the expected result
         *
         * String expectedResult = "some result";
         *
         * Validator<MyRequest, String> equalityValidator = Validator.equals();
         * test(Eval.eval(request, expectedResult, equalityValidator), (request) ->
         * {
         *      // Perform the actual operation that produces the result
         *      String actualResult = performOperation(request);
         *      return actualResult;
         * });
         * *
         * }</pre>
         *
         * @param <R> The type of the request.
         * @param <E> The type of the expected and the result of the request.
         * @return A new instance of {@link Validator}.
         * @see Validator
         * @see Assertions#assertEquals(Object, Object)
         */
        @Contract(pure = true) static <R, E> @NotNull Validator<R, E> equals()
        {
            return (request, expected, result) -> Assertions.assertEquals(expected, result);
        }

        /**
         * This method returns a {@link Validator} that performs an array equality check between the expected and actual results
         * using the {@link Assertions#assertTrue(boolean)} method from JUnit.
         * The contract of this method is to always return a new instance every time it is called.
         * <p>
         * The {@code @Contract(pure = true)} annotation indicates that this method is pure, which means that the returned
         * {@link Validator} doesn't depend on any mutable state and doesn't produce any side effects. The "pureness" of this method
         * allows for safe usage in any context, even in a multi-threaded environment.
         * </p>
         * <p>
         * The array equality check is performed by converting the expected array into a {@link Set} using the
         * {@link Arrays#stream(Object[])} and {@link Collectors#toSet()} methods, and then iterating through the actual result
         * array to ensure each element is present in the expected set. If any element in the result array is not present in the
         * expected set, an assertion failure will be triggered using {@link Assertions#assertTrue(boolean)}.
         * </p>
         * <p>
         * Example Usage:
         * </p>
         * <pre>{@code
         * // Define a sample request
         * MyRequest request = new SomeRequest();
         *
         * // Define the expected array of results
         *
         * String[] expectedResults = {
         *      "result1",
         *      "result2",
         *      "result3"
         * };
         *
         * Validator<MyRequest, String[]> arrayValidator = Validator.arrayEquals();
         * test(Eval.eval(request, expectedResults, arrayValidator), (request) ->
         * {
         *      // Perform the actual operation that produces the result array
         *      String[] actualResults = performOperation(request);
         *      return actualResults;
         * });
         *
         * }</pre>
         *
         * @param <R> The type of the request.
         * @param <E> The type of the elements in the expected and result arrays.
         * @return A new instance of {@link Validator} for array equality checks.
         * @see Validator
         * @see Assertions#assertTrue(boolean)
         */
        @Contract(pure = true) static <R, E> @NotNull Validator<R, E[]> arrayEquals()
        {
            return (request, expect, result) ->
            {
                Set<E> expected = Arrays.stream(expect).collect(Collectors.toSet());
                for (E current : result)
                {
                    Assertions.assertTrue(expected.contains(current));
                }
            };
        }

        /**
         * Evaluates a request, expected result and actual result.
         *
         * @param request The request which is to be validated.
         * @param expect  The expected result of the request.
         * @param result  The result of the request which is to be compared to the expected result.
         */
        void evaluate(@NotNull R request, @NotNull E expect, @NotNull E result);
    }

}
