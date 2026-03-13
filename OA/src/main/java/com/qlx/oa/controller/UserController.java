package com.qlx.oa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qlx.oa.common.QueryPageParam;
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
        String no = user.getNo();
        if(StringUtils.isBlank(no)){
            return Result.error(500, "账号不能为空");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getNo, no);
        long count = userService.count(wrapper);

        if(count > 0){
            return Result.error(500, "已有用户，账号已被注册");
        } else {
            boolean flag = userService.save(user);
            return flag ? Result.success() : Result.error(500, "新增用户失败");
        }
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
    /**
     * 使用 Wrapper 的单表分页
     * 因为接收的是复杂对象 QueryPageParam，所以使用 @PostMapping 和 @RequestBody 接收 JSON 格式的请求
     */
    @PostMapping("/list/page")
    public Result<Page<User>> pageList(@RequestBody QueryPageParam queryParam) {
        //确定返回对象的页数，以及页大小
        Page<User> page = new Page<>(queryParam.getPageNum(), queryParam.getPageSize());

        // 提取所有的参数
        String keyword = (String) queryParam.getParam().get("keyword");
        String roleId = (String) queryParam.getParam().get("roleId");
        String sex = (String) queryParam.getParam().get("sex");

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 如果有关键字，就进行模糊匹配
        if (StringUtils.isNotBlank(keyword)) {
            // 当多个条件组合时，带有 OR 的模糊查询必须用 .and(w -> ...) 括起来！
            // 否则 SQL 会变成：WHERE name LIKE ? OR no LIKE ? AND roleId = ?
            // 加上括号后 SQL 是：WHERE (name LIKE ? OR no LIKE ?) AND roleId = ?
            wrapper.and(w -> w.like(User::getName, keyword).or().like(User::getNo, keyword));
        }

        // 如果传了角色 ID，就加上精确等值匹配 (eq)
        if (StringUtils.isNotBlank(roleId)) {
            wrapper.eq(User::getRoleId, roleId);
        }

        // 如果传了性别，也加上精确匹配
        if (StringUtils.isNotBlank(sex)) {
            wrapper.eq(User::getSex, sex);
        }

        // 执行查询
        Page<User> resultPage = userService.page(page, wrapper);//wrapper类似SQL语法生成器

        return Result.success(resultPage);
    }
}
