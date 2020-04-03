package com.instagraph.paths.allpairs.fullpaths

import com.instagraph.paths.ShortestPaths._
import com.instagraph.paths.allpairs.adjacents.SinglePathInfo
import org.apache.spark.graphx.{Graph, VertexId}

import scala.reflect.ClassTag

case class SingleFullPath[V: ClassTag, E: ClassTag](graph: Graph[V, E])(implicit numeric: Numeric[E])
  extends FullPathsAllPairShortestPaths[V, E, SingleFullPathInfo[E], SinglePathInfo[E]](graph.singlePredecessorAPSP) {
  override protected def initializeInfo(cost: E, paths: List[VertexId]*): SingleFullPathInfo[E] =
    SingleFullPathInfo(cost, paths.head)
}

case class SingleFullPathInfo[+E](override val cost: E, path: List[VertexId]) extends FullPathsInfo[E] {
  override def paths: Set[List[VertexId]] = Set(path)
}