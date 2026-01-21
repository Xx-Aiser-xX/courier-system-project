package org.example.couriers.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(2)
public class PerformanceWarningFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(PerformanceWarningFilter.class);
    private static final long WARN_THRESHOLD_MS = 100;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        finally {
            long duration = System.currentTimeMillis() - startTime;
            HttpServletRequest request = (HttpServletRequest) servletRequest;

            if (duration > WARN_THRESHOLD_MS) {
                log.warn("запрос занял много времени: {} {} занял {}ms",
                        request.getMethod(),
                        request.getRequestURI(),
                        duration);
            }
        }
    }
}