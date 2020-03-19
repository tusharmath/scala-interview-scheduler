object CombinationBuilder {
  trait Resource {
    val kind: Int
  }

  type Combination    = Set[Resource]
  type CombinationSet = Set[Set[Combination]]

  def combination(resources: Set[Resource]): CombinationSet = {
    val resourceMap       = resources.groupBy(_.kind)
    val resourceKindCount = resourceMap.keys.size

    createCombinations(resources, resourceKindCount)
  }

  private def createCombinations(
      resources: Set[Resource],
      setSize: Int
  ): CombinationSet = {
    val combinations =
      resources.subsets(setSize).filter(differentKind(setSize)).toSet

    for {
      combination  <- combinations
      pCombination <- pendingCombinations(resources, setSize, combination)
    } yield pCombination + combination

  }

  private def pendingCombinations(
      resources: Set[Resource],
      setSize: Int,
      combination: Set[Resource]): CombinationSet = {
    val combinationSet = createCombinations(resources -- combination, setSize)
    if (combinationSet.isEmpty) Set(Set()) else combinationSet
  }

  private def differentKind(setSize: Int)(S: Set[Resource]) = {
    S.groupBy(_.kind).size == setSize
  }
}
