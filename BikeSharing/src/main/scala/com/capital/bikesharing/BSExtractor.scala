package com.capital.bikesharing

import com.capital.bikesharing.utils.DownloadUtils

object BSExtractor {

  def main(args: Array[String]): Unit = {
    DownloadUtils.downloadAndUzipDataFromSourceURL("https://s3.amazonaws.com/capitalbikeshare-data/",
      "C:\\tmp\\","C:\\tmp\\unzip\\")
  }

}
