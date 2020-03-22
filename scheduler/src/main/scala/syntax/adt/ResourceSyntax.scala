package syntax.adt

import adt.{EventAvailability, EventResource}

trait ResourceSyntax {
  implicit class ResourceOps(resource: EventResource) {
    def +(resource0: EventResource): EventAvailability = {
      EventAvailability() + resource + resource0
    }
  }
}
