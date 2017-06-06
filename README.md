# COMP7506 Smart Phone Apps Development                        -Assignment 2
#               Cloud Video Album (Option 1)

## Main Features
### 1.Video capture and Rename
At the bottom of the screen (Figure 1), there are three buttons. Every button has two states, static state and pressed state, for better interaction with users. Click the central video button to start video capture. To enable users to customize their video name for convenient recognition, this application provides a dialog to rename their video before recording (Figure 2). After recording, a dialog will show up to tell user whether the operation is successful and then automatically turn back to the refreshed list of album. (Figure 3).
# ![Figure1](https://github.com/1040870658/Cloud_Album/raw/master/pics/figure1.png)
  ![Figure2](https://github.com/1040870658/Cloud_Album/raw/master/pics/figure2.png)
### 2.Manually upload
At the right side of bottom screen, click the upload button, then a webview will be opened (Figure 4). Select a file then click “Here” to upload file to the server.
# ![Figure3](https://github.com/1040870658/Cloud_Album/raw/master/pics/figure3.png)
 ![Figure4](https://github.com/1040870658/Cloud_Album/raw/master/pics/figure4.png)

### 3.Display a list of uploaded videos
In Figure 1, at the top there is a list showing which .mp4 files are stored at server side. All the data are put into a RecyclerView so that user can scroll up and down when the view are completely filled with records.

### 4.Select and play the video
When user chooses a specific video, a new activity will be created. A progress bar (Figure 5) will run before system is ready to play video. If the video view is ready, it will automatically play and the view is located at the top (Figure 6). Also user can rotate the screen, when the screen is horizontal, the video view will automatically change to full screen (Figure 7). Finally, users are able to pause/start the video by click the control bar in the bottom of the video player. (Figure 8).

# ![Figure5](https://github.com/1040870658/Cloud_Album/raw/master/pics/figure5.png)
# ![Figure6](https://github.com/1040870658/Cloud_Album/raw/master/pics/figure6.png)
# ![Figure7](https://github.com/1040870658/Cloud_Album/raw/master/pics/figure7.png)
# ![Figure8](https://github.com/1040870658/Cloud_Album/raw/master/pics/figure8.png)

### 5.Switch to play another selected video
In Figure 5 and Figure 6, there is a viewPager showing which video is playing right now and user can also scroll left or right to switch to another video and play it.

### Additional Features
1.Automatically upload after recording
In Figure 3, actually there is a service running background, when user sees the dialog “recorded”. It means not only that the video is captured but also that the captured video has uploaded to server. 

2.Synchronization between mobile and server
This function provides user with a chance to check whether the captured video file has been uploaded to server. After the file is uploaded, no matter it is done manually or automatically, a background service has actually run and updated data at mobile side so that the list of uploaded video is always up to date. In addition, in Figure 1 user may also click refresh button at the left side of bottom screen to get the latest list of uploaded video from server in case of Internet problem.

3.Nice control panel for switching video
    This control panel provides popular interactive switcher to set different videos to be played while the player is working. User can drag the panel to select a video and the panel react by automatically adjust the display position while changing the style of selected video label.

### Implementation methods
1.Automatically upload
There is an Activity called VideoCaptureActivity responsible for video capture and automatically upload. Each time the video has been created, in onActivityResult() method a thread will start to run upload service.

HttpRequest is used to upload files. We need to write the file stream into DataOutputStream so that at server side we can receive the file then save it.

2.Play and switch video album

Video Player:
Classical combination of VideoView and MediaController are selected to implement the function of video player. And for achieving better user interaction. We mainly implement two additional functions: Loading dialog and auto transformation of full screen and normal screen.


    Loading dialog: SetOnPreParedListener is made use to auto start the video once the resources are all loaded by video view. During preparation, a progress dialog is displayed to indicate users to wait for loading.




OnConfigurationChanged Method in Display Activity, which is responsible for displaying video GUI, is override to achieve full screen display when users rotate the phone to horizontal orientation. When video  finishes displaying or users rotate back to normal vertical orientation, the size of player returns to be normal and the control panel appears again. This is implemented by means of setting a OnCompletionListener in videoview.

Video Switcher:


To provide nice and flexible control panel, viewPager is chosen to implement the video switcher so that users can drag and select a new video in a comfortable way. The key of viewPager is to customize the PagerAdapter. By overriding the getPageWidth() method, each selection occupies 50% width space of the current page so that 2 selections will be displayed in a page. 

3.Synchronization between mobile and server


To complete this task, we need to build a communication between server side and mobile device, so the first thing comes into my mind is Json. At server side we query all the records from database including file name and timestamp, then encode to Json and response to mobile device. At mobile device, we get Json and parse them in onResume() method so that the method will run and synchronize data when the AlbumListActivity comes to foreground. 

4.Rename captured video


A dialog before recording pops up to ask for customize the video name. This dialog is initially assigned a name using the record time stamp so that users are not always necessary to enter custom name even when they do not like to get a name for the video. This is mainly implemented by using AlertDialog, with whose setView() methods, name editor layout can be customized inside the dialog to provide users with a EditText.

5.Automatically forward to the list after recording video




To provide better users friendly interaction. When users finish uploading video manually, the application is implemented to forward back to the album list interface. However, this is not easy achieve by only apply method in android terminal because of the completed encapsulation of   ValueCallback.OnReceiveValue(). So we achieve this by making use of Javascript in server to invoke the jumping logic in android side. This invocation is provided by webview.addJavaScriptinterface().



6.Architecture Design and Optimization
The application is globally divided into PHP server and android client. The android client can be further divided into background operation and UI display. We’ve try our best to dispatch correct behavior to them in reasonable way so that the coupling becomes as low as possible.
There are 4 classes maintaining the GUI of android: AlbumListActivity, DisplayActivity, BrowserActivity and VideoCaptureActivity while there is only one controller VideoManager to maintain the video synchronization between server and client and setup model for providing data to UI display.
Classes cooperate by means of handler- looper - message queue mechanism of android. Background operations are executed in controller with a child thread so that the GUI thread(MainThread) focus on drawing view without wasting time on logical operation in which may cause ANR .
And for displaying list of albums. We choose RecyclerView instead of ListView for better data caching and also for more flexible display structure. 
Finally, we cache the frequent-used image resources to memory by decoding in BitmapDrawable format and apply setImageDrawable instead of setImageResource so that the frequency of GC invocation reduce and provide better performance to the application.

Techniques
In addition to apply android techniques, we also try other useful techniques to make the application better.
For web (Figure 4) front end design, we mainly use the web framework bootstrap, which contains JavaScript, HTML and CSS-based templates, to make a nice user interface. The reason why we choose bootstrap is that it is compatible with the latest version of all popular browsers. Also, it brings us a series of nice-designed components. In addition to bootstrap, in order to make our upload page looks better, JavaScript is also implemented by us to present the information of uploading file.
For the server design, PHP is our major techniques. PHP is a server-side scripting language designed primarily for web development but also used as a general-purpose programming language.
For client – server communication, we use http get/post method and   android - JavaScript invocation setup.
The availability of database is another crucial factor. Therefore, we select MySQL, which is one of the most popular database techniques at the time. We use MySQL to store three main items: ID, NAME and TIME. 


Limitation
There is still one little problem has not been resolved. Although most of recorded videos can be play well, some of downloading videos on android emulator do not show. However, they work fine on our real phone. We assume it may have something to do with the video format. And indeed, this problem is in some way related to it as when we recoding several videos, they can be played again. But there are other videos cannot be resolved in this way. That is the hard work needs further efforts. 
Reference
1.  https://developer.android.com/guide/index.html
2.  http://www.w3schools.com/php/php_file_upload.asp
3.  http://v3.bootcss.com/css/
4.  http://www.w3schools.com/js/default.asp

