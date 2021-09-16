package bentolor.sandbox;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;

import java.net.InetSocketAddress;
import java.nio.file.Path;

public class MyWebServer {
    public static void main(String[] args) {
        HttpServer server = SimpleFileServer.createFileServer(
                new InetSocketAddress(8080),
                Path.of("/tmp"),
                SimpleFileServer.OutputLevel.VERBOSE);
        server.start();
    }
}
