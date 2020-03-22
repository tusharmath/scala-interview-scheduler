import adt._
import zio.test.Assertion._
import zio.test._

object EventAvailabilityOpsSpec extends DefaultRunnableSpec {
  import syntax._

  def spec = suite("EventAvailabilityOps")(
    suite("- Resource") {
      testM("should remove resources") {
        val gen = for {
          availability <- EventAvailabilityGen.genEventAvailability
          resource     <- Gen.fromIterable(getResources(availability))
        } yield (availability, resource)
        check(gen) {
          case (eventAvailability, resource) => {
            val nEventAvailability = eventAvailability - resource
            val nResources         = getResources(nEventAvailability)
            val hasResource        = nResources.contains(resource)

            assert(hasResource)(isFalse)
          }

        }
      }
    },
    suite("+ Resource") {
      testM("should add resources")(
        check(EventAvailabilityGen.genResource,
              EventAvailabilityGen.genEventAvailability)(
          (resource, eventAvailability) => {
            val nEventAvailability = eventAvailability + resource
            val nResources         = getResources(nEventAvailability)
            val hasResource        = nResources.contains(resource)
            assert(hasResource)(isTrue)
          }))
    }
  )

  private def getResources(
      availability: EventAvailability): Set[EventResource] = {
    availability.candidates ++ availability.rooms ++ availability.interviewers
  }
}
