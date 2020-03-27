package com.instagraph.paths

import ShortestPathGraph.Manipulations
import ShortestPaths.Distances
import ShortestPaths.Hops
import com.instagraph.SparkTest
import org.apache.spark.graphx.lib.ShortestPaths.SPMap
import org.apache.spark.graphx.{Edge, Graph, VertexId, VertexRDD}
import org.scalatest.flatspec.AnyFlatSpec

class ShortestPathsTest extends AnyFlatSpec with SparkTest {

  private val vertices = sparkContext.makeRDD(Array(
    (1L, "A"), (2L, "B"), (3L, "C"), (4L, "D"), (5L, "E"), (6L, "F"), (7L, "G"))
  )

  private val edges = sparkContext.makeRDD(Array(
    Edge(1L, 2L, 7.0), Edge(1L, 4L, 5.0), Edge(2L, 3L, 8.0), Edge(2L, 4L, 9.0),
    Edge(2L, 5L, 7.0), Edge(3L, 5L, 5.0), Edge(4L, 5L, 15.0), Edge(4L, 6L, 6.0),
    Edge(5L, 6L, 8.0), Edge(5L, 7L, 9.0), Edge(6L, 7L, 11.0))
  )

  private val graph = Graph(vertices, edges)

  "Shortest paths from A" should "be equal to the expected minimum distances" in {
    val solution = Map(
      1L -> (0.0, List(1L)),
      2L -> (7.0, List(1L, 2L)),
      3L -> (15.0, List(1L, 2L, 3L)),
      4L -> (5.0, List(1L, 4L)),
      5L -> (14.0, List(1L, 2L, 5L)),
      6L -> (11.0, List(1L, 4L, 6L)),
      7L -> (22.0, List(1L, 4L, 6L, 7L))
    )
    assert(graph.allPairsShortestPath().shortestPathsMapFrom(1L) == solution)
  }

  "Dijkstra and fewest hops" should "coincide for unitary edges graphs" in {
    val unitaryGraph: Graph[String, Double] = Graph(vertices, edges.map(e => Edge(e.srcId, e.dstId, 1.0)))

    val hopsVertices: List[(VertexId, VertexId, Int)] = unitaryGraph.fewestHops(1L to 7L)
      .vertices
      .collectAsMap()
      .flatMap { case (origin, spMap) => spMap.map { case (destination, cost) => (origin, destination, cost) } }
      .toList

    val spVertices: List[(VertexId, VertexId, Int)] = unitaryGraph.allPairsShortestPath()
      .toShortestPathMap
      .flatMap { case (origin, spMap) => spMap.map { case (destination, info) => (origin, destination, info.totalCost.toInt) } }
      .toList

    hopsVertices.foreach(v => assert(spVertices.contains(v)))
  }
}
