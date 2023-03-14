# websocket-chat
Server should be started from ChatServer Main
Before running apply the following settings to IDE:

ChatClient -> Edit Configuration -> Modify Options -> Allow Multiple instances (should be on)
ChatClient -> Edit Configuration -> Modify Options -> Add VM Options -> should be set to --enable-preview
Preferences -> Java Compiler -> Additional Command Line Parameters -> should be set to --enable-preview

DO POPRAWY:

DONE -> Aktualnie cała logika siedzi w jednym pliku - warto to ładniej porozdzielać tak, jak jest w przypadku chat-server

File to przestarzała klasa w Javie do obsługi plików. Aktualnie zalecane jest stosowanie java.nio.files, czyli Path oraz Files.

W przypadku klienta warto również zaimplementować FileWatchera tak, żeby można było reagować na to, że pojawiły się nowe pliki w danym katalogu. Jest to opcjonalne usprawnienie, bo nie było tego w wymaganiach projektu :slightly_smiling_face:

DONE -> W przypadku metody processMenuCommand sugeruję zastosować pattern Command lub Strategy - aktualnie za dużo się dzieje w jendym miejscu :wink: