package com.heng.hengapicommon.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class JwtUtils {
    public static final long EXPIRE = 1000 * 60 * 60 * 24; // 过期时间 1天
    public static final String APP_SECRET = "sad8485ad64sa5da6d8asd415a64d"; // 秘钥

    /**
     * 根据id和name获取token
     * @param id
     * @param userName
     * @return
     */
    public static String getJwtToken(Long id, String userName){
        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                //设置参数
                .claim("id", id)
                .claim("userName", userName)
                //设置荷载
                .setSubject("api-user")                    //用于对数据进行分类
                .setIssuedAt(new Date())                    //设置数据发布(创建)时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))  //设置数据的过期时间
                //设置签名
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();

        return token;
    }

    /**
     * 获取用户id
     * @param request
     * @return
     */
    public static Long getUserIdByToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if (cookie.getName().equals("token")){
                String value = cookie.getValue();
                if (!StringUtils.isBlank(value)){
                    try {
                        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(value);
                        Claims body = claimsJws.getBody();
                        return Long.parseLong(body.get("id").toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 检测token是否没过期
     * @param token
     * @return
     */
    public static boolean checkToken(String token){
        if (StringUtils.isBlank(token)){
            return false;
        }
        try{
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
