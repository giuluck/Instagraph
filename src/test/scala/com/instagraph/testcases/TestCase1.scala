package com.instagraph.testcases

import com.instagraph.SparkConfiguration._
import com.instagraph.paths.allpairs.adjacents.EachPathWeightedInfo
import com.instagraph.paths.allpairs.fullroutes.EachFullRouteInfo
import org.apache.spark.graphx.Edge

object TestCase1 extends TestCase[Int](
  graph = createGraph(sparkContext.makeRDD(
    Seq((0, 1), (0, 2), (1, 2), (1, 3), (2, 3), (3, 4))
      .map { case (source, destination) => Edge(source.toLong, destination.toLong, (destination - source)) }
      ++ Seq(Edge(4L, 0L, 1))
  )),
  adjacentsSolutions = Map(
    0L -> Map(
      0L -> EachPathWeightedInfo(0, Map.empty),
      1L -> EachPathWeightedInfo(1, Map(1L -> 1)),
      2L -> EachPathWeightedInfo(2, Map(2L -> 1, 1L -> 1)),
      3L -> EachPathWeightedInfo(3, Map(2L -> 1, 1L -> 2)),
      4L -> EachPathWeightedInfo(4, Map(2L -> 1, 1L -> 2))
    ),
    1L -> Map(
      0L -> EachPathWeightedInfo(4, Map(3L -> 1, 2L -> 1)),
      1L -> EachPathWeightedInfo(0, Map.empty),
      2L -> EachPathWeightedInfo(1, Map(2L -> 1)),
      3L -> EachPathWeightedInfo(2, Map(3L -> 1, 2L -> 1)),
      4L -> EachPathWeightedInfo(3, Map(3L -> 1, 2L -> 1))
    ),
    2L -> Map(
      0L -> EachPathWeightedInfo(3, Map(3L -> 1)),
      1L -> EachPathWeightedInfo(4, Map(3L -> 1)),
      2L -> EachPathWeightedInfo(0, Map.empty),
      3L -> EachPathWeightedInfo(1, Map(3L -> 1)),
      4L -> EachPathWeightedInfo(2, Map(3L -> 1))
    ),
    3L -> Map(
      0L -> EachPathWeightedInfo(2, Map(4L -> 1)),
      1L -> EachPathWeightedInfo(3, Map(4L -> 1)),
      2L -> EachPathWeightedInfo(4, Map(4L -> 2)),
      3L -> EachPathWeightedInfo(0, Map.empty),
      4L -> EachPathWeightedInfo(1, Map(4L -> 1))
    ),
    4L -> Map(
      0L -> EachPathWeightedInfo(1, Map(0L -> 1)),
      1L -> EachPathWeightedInfo(2, Map(0L -> 1)),
      2L -> EachPathWeightedInfo(3, Map(0L -> 2)),
      3L -> EachPathWeightedInfo(4, Map(0L -> 3)),
      4L -> EachPathWeightedInfo(0, Map.empty)
    )),
  fullRoutesSolutions = Map(
    0L -> EachFullRouteInfo(0, Set(List(0L))),
    1L -> EachFullRouteInfo(1, Set(List(0L, 1L))),
    2L -> EachFullRouteInfo(2, Set(List(0L, 2L), List(0L, 1L, 2L))),
    3L -> EachFullRouteInfo(3, Set(List(0L, 1L, 3L), List(0L, 2L, 3L), List(0L, 1L, 2L, 3L))),
    4L -> EachFullRouteInfo(4, Set(List(0L, 1L, 3L, 4L), List(0L, 2L, 3L, 4L), List(0L, 1L, 2L, 3L, 4L)))
  )
)
