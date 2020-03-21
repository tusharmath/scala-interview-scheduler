import zio.test.Assertion.equalTo
import zio.test._

object PreludeTest {
  case class Example[+A](actual: A, expected: A)
  trait ExampleRunnableSpec extends DefaultRunnableSpec {
    def eg[A](actual: A, expected: A): Example[A] = Example(actual, expected)
    def testEg[A](label: String)(seq: Example[A]*): ZSpec[Any, Nothing] =
      testM(label) {
        check(Gen.fromIterable(seq)) { row =>
          assert(row.actual)(equalTo(row.expected))
        }
      }
  }
}
