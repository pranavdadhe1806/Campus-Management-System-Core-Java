package com.collegeapp.http;

import com.collegeapp.model.Course;
import com.collegeapp.service.CourseService;
import com.collegeapp.service.ServiceException;
import com.collegeapp.util.LoggerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class HttpServerApp {

    public static final int DEFAULT_PORT = 8080;

    private final CourseService courseService;

    public HttpServerApp() {
        this(new CourseService());
    }

    public HttpServerApp(CourseService courseService) {
        this.courseService = courseService;
    }

    public HttpServer start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", this::health);
        server.createContext("/courses", this::courses);
        server.start();
        LoggerUtil.info("HTTP server started on port " + port);
        return server;
    }

    private void health(HttpExchange exchange) throws IOException {
        writeJson(exchange, 200, "{\"status\":\"UP\"}");
    }

    private void courses(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            writeJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }
        try {
            String json = courseService.listCourses().stream()
                    .map(this::courseJson)
                    .collect(Collectors.joining(",", "[", "]"));
            writeJson(exchange, 200, json);
        } catch (ServiceException e) {
            writeJson(exchange, 500, "{\"error\":\"" + escape(e.getMessage()) + "\"}");
        }
    }

    private String courseJson(Course course) {
        return String.format(
                "{\"courseId\":%d,\"courseCode\":\"%s\",\"courseName\":\"%s\",\"credits\":%d,\"semester\":%d}",
                course.getCourseId(), escape(course.getCourseCode()), escape(course.getCourseName()),
                course.getCredits(), course.getSemester());
    }

    private static void writeJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, response.length);
        try (OutputStream body = exchange.getResponseBody()) {
            body.write(response);
        }
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        new HttpServerApp().start(port);
    }
}
