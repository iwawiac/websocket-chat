# websocket-chat


Description: WebSocket chat application written in pure Java without the use of any external libraries/ frameworks. Contains lower-level implementation of communication & handshake protocols, data frame handling and encoding/ decoding mechanisms according to RFC 6455 The WebSocket Protocol.

Server should be started from ChatServer Main
Before running apply the following settings to IDE (IntelliJ Idea):

ChatClient -> Edit Configuration -> Modify Options -> Allow Multiple instances (should be on)
ChatClient -> Edit Configuration -> Modify Options -> Add VM Options -> should be set to --enable-preview
Preferences -> Java Compiler -> Additional Command Line Parameters -> should be set to --enable-preview
