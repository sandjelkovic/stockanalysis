# Stock Analysis application

This is a simple application that provides near real time analysis of the stock prices previously saved to it. It is
used to demonstrate how Redis can be used for high performance and near real time data analysis.

## Running the application

- Make sure port 8080 is not in use, and that you have installed JDK 17+ & Docker
- `docker compose up -d`
- `./gradlew bootRun`

In case you're using IntelliJ or a tool that can read the http requests from the `http` file, you can use the requests
in the `httprequests` file to see the application working.

## Running the tests

To run regular tests, you can use

- `./gradlew check`

To run performance benchmark tests on the calculations alone, you can use
- `./gradlew jmh`

## Performance benchmarks

In order to deduce at what point it becomes more performant to use parallel processing instead of
sequential for calculations, there is a JMH benchmark test. The results are as follows:

```
Benchmark                                                       (size)  Mode  Cnt    Score    Error  Units
SequentialOrParallelBenchmarkTest.calculateSummaryParallel          10  avgt    4    0.024 ±  0.002  ms/op
SequentialOrParallelBenchmarkTest.calculateSummaryParallel         100  avgt    4    0.046 ±  0.006  ms/op
SequentialOrParallelBenchmarkTest.calculateSummaryParallel        1000  avgt    4    0.046 ±  0.019  ms/op
SequentialOrParallelBenchmarkTest.calculateSummaryParallel       10000  avgt    4    0.061 ±  0.020  ms/op
SequentialOrParallelBenchmarkTest.calculateSummaryParallel      100000  avgt    4    0.216 ±  0.085  ms/op
SequentialOrParallelBenchmarkTest.calculateSummaryParallel     1000000  avgt    4    1.607 ±  0.378  ms/op
SequentialOrParallelBenchmarkTest.calculateSummaryParallel    10000000  avgt    4   17.367 ± 10.681  ms/op
SequentialOrParallelBenchmarkTest.calculateSummarySequential        10  avgt    4   ≈ 10⁻⁴           ms/op
SequentialOrParallelBenchmarkTest.calculateSummarySequential       100  avgt    4   ≈ 10⁻³           ms/op
SequentialOrParallelBenchmarkTest.calculateSummarySequential      1000  avgt    4    0.010 ±  0.001  ms/op
SequentialOrParallelBenchmarkTest.calculateSummarySequential     10000  avgt    4    0.099 ±  0.023  ms/op
SequentialOrParallelBenchmarkTest.calculateSummarySequential    100000  avgt    4    1.116 ±  0.140  ms/op
SequentialOrParallelBenchmarkTest.calculateSummarySequential   1000000  avgt    4   14.272 ±  2.322  ms/op
SequentialOrParallelBenchmarkTest.calculateSummarySequential  10000000  avgt    4  120.417 ±  4.259  ms/op
```
