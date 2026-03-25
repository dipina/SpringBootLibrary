package com.example.restapi.performance;

import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestActiveConfig;
import com.github.noconnor.junitperf.JUnitPerfInterceptor;
import com.github.noconnor.junitperf.JUnitPerfReportingConfig;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.restapi.service.BookService;
import com.example.restapi.service.BorrowingService;
import com.example.restapi.service.UserService;

@SpringBootTest
@ExtendWith(JUnitPerfInterceptor.class)
public class PerformanceTest {

    @Autowired
    private BookService bookService;
    
    @Autowired
    private BorrowingService borrowingService;
    
    @Autowired
    private UserService userService;
    
    // Should be static or new instance will be created for each @Test method
    @JUnitPerfTestActiveConfig
    private final static JUnitPerfReportingConfig PERF_CONFIG = JUnitPerfReportingConfig.builder()
            .reportGenerator(new HtmlReportGenerator(System.getProperty("user.dir") + "/target/reports/perf-report.html"))
            .build();


    @BeforeEach
    void setup() {
        // Optional setup before each test if required
    }
    
    // Test getAllBooks() with a throughput of 50 requests per second for 10 seconds
    @Test
    @JUnitPerfTest(threads = 10, durationMs = 10000, warmUpMs = 2000)
    @JUnitPerfTestRequirement(executionsPerSec = 50, percentiles = "95:300ms")
    public void testGetAllBooksPerformance() {
        bookService.getAllBooks();
    }
    
    // Test getAllBorrowings() with 20 concurrent users, running for 5 seconds
    @Test
    @JUnitPerfTest(threads = 20, durationMs = 5000, warmUpMs = 1000)
    @JUnitPerfTestRequirement(executionsPerSec = 50, percentiles = "95:300ms")
    public void testGetAllBorrowingsPerformance() {
        borrowingService.getAllBorrowings();
    }
    
    @Test
    @JUnitPerfTest(threads = 100, durationMs = 15000, warmUpMs = 3000)
    @JUnitPerfTestRequirement(
        percentiles = "90:100ms,95:150ms,98:200ms,99:250ms",
        executionsPerSec = 100,
        allowedErrorPercentage = 0.10f
    )
    public void testGetAllUsersPerformance() {
        userService.getAllUsers();
    }
}
