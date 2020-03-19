object CombinationBuilder {
  trait Resource {
    val kind: Int
  }

  type Combination    = Set[Resource]
  type CombinationSet = Set[Set[Combination]]

  def combinationWith(resources: Set[Resource])(
      cond: Set[Resource] => Boolean) = {
    val resourceMap       = resources.groupBy(_.kind)
    val resourceKindCount = resourceMap.keys.size

    createCombinations(resources, resourceKindCount, cond)
  }
  def combination(resources: Set[Resource]): CombinationSet = {
    combinationWith(resources)(_ => true)
  }

  private def createCombinations(
      resources: Set[Resource],
      setSize: Int,
      cond: Set[Resource] => Boolean
  ): CombinationSet = {
    val combinations =
      resources.subsets(setSize).filter(differentKind(setSize, cond)).toSet

    for {
      combination  <- combinations
      pCombination <- pendingCombinations(resources, setSize, combination, cond)
    } yield pCombination + combination

  }

  private def pendingCombinations(
      resources: Set[Resource],
      setSize: Int,
      combination: Set[Resource],
      cond: Set[Resource] => Boolean): CombinationSet = {
    val combinationSet =
      createCombinations(resources -- combination, setSize, cond)
    if (combinationSet.isEmpty) Set(Set()) else combinationSet
  }

  private def differentKind(setSize: Int, cond: Set[Resource] => Boolean)(
      S: Set[Resource]) = {
    S.groupBy(_.kind).size == setSize && cond(S)
  }
}
