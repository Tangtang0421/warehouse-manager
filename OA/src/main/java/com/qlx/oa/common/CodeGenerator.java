package com.qlx.oa.common;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;
import java.util.Scanner;

public class CodeGenerator {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("输入需要生成的数据库表");
        System.out.println("如果有多个表，用英文逗号隔开。输入 all 表示生成所有表");

        String inputTables = scanner.nextLine();

        if (inputTables == null || inputTables.trim().isEmpty()) {
            System.out.println("表名不能为空");
            return;
        }

        // 自动获取当前项目的根目录路径
        String projectPath = System.getProperty("user.dir");

        // 数据库连接配置
        String url = "jdbc:mysql://localhost:3306/qlx?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";

        FastAutoGenerator.create(url, "root", "12345")

                // 全局配置
                .globalConfig(builder -> {
                    builder.author("qlx") // 设置作者名字
                            .outputDir(projectPath + "/OA/src/main/java"); // 指定生成的 Java 文件直接存放到 java 目录下
                })

                // 包配置
                .packageConfig(builder -> {
                    builder.parent("com.qlx.oa") // 设置父包名，所有生成的 entity/mapper/service 都会放在这个下面
                            .moduleName("")
                            // XML 文件专门扔到 resources/mapper 文件夹里去
                            .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + "/OA/src/main/resources/mapper"));
                })

                // 策略配置
                .strategyConfig(builder -> {

                    if ("all".equalsIgnoreCase(inputTables.trim())) {
                        // 如果输入 all，则包含所有表
                        builder.addInclude(".*");
                    } else {

                        builder.addInclude(inputTables.split(","));
                    }

                    builder.entityBuilder().enableLombok(); // 自动加上 @Data 注解
                })


                .templateEngine(new FreemarkerTemplateEngine())
                .execute();

    }
}