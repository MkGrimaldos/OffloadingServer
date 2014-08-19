OffloadingServer
================

This is the server-side of the MSc thesis "Computation Offloading from an Android Device".
It's main function is receiving the petition (and maybe a video file) from the client-side, transcode the video and return the result back to the client.

How to configure it
-------------------

It is possible to change the port the socket is listening to by changing the line `new ServerSocket(8888);`

By changing the "OPTION" constant, at the beginning of the code, selecting 1 or 2, the program will assume the video file is already stored on the Server, or it will wait for the client to send it respectively. This option needs to be consistent with the option picked in the server-side.
