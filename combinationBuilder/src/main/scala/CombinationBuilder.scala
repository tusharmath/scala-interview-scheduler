object CombinationBuilder {

  trait Resource {
    val kind: Int
  }

  type Combination[A]    = Set[Resource]
  type CombinationSet[A] = Set[Set[Combination[A]]]

  def combination[A](resources: Set[Resource]): CombinationSet[A] = {
    val resourceMap       = resources.groupBy(_.kind)
    val resourceKindCount = resourceMap.keys.size

    createCombinations(resources, resourceKindCount)
  }

  private def createCombinations[A](
      resources: Set[Resource],
      setSize: Int
  ): CombinationSet[A] = {
    resources
      .subsets(setSize)
      .filter(differentKind(setSize))
      .flatMap(combination => {
        val K = createCombinations(resources -- combination, setSize)

        if (K.isEmpty) Set(Set(combination)) else K.map(KK => KK + combination)

      })
      .toSet
  }

  private def differentKind[A](setSize: Int)(S: Set[Resource]) = {
    S.groupBy(_.kind).size == setSize
  }
}
