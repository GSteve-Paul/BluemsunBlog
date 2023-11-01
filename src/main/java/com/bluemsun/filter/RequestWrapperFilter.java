package com.bluemsun.filter;

import com.bluemsun.util.RequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter("/*")
@Order(2)

public class RequestWrapperFilter implements Filter
{
    String[] ignore = new String[]{"file"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("RequestWrapperFilter init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        for (String s : ignore) {
            if (s.equals(req.getRequestURI().split("/")[1])) {
                filterChain.doFilter(req, resp);
                return;
            }
        }
        ServletRequest requestWrapper = new RequestWrapper(req);
        filterChain.doFilter(requestWrapper, resp);
    }

    @Override
    public void destroy() {
        log.info("RequestWrapperFilter destroy");
    }
}
