package com.qlx.oa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.qlx.oa.common.Result;
import com.qlx.oa.entity.User;
import com.qlx.oa.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result<List<User>> findAll(){
        List<User> userList =userService.list();
        return Result.success(userList);
    }
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody User user){
        boolean flag =userService.save(user);
        return flag ? Result.success():Result.error(500,"新增用户失败");
    }
    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Integer id){
         boolean flag =userService.removeById(id);
         return flag ? Result.success() : Result.error(500,"删除用户失败，可能不存在该用户");
    }
    @PutMapping("/mod")
    public Result<Boolean> modify(@RequestBody User user){
        boolean flag =userService.updateById(user);
        return flag ? Result.success() : Result.error(500,"更新失败");
    }
    @GetMapping("/search")
    public Result<List<User>> search(@RequestParam(required = false) String keyword) {

        //Lambda 包装器
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 只有当关键字不是空的时候，才拼接查询条件
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(User::getName, keyword)
                    .or()
                    .like(User::getNo, keyword);
        }

        // 把包装器交给 Service 执行查询
        List<User> list = userService.list(wrapper);

        return Result.success(list);
    }
}
