Logback on the System Tray
==========================

This is a simple [Logback](http://logback.qos.ch/) extension for displaying log
messages as notifications on the system tray. For example, the following simple
configuration makes all warnings and errors show up as notifications:

```xml
<configuration>

  <appender name="TRAY" class="name.zitting.logbacktray.SystemTrayAppender">
    <tooltip>My Application</tooltip>
    <image>/path/to/my-application-logo.png</image>
  </appender>

  <root level="warn">
    <appender-ref ref="TRAY" />
  </root>

</configuration>
```

Each process with such configuration gets a separate system tray icon. If you
have lots of shortlived processes or would otherwise like to use just a single
tray icon for all log messages, you can use the `logback-tray-X.Y-server.jar`
to start a server for handling log messages sent to it using
`SocketAppender` configuration. For example:

    $ java -jar logback-tray-X.Y-server.jar 12345 tray-example.xml

The `tray-example.xml` configuration included in this directory is a simple
example of how such a tray notification server could be configured.

Each client application can then use the following configuration to send
events to such a system tray server:

```xml
  <appender name="TRAY" class="ch.qos.logback.classic.net.SocketAppender">
    <remoteHost>localhost</remoteHost>
    <port>12345</port>
  </appender>
```
