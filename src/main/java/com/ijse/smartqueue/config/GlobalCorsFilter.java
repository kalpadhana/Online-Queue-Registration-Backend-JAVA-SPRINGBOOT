package com.ijse.smartqueue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class GlobalCorsFilter implements WebMvcConfigurer {

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
                String origin = request.getHeader("Origin");
                
                // Allow specific origins
                if (origin != null && (origin.contains("localhost:5173") || 
                    origin.contains("localhost:3000") || 
                    origin.contains("localhost:5174") ||
                    origin.contains("localhost:63342"))) {
                    response.setHeader("Access-Control-Allow-Origin", origin);
                }
                
                response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
                response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept, Origin");
                response.setHeader("Access-Control-Expose-Headers", "*");
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Max-Age", "3600");
                
                // Handle preflight requests
                if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    return false;
                }
                
                return true;
            }
        }).addPathPatterns("/**");
    }
}
