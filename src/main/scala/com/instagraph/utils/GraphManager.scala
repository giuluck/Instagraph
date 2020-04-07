package com.instagraph.utils

import com.instagraph.utils.MapUtils.Manipulations
import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SparkSession}

import scala.reflect.ClassTag

class GraphManager[E: ClassTag](spark: SparkSession) {
  private val sparkContext = spark.sparkContext
  sparkContext.setLogLevel("ERROR")

  def build(edgeWeight: Seq[String] => E, jsonPaths: String*): Graph[String, E] = {
    val dfSeq = jsonPaths.map(spark.read.json(_))
    val users = dfSeq.flatMap(_.columns).distinct
    // Vertices of the graph are the users identified with an ID
    val vertices: RDD[(VertexId, String)] = sparkContext.parallelize(
      users.zipWithIndex.map { case (user, idx) => (idx.toLong, user) }
    )
    // Extract the ID of a vertex given the username
    val correspondence: String => VertexId = user => vertices.filter(v => v._2 == user).first()._1
    // Edges of the graph are following relations between any pair of vertices
    val edges: RDD[Edge[E]] = sparkContext.parallelize(
      dfSeq.map(df => (df.head(), df.columns))
        .map { case (row, users) => row.getValuesMap[Seq[String]](users) }
        .reduce((map1, map2) => map1.merge(map2, (user, seq1, seq2) => seq1))
        .mapValues[Seq[String]](_.filter(users.contains)) // Don't consider following outward users
        .flatten { case (user, following) =>
          following.map((user, _, edgeWeight(following))) // Convert map into triplets representing edges
        }
        .map(e => Edge(correspondence(e._1), correspondence(e._2), e._3))
        .toSeq
    )
    Graph(vertices, edges)
  }

  def load(path: String): Graph[String, E] = {
    val vertices = sparkContext.objectFile[(VertexId, String)](path + "/vertices")
    val edges = sparkContext.objectFile[Edge[E]](path + "/edges")
    Graph(vertices, edges)
  }

  def save(graph: Graph[String, E], path: String): Unit = {
    graph.vertices.saveAsObjectFile(path + "/vertices")
    graph.edges.saveAsObjectFile(path + "/edges")
  }
}
