object CombinationBuilder {
  trait Resource {
    val kind: Int
  }

  type Combination[R <: Resource]    = Set[R]
  type CombinationSet[R <: Resource] = Set[Set[Combination[R]]]

  def combinationWith[R <: Resource](
      resources: Set[R]
  )(cond: Set[R] => Boolean): CombinationSet[R] = {
    val resourceMap       = resources.groupBy(_.kind)
    val resourceKindCount = resourceMap.keys.size

    createCombinations(resources, resourceKindCount, cond)
  }
  def combination[R <: Resource](resources: Set[R]): CombinationSet[R] = {
    combinationWith(resources)(_ => true)
  }

  private def createCombinations[R <: Resource](
      resources: Set[R],
      setSize: Int,
      cond: Set[R] => Boolean
  ): CombinationSet[R] = {
    val combinations =
      resources
        .subsets(setSize)
        .filter(differentKind(setSize, cond))
        .toSet

    for {
      combination  <- combinations
      pCombination <- pendingCombinations(resources, setSize, combination, cond)
    } yield pCombination + combination

  }

  private def pendingCombinations[R <: Resource](
      resources: Set[R],
      setSize: Int,
      combination: Set[R],
      cond: Set[R] => Boolean
  ): CombinationSet[R] = {
    val combinationSet =
      createCombinations(resources -- combination, setSize, cond)
    if (combinationSet.isEmpty) Set(Set()) else combinationSet
  }

  private def differentKind[R <: Resource](
      setSize: Int,
      cond: Set[R] => Boolean
  )(S: Set[R]) = {
    S.groupBy(_.kind).size == setSize && cond(S)
  }
}
