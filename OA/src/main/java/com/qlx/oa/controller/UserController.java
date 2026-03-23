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
import com.qlx.oa.po.User;
import com.qlx.oa.service.IUserService;
import com.qlx.oa.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author qlx
 * @since 2026-03-10
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @GetMapping("/list")
    public Result<List<UserVO>> findAll(){
        List<User> userList = userService.list();
        //批量PO转VO
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
         User u=new User();
         u.setId(id);
         u.setValidStatus(0);
         boolean flag=userService.updateById(u);
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
        if (userUpdateDTO.getPassword() != null && userUpdateDTO.getPassword().trim().isEmpty()) {
            userUpdateDTO.setPassword(null);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateDTO,user);
        boolean flag =userService.updateById(user);
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
        //确定返回对象的页数，以及页大小
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
    public Result<User> login(@RequestBody @Validated UserLoginDTO userLoginDTO) {

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getNo, userLoginDTO.getNo());
        User dbUser = userService.getOne(wrapper);//getOne只能有一个账号
        if (dbUser == null) {
            throw new BusinessException("账号或密码错误");
        }
        if (dbUser.getValidStatus() == 0) {
            throw new BusinessException("该账号已注销或被禁用，请联系管理员");
        }
        if (!dbUser.getPassword().equals(userLoginDTO.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        dbUser.setPassword(null);
        return Result.success(dbUser);

    }
}
