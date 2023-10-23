package com.bluemsun.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter("/*")
@Order(1)
public class CORSFilter implements Filter
{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("CORSFilter init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        /*
        解决跨域问题
         */
        //允许跨域的域名，*号为允许所有。解决跨域访问报错
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        //服务器支持的所有头信息字段
        resp.setHeader("Access-Control-Allow-Headers",
                "Origin," +
                        "Access-Control-Request-Headers," +
                        "Access-Control-Allow-Headers," +
                        "DNT," +
                        "X-Requested-With," +
                        "X-Mx-ReqToken," +
                        "Keep-Alive," +
                        "User-Agent," +
                        "X-Requested-With," +
                        "If-Modified-Since," +
                        "Cache-Control," +
                        "Content-Type," +
                        "Accept," +
                        "Connection," +
                        "Cookie," +
                        "X-XSRF-TOKEN," +
                        "X-CSRF-TOKEN," +
                        "Authorization");
        //将Cookie发到服务端，需要指定Access-Control-Allow-Credentials为true;
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        //首部字段 Access-Control-Allow-Methods 表明服务器允许客户端使用 POST, GET 和 OPTIONS 方法发起请求
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        //设置过期时间，这里设置响应最大有效时间为 86400 秒，即24 小时
        resp.setHeader("Access-Control-Max-Age", "86400");

        /*
        解决输入流问题req.body
         */
        req.setCharacterEncoding("UTF-8");
        filterChain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
        log.info("CORSFilter destroy");
    }
}
