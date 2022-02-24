package com.capital.bikesharing.utils

import java.io.File
import java.net.URL
import java.nio.file.{Files, Path}
import java.util.zip.ZipFile

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.capital.bikesharing.utils.Constant.{AWS_ACCESS_KEY, AWS_SECRET_KEY, BUCKET_NAME}
import org.apache.commons.io.FileUtils

import scala.io.Source
import scala.xml.XML
import scala.collection.JavaConverters._

object DownloadUtils {

  def downloadAndUzipDataFromSourceURL(downloadUrl:String,downloadDirectory:String,unzipDirectory:String): Unit = {
    XML.loadString(Source.fromURL(downloadUrl).mkString)
      .child.filter(x=>x.child.length>0)
      .filter(x=>x.child(0).label.equalsIgnoreCase("Key"))
      .map(x=>(x.child(0).text,Constant.FTP_PATH+x.child(0).text))
      .foreach(x=>{
        println("Downloading file "+x._1+" ......")
        FileUtils.copyURLToFile(new URL(x._2),new File(downloadDirectory+x._1))
        println("Download completed for "+x._1+" ......")
        println("Unzipping started for "+x._1+" ......")
        val extractedFilePath=unzip(new File(downloadDirectory+x._1).toPath,new File(unzipDirectory).toPath)
        uploadToS3(extractedFilePath)
        println("Unzipping completed for "+x._1+" ......")
      })

  }

  def unzip(zipPath: Path, outputPath: Path): File = {
    val zipFile = new ZipFile(zipPath.toFile)
    var path:Path=null
    for (entry <- zipFile.entries.asScala) {
      path = outputPath.resolve(entry.getName)
      if (entry.isDirectory) {
        Files.createDirectories(path)
      } else {
        Files.createDirectories(path.getParent)
        Files.copy(zipFile.getInputStream(entry), path)
      }
    }
    path.toFile
  }

  def uploadToS3(fileToUpload:File): Unit = {
    val yourAWSCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY)
    val amazonS3Client = new AmazonS3Client(yourAWSCredentials)
    amazonS3Client.putObject(BUCKET_NAME, "BikeRawData/"+fileToUpload.getName, fileToUpload)
  }

}
