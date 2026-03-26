package com.qlx.oa.vo;

import com.qlx.oa.po.Menu;
import com.qlx.oa.po.User;
import lombok.Data;

import java.util.List;

@Data
public class UserLoginVO {
    private String token;
    private User user;
    private List<Menu> menus;
}
