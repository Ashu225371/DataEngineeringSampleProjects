package com.capital.bikesharing

import org.apache.spark.sql.{SparkSession, functions}
import org.apache.spark.sql.functions._

object Test {
  def main(args: Array[String]): Unit = {
    val sparkSession=SparkSession.builder().getOrCreate()
    val sourceData=sparkSession.read.option("header","true").csv(args(0))
    val maxValue=sourceData.select(functions.max(col("Duration")).as("mymax"))
    sourceData.join(maxValue,col("Duration")===col("mymax")).drop("mymax").show()
  }
}
