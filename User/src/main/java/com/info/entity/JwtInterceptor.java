package com.info.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)
            throws Exception {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
           writeError(response,401, "未登录");
           return false;
        }
        String token = header.substring(7);

        Integer userId = jwtUtil.getUserId(token);
        if (!jwtUtil.validateToken(token)) {
            writeError(response,401, "token验证失败");
            return false;
        }

        String username = jwtUtil.getUsername(token);
        UserContext.set(userId, username);

        return true;



    }



    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex)
            throws Exception {
        UserContext.clear();
    }



    private void writeError(HttpServletResponse response,int code, String message)throws Exception {

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(200);
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(code,message)));
    }


}
