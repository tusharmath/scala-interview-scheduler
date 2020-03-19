package zio.test

import zio.test.Assertion.equalTo

case class Example[+A](actual: A, expected: A)
trait ExampleRunnableSpec extends DefaultRunnableSpec {
  def eg[A](actual: A, expected: A) = Example(actual, expected)
  def testEg[A](label: String)(seq: Example[A]*) =
    testM(label) {
      check(Gen.fromIterable(seq)) { row =>
        assert(row.actual)(equalTo(row.expected))
      }
    }
}
