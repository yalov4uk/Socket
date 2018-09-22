Client-server application for communication by sockets.
<br>
_Server_ can serve multiple clients in different threads.
<br>
_ClientHandler_ holds thread specific information, for example, message id.
<br>
Package _calculator_ contains _Server'_ business logic: calculating result of numeric expression implemented by reverse polish notation.
<br>
There is simple test _Main_ where server and several clients start in separate threads. Clients send requests and receive responses by sockets. _Server_ logs data.
<br>
Screenshots:
![Alt text](/image/Screenshot_test.png?raw=true "Test screenshot")
