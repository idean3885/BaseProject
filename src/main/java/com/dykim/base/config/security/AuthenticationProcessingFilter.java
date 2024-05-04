package com.dykim.base.config.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 *
 *
 * <h3>Authentication processing filter</h3>
 *
 * @author dongyoung.kim
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
public class AuthenticationProcessingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    public void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        var responseWrapper = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(request, responseWrapper);
            if (responseWrapper.getStatus() != HttpStatus.OK.value()) {
                log.error(
                        "Authentication failed. Request logging.\nRequest URI: [{}]{}\nheaders: {}\nrequestBody: {}",
                        request.getMethod(),
                        request.getRequestURI(),
                        getHeaderLog(request),
                        getRequestBody(request));

                // WWW-Authenticate
                var status = responseWrapper.getStatus();
                var responseHeader = getResponseHeader(responseWrapper);
                var body = responseWrapper.getContentAsByteArray();
                var bodyString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
                log.error(
                        "Response logging.\nstatus: {}\nheaders: {} \nresponseBody: {}",
                        status,
                        responseHeader,
                        bodyString);
                //                if (body.length == 0) {
                //                    ExceptionCode exceptionCode;
                //                    if (status == HttpStatus.UNAUTHORIZED.value()) {
                //                        exceptionCode = ExceptionCode.UNAUTHORIZED_USER;
                //                    } else {
                //                        log.error("Exception! responseBody is Null. Set 5001
                // Manually.");
                //                        exceptionCode =
                // ExceptionCode.INTERNAL_SERVER_ERROR_TOKEN_ISSUED;
                //                    }
                //                    var internalResponse = CommonResponse.error(exceptionCode);
                //
                // responseWrapper.setStatus(exceptionCode.getHttpStatus().value());
                //
                // responseWrapper.setContentType(MediaType.APPLICATION_JSON_VALUE);
                //                    responseWrapper.setCharacterEncoding("utf-8");
                //
                // responseWrapper.getWriter().write(objectMapper.writeValueAsString(internalResponse));
                //                }
            }
            responseWrapper.copyBodyToResponse();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //            ExceptionCode exceptionCode;
            //            if (responseWrapper.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
            //                exceptionCode = ExceptionCode.UNAUTHORIZED_USER;
            //            } else {
            //                log.error("Unknown exception occurred. Logging and set 5001
            // Manually.");
            //                exceptionCode = ExceptionCode.INTERNAL_SERVER_ERROR_TOKEN_ISSUED;
            //            }
            //            var internalResponse = CommonResponse.error(exceptionCode);
            //            response.setStatus(exceptionCode.getHttpStatus().value());
            //            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            //            response.setCharacterEncoding("utf-8");
            //
            // response.getWriter().write(objectMapper.writeValueAsString(internalResponse));
        }
    }

    private String getHeaderLog(HttpServletRequest request) {
        var headerNames = request.getHeaderNames();
        var headerMap = new HashMap<String, String>();
        while (headerNames.hasMoreElements()) {
            var key = headerNames.nextElement();
            var value = request.getHeader(key);
            headerMap.put(key, value);
        }
        String headerString;
        try {
            headerString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(headerMap);
        } catch (JsonProcessingException | RuntimeException e) {
            log.error(e.getMessage(), e);
            headerString = "parse exception";
        }
        return headerString;
    }

    private String getResponseHeader(HttpServletResponse response) {
        var headerNames = response.getHeaderNames();
        var headerMap = new HashMap<String, String>();
        for (var headerName : headerNames) {
            headerMap.put(headerName, response.getHeader(headerName));
        }
        String headerString;
        try {
            headerString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(headerMap);
        } catch (JsonProcessingException | RuntimeException e) {
            log.error(e.getMessage(), e);
            headerString = "parse exception";
        }
        return headerString;
    }

    private String getRequestBody(HttpServletRequest request) {
        try {
            var requestBody = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
            return StringUtils.isNoneBlank(requestBody) ? requestBody : "RequestBody is null.";
        } catch (RuntimeException | IOException e) {
            log.error(e.getMessage(), e);
            return "Failed get requestBody";
        }
    }
}
