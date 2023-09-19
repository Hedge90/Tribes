package com.greenfoxacademy.springwebapp.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

@Component
public class LogInterceptor implements HandlerInterceptor {
    private Logger logger = LoggerFactory.getLogger(LogInterceptor.class);


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        Map requestParamMap = request.getParameterMap();
        String requestParameters = null;
        for (Object key : requestParamMap.keySet()) {
            String keyStr = (String) key;
            String[] value = (String[]) requestParamMap.get(keyStr);
            requestParameters = key + ": " + Arrays.toString(value);
        }
        if (response.getStatus() >= 400) {
            logger.error(buildLogMessage(request, response, requestParameters));
        } else {
            logger.info(buildLogMessage(request, response, requestParameters));
        }
    }

    private String buildLogMessage(HttpServletRequest request, HttpServletResponse response, String requestParameters) {
        if (requestParameters == null) {
            requestParameters = "";
        }
        return "Request method: " + request.getMethod() + ", Endpoint uri: " + request.getServletPath() + ", Request parameters: " + requestParameters + ", Status code: " + response.getStatus();
    }
}