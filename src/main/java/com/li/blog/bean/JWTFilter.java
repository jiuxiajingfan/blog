package com.li.blog.bean;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.li.blog.enums.ResultStatus;
import com.li.blog.util.JwtUtils;
import com.li.blog.util.RedisUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;


/**
 * @ClassName JWTFilter
 * @Description TODO
 * @Author Nine
 * @Date 2022/10/19 23:17
 * @Version 1.0
 */
@Slf4j
public class JWTFilter extends BasicHttpAuthenticationFilter {

    /**
     * 尝试登录并且访问接口
     * 检测header里面是否包含Authorization字段即可
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("Authorization");
        return token != null;
    }

    /**
     * 执行账号密码登录功能
     */
    @Override
    protected boolean executeLogin(ServletRequest httpServletRequest, ServletResponse httpServletResponse) throws AuthenticationException{
        HttpServletRequest request = (HttpServletRequest) httpServletRequest;
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        JwtUtils jwtUtils = getBean(JwtUtils.class, request);
        RedisUtils redisUtils = getBean(RedisUtils.class, request);
        // 判断是否已过期
        Claims claim = jwtUtils.getClaimByToken(authorization);
        if (claim == null) {
            throw new AuthenticationException("登录有效期已过,请重新登录！");
        }
        if (jwtUtils.isTokenExpired(claim.getExpiration())) {
            throw new AuthenticationException("登录有效期已过,请重新登录！");
        }
        JWTToken token = new JWTToken(authorization);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage());
        }
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse response, Object mappedValue) {
        HttpServletResponse res = WebUtils.toHttp(response);
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        WebApplicationContext ctx = RequestContextUtils.findWebApplicationContext(request);
        RequestMappingHandlerMapping mapping = ctx.getBean("requestMappingHandlerMapping",
                RequestMappingHandlerMapping.class);
        try {
            HandlerExecutionChain handler = mapping.getHandler(request);
            HandlerMethod handlerClass = (HandlerMethod) handler.getHandler();
            Method method = handlerClass.getMethod();
            UnCheck annotation = AnnotationUtils.findAnnotation(method, UnCheck.class);
            if (annotation != null) {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //未携带token拒绝访问
        if (!isLoginAttempt(request, response)) {
            return false;
        }
        try {
            executeLogin(servletRequest, response);
            return true;
        } catch (AuthenticationException e) {
            response401(response,e.getMessage());
        }
        return false;
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        return super.preHandle(request, response);
    }

    /*
     * 如果没有去除将会循环调用doGetAuthenticationInfo方法
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        this.sendChallenge(request, response);
        return false;
    }

    public <T> T getBean(Class<T> clazz, HttpServletRequest request) {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        return applicationContext.getBean(clazz);
    }


    /**
     * 直接返回Response err信息
     */
    private void response401(ServletResponse response, String msg) {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        try (PrintWriter out = httpServletResponse.getWriter()) {
            R<String> error = R.error(msg, ResultStatus.ACCESS_DENIED.getStatus());
            ObjectMapper objectMapper = new ObjectMapper();
            String data = objectMapper.writeValueAsString(error);
            out.append(data);
        } catch (IOException e) {
            log.error("直接返回Response信息出现IOException异常:{}", e.getMessage());
        }
    }
}
