import zio.test.Assertion._
import zio.test._

object EventAvailabilityOpsSpec extends DefaultRunnableSpec {
  import EventAvailabilityOps._

  def spec = suite("EventAvailabilityOps")(
    suite("- Resource") {
      testM("should remove resources") {
        val gen = for {
          availability <- EventAvailabilityGen.genEventAvailability
          resource <- Gen.fromIterable(
            availability.candidates ++ availability.rooms ++ availability.interviewers)
        } yield (availability, resource)
        check(gen) {
          case (eventAvailability, resource) =>
            val hasResource = (eventAvailability - resource).has(resource)

            assert(hasResource)(isFalse)
        }
      }
    },
    suite("+ Resource") {
      testM("should add resources")(
        check(EventAvailabilityGen.genResource,
              EventAvailabilityGen.genEventAvailability)(
          (resource, eventAvailability) => {
            val hasResource = (eventAvailability + resource).has(resource)
            assert(hasResource)(isTrue)
          }))
    }
  )

}
