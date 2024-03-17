package com.heng.project.service.impl;

import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.heng.hengapicommon.common.JwtUtils;
import com.heng.hengapicommon.model.entity.User;
import com.heng.project.common.ErrorCode;
import com.heng.project.constant.UserConstant;
import com.heng.project.exception.BusinessException;
import com.heng.project.mapper.UserMapper;
import com.heng.project.model.vo.LoginUserVO;
import com.heng.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * 用户服务实现类
 *
 * @author yupi
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private Gson gson;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            //生成accessKey和secretKey
            String[] keys = UUID.randomUUID().toString().split("-");
            user.setAccessKey(keys[0]);
            user.setSecretKey(keys[4]);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request, HttpServletResponse response) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        LoginUserVO loginUserVO = setLoginUser(response, user);

        //request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        return loginUserVO;
    }

    /**
     * 记录用户的登录态，并返回脱敏后的登录用户
     * @param response
     * @param user
     * @return
     */
    private LoginUserVO setLoginUser(HttpServletResponse response, User user){
        // 1.生成token
        String jwtToken = JwtUtils.getJwtToken(user.getId(), user.getUserName());
        // 2.生成Cookie，放入response
        Cookie cookie = new Cookie("token",jwtToken);
        cookie.setPath("/");
        response.addCookie(cookie);
        // 3.将token放入redis中
        String userJSON = gson.toJson(user);
        // 键 user:login:id 值 过期时间 单位
        stringRedisTemplate.opsForValue().set(UserConstant.USER_LOGIN_STATE + user.getId(), userJSON, JwtUtils.EXPIRE, TimeUnit.MILLISECONDS);
        // 4.返回loginUserVO脱敏后的用户数据
        return getLoginUserVO(user);

    }


    /**
     * 获取脱敏后的用户信息
     * @param user
     * @return
     */
    private LoginUserVO getLoginUserVO(User user){
        if (user == null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Long userIdByToken = JwtUtils.getUserIdByToken(request);
        if (userIdByToken == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }


        // 从缓存查询
        String s = stringRedisTemplate.opsForValue().get(UserConstant.USER_LOGIN_STATE + userIdByToken);
        User user = gson.fromJson(s, User.class);
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return user;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        User user = getLoginUser(request);
        return user != null && UserConstant.ADMIN_ROLE.equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies){
            if (cookie.getName().equals("token")){
                //删除缓存数据
                Long userIdByToken = JwtUtils.getUserIdByToken(request);
                stringRedisTemplate.delete(UserConstant.USER_LOGIN_STATE+userIdByToken);
                //删除Cookie
                Cookie timeOutCookie = new Cookie(cookie.getName(),cookie.getValue());
                timeOutCookie.setMaxAge(0);
                response.addCookie(timeOutCookie);
                return true;
            }
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
    }

}




