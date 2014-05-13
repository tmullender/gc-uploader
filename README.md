#Garmin Connect Uploader

As a Garmin device user

I would like activities on my device to be automatically uploaded when I connect it to my PC

So that I don't have to visit the website and manually upload the files

##User Guide

###Getting Started

1. Download the zip file
2. Unpack the zip file
3. Create a configuration file
4. Run the appropriate script

  **Windows**

    bin\gc-uploader.bat <path to config file>

  **Unix**

    bin/gc-uploader <path to config file>

###Configuration

The app is configured using a properties file, an example is shown below with the default values used.

    username=
    #password=

    #acceptable.extensions=fit,gtx,tcx
    #upload.new.directory=new
    #upload.complete.directory=complete
    #upload.error.directory=error

    #new.file.check.interval=5
    #http.connect.timeout=30000

The default location of the properties file, **config.properties**, is within _<User's home directory>_/gc-uploader

|Property|Description|
|:------------:|:-----------|
|username|The username used to login to Garmin Connect, this is **mandatory**|
|password|The password used to login to Garmin Connect, this will be prompted for if not specified assuming that a console is
available.  As the properties file is plain text, it's access should be restricted if it contains a password|
|acceptable.extensions|The file extensions that represent files that should be uploaded to Garmin Connect|
|upload.new.directory|The directory in which to look for new files|
|upload.complete.directory|The directory to move files to once they have been successfully uploaded|
|upload.error.directory|The directory to move files to if there is an error during the upload|
|new.file.check.interval|The interval in minutes between checks to see if there are new files to upload|
|http.connect.timeout|The number of milliseconds to wait for a request to Garmin Connect|

###What happens next
* The app will check the _upload.new.directory_ every _new.file.check.interval_ minutes for files with an extension in
_acceptable.extensions_.
* If files are found the app will login to Garmin Connect
* If successful the app will attempt to upload the files
* If the file is uploaded successfully it will be moved to _upload.complete.directory_
* If the upload fails the file will be moved to _upload.error.directory_