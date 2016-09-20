# Bophelo-Haeso-Open
Haeso API Technical Documentation <br/>
<h1>Setting up the API</h1>
The API is currently fully implemented in its own package called HaesoAPI. <br/>

<b>To use the API:</b>

1)	Download the package from https://github.com/Shaaheen/Bophelo-Haeso-Open/archive/master.zip <br/>
2)	Place the Haeso API folder in your desired Project folder <br/>
3)	Import classes from the Haeso API eg. “import capstone.bophelohaesoopen.HaesoAPI”

<h1>Initializing API</h1> <br/>
Before calling on the API functionality, some optional configurations can be set for the API.<br/>
<b>App Folder configuration</b><br/>
The folder names can be specified so the API knows what folders to save files in. <br/>
To do any file management, an instance of FileUtils must be created: <br/>

  FileUtils fileUtils = new FileUtils(this);

Thereafter, to set the app folder names, call the setFolderNames method to set the static folder names in the API: <br/>
  fileUtils.setFolderNames(AppName, VideosFolder, RecordingsFolder, ImageFolder); <br/>
Set the app identifier prefix<br/>
The API distinguishes related app files with a specified prefix. This prefix must be set before use of the API or else all media files on the device would be recognized as App files. <br/>
To set the identifier prefix: <br/>
Media.setIdentifierPrefix(identifierPrefix);<br/>

<h1>Getting specific media files from device</h1><br/>
To retrieve the files of app specific media files, the FileUtils class must be used. Use this method with the app prefix and the extension of the type of file you want to retrieve. <br/>
ArrayList<? extends Media> getMediaCollectionFromStorage(String prefix, String extension)<br/>
Eg:  <br/>
  videoList = (ArrayList<Video>) fileUtils.getMediaCollectionFromStorage("chw_", Video.mediaExtension); <br/>
  
<h1>Media Playback</h1> <br/>
To play media back using the API, First initialize the MediaPlayer class and then use the play media method : <br/>
mediaPlayer = new MediaPlayer(MediaPlayerName); <br/>
playMedia(Media mediaFile, SurfaceView mediaView, final int screenWidth, final int screenHeight) <br/>
Eg:
mediaPlayer = new MediaPlayer("BHO"); <br/>
mediaPlayer.playMedia(video, videoView, width, height);  <br/>

<h1>Logging</h1> <br/>
Logging in the API is done through the use of a SQLiteDatabase in the app. To start logging the database first needs to be initialized: <br/>
  databaseUtils = new DatabaseUtils(this);<br/>
To log a specific action, create a log entry and then pass it the databaseUtils instance. <br/>
Also, if an instance of database is created then this instance can be retrieved using static methods hence LogEntry’s can be made from any class if the database has been created.<br/>
Eg: <br/>
LogEntry logEntry = new LogEntry(LogEntry.LogType.PAGE_VISITS, "Main Screen", null); <br/>
        If (DatabaseUtils.isDatabaseSetup()) <br/>
        { <br/>
            DatabaseUtils.getInstance().addLog(logEntry); <br/>
        } <br/>
        
<h1>Bluetooth Sharing<br/> </h1>
<b> Initialization<br/> </b>
Bluetooth Sharing in the API is done mainly through event listeners. The BluetoothUtils class needs to first be initialized with the current Views context and activity instance: <br/>
BluetoothUtils( Context context, Activity activity ) <br/>
Then the BluetoothListener needs to be implemented so the View can react to bluetooth events. All the interface methods need to be implemented. <br/>
Eg: <br/>
bluetoothUtils.bluetoothListener = new BluetoothListener() <br/>
        {
            @Override
            public void onStartScan(){}

            @Override
            public void onStopScan() {}

            @Override
            public void onBTDevicesFound(final List<BluetoothUtils.BTDevice> btDevices)	{}

            @Override
            public void onConnected(){}

            @Override
            public void onDisconnected(){}

            @Override
            public void onStartReceiving(){}

            @Override
            public void onStartSending() {}

            @Override
            public void onReceivingProgress(double progress)
            { }

            @Override
            public void onSendingProgress(String progress) {}

        };
<b>Connecting </b><br/>
The BluetoothUtils method startScanning() must be called to initialize the scanning for surrounding devices search. Then the onBTDevicesFound method must be implemented in the Bluetooth listener to be able to allow the user to choose from the given list of devices. <br/>
To connect to a device, the connectToAddress method needs to be called on the mac address of one of the found devices. <br/>
Eg: bluetoothUtils.connectToAddress(btDevices.get(0).getAddress()); <br/>
<b>Sending </b><br/>
To send Media through the BluetoothUtils after the connection is successful, the sendMedia method is used:<br/>
sendMediaFile(Media mediaFile) <br/>
eg: <br/>
bluetoothUtils.sendMedia(Media mediaFile); <br/>

<h1>Recording</h1><br/>
The Audio recorder needs to be initialized: <br/>
audioRecorder = new AudioRecorder(); <br/>
Then when need to record, call these methods to start:<br/>
audioRecorder.prepareForRecording();<br/>
audioRecorder.startRecording();<br/>
And then to stop recording:<br/>
audioRecorder.stopRecording();<br/>
It is also to be noted that duration limit may be set on the audio recorder to prevent long recordings:<br/>
audioRecorder.setRecordingDurationLimit(10000);


