package com.qlx.oa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qlx.oa.common.BusinessException;
import com.qlx.oa.common.Result;
import com.qlx.oa.dto.UserAddDTO;
import com.qlx.oa.dto.UserLoginDTO;
import com.qlx.oa.dto.UserPageDTO;
import com.qlx.oa.dto.UserUpdateDTO;
import com.qlx.oa.po.Menu;
import com.qlx.oa.po.User;
import com.qlx.oa.service.IMenuService;
import com.qlx.oa.service.IUserService;
import com.qlx.oa.utils.JwtUtils;
import com.qlx.oa.vo.UserLoginVO;
import com.qlx.oa.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author qlx
 * @since 2026-03-10
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IUserService userService;
    @Autowired
    private IMenuService menuService;

    private static final String SALT = "qlx_oa_system_#@!_2026";

    @GetMapping("/list")
    public Result<List<UserVO>> findAll(){
        List<User> userList = userService.list();
        List<UserVO> voList = userList.stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(voList);
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody @Validated UserAddDTO userAddDTO){
        String no = userAddDTO.getNo();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getNo, no);
        long count = userService.count(wrapper);

        if(count > 0){
            throw new BusinessException(500, "已有用户，账号已被注册");
        }
        User user = new User();
        BeanUtils.copyProperties(userAddDTO,user);

        String rawPassword = userAddDTO.getPassword();
        String encodedPassword = org.springframework.util.DigestUtils.md5DigestAsHex((rawPassword + SALT).getBytes());
        user.setPassword(encodedPassword);

        boolean flag = userService.save(user);
        if(!flag){
            throw new BusinessException("新增用户失败");
        }
        return Result.success() ;
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Integer id){
        if(id == null){
            throw new BusinessException(400,"ID不能为空");
        }
        User u = new User();
        u.setId(id);
        u.setValidStatus(0);
        boolean flag = userService.updateById(u);
        if(!flag){
            throw new BusinessException("删除失败");
        }
        return Result.success() ;
    }

    @PostMapping ("/update")
    public Result<Boolean> modify(@RequestBody @Validated UserUpdateDTO userUpdateDTO){
        if (userUpdateDTO.getId() == null) {
            throw new BusinessException(400, "更新失败，缺少用户ID");
        }


        String rawPassword = userUpdateDTO.getPassword();
        if (rawPassword != null && !rawPassword.trim().isEmpty()) {
            String encodedPassword = org.springframework.util.DigestUtils.md5DigestAsHex((rawPassword + SALT).getBytes());
            userUpdateDTO.setPassword(encodedPassword);
        } else {
            userUpdateDTO.setPassword(null);
        }

        User user = new User();
        BeanUtils.copyProperties(userUpdateDTO,user);
        boolean flag = userService.updateById(user);
        if(!flag){
            throw new BusinessException("更新失败");
        }
        return Result.success() ;
    }

    @GetMapping("/search")
    public Result<List<UserVO>> search(@RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(User::getName, keyword)
                    .or()
                    .like(User::getNo, keyword);
        }

        List<User> list = userService.list(wrapper);
        List<UserVO> voList = list.stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user,vo);
            return vo;
        }).collect(Collectors.toList());

        return Result.success(voList);
    }

    @PostMapping("/list/page")
    public Result<Page<UserVO>> pageList(@RequestBody @Validated UserPageDTO userPageDTO) {
        Page<User> page = new Page<>(userPageDTO.getPageNum(), userPageDTO.getPageSize());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getValidStatus, 1);

        if (StringUtils.isNotBlank(userPageDTO.getKeyword())) {
            wrapper.and(w -> w.like(User::getName, userPageDTO.getKeyword()).or().like(User::getNo, userPageDTO.getKeyword()));
        }

        // 如果传了角色 ID 且不为 0，就加上精确等值匹配 (eq)
        if (userPageDTO.getRoleId() != null && userPageDTO.getRoleId() != 0) {
            wrapper.eq(User::getRoleId, userPageDTO.getRoleId());
        }

        // 如果传了性别，也加上精确匹配
        if (userPageDTO.getSex() != null) {
            wrapper.eq(User::getSex, userPageDTO.getSex());
        }

        // 执行查询
        Page<User> resultPage = userService.page(page, wrapper);
        Page<UserVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<UserVO> voList = resultPage.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return Result.success(voPage);
    }

    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody @Validated UserLoginDTO userLoginDTO) {

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getNo, userLoginDTO.getNo());
        User dbUser = userService.getOne(wrapper);//getOne只能有一个账号
        if (dbUser == null) {
            throw new BusinessException("账号或密码错误");
        }

        String inputPassword = userLoginDTO.getPassword();
        String encodedInputPassword = org.springframework.util.DigestUtils.md5DigestAsHex((inputPassword + SALT).getBytes());

        if (!dbUser.getPassword().equals(encodedInputPassword)) {
            throw new BusinessException("账号或密码错误");
        }

        if (dbUser.getValidStatus() == 0) {
            throw new BusinessException("该账号已注销或被禁用，请联系管理员");
        }


        dbUser.setPassword(null);

        // 生成 JWT Token
        String token = JwtUtils.generateToken(dbUser.getId(), dbUser.getNo());

        // 获取用户的菜单权限
        List<Menu> menuList = menuService.getMenusByRoleId(dbUser.getRoleId());

        // 将 Token 作为 Key，只将用户信息作为 Value 存入 Redis
        String redisKey = "login:token:" + token;
        Map<String, Object> cacheData = new HashMap<>();
        cacheData.put("user", dbUser);

        stringRedisTemplate.opsForValue().set(
                redisKey,
                com.alibaba.fastjson2.JSON.toJSONString(cacheData),
                2,
                java.util.concurrent.TimeUnit.HOURS
        );

        UserLoginVO loginVO = new UserLoginVO();
        loginVO.setToken(token);
        loginVO.setUser(dbUser);
        loginVO.setMenus(menuList);

        return Result.success(loginVO);
    }

    @PostMapping("/logout")
    // 用 @RequestHeader 注解，告诉 Spring Boot：去把 Header 里Authorization 的值拿过来，赋值给 headerToken 变量
    public Result<Boolean> logout(@RequestHeader(value = "Authorization", required = false) String headerToken) {

        if (StringUtils.isNotBlank(headerToken) && headerToken.startsWith("Bearer ")) {

            String token = headerToken.substring(7);


            String redisKey = "login:token:" + token;


            stringRedisTemplate.delete(redisKey);
        }

        return Result.success();
    }
}