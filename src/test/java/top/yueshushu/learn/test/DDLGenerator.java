package top.yueshushu.learn.test;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.junit.Test;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class DDLGenerator {

    /**
     * 扫描指定目录下的所有class
     *
     * @param basePackage: 实体类目录path
     * @return
     * @author huqs
     * @date 2024/3/29
     */
    public static List<Class<?>> scanEntityClasses(String basePackage) {
        List<Class<?>> classes = new ArrayList<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> urls = classLoader.getResources(basePackage.replace('.', '/'));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url.getProtocol().equals("file")) {
                    File directory = new File(url.getPath());
                    findClassesInDirectory(directory, basePackage, classes);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static void findClassesInDirectory(File directory, String packageName, List<Class<?>> classes) {
        if (!directory.exists()) {
            return;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                findClassesInDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    // 获取拥有@TableName 注解的类
                    if (hasTableNameAnnotation(clazz)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 校验class是否包含 @TableName 注解
     *
     * @param clazz:
     * @return
     * @author huqs
     * @date 2024/3/29
     */
    private static boolean hasTableNameAnnotation(Class<?> clazz) {
//        return clazz.getAnnotation(TableName.class) != null;
        return true;
    }

    /**
     * 生成DDL语句，生成的是postgresql，其他数据库可以调整对应语法
     *
     * @param poClass:
     * @return
     * @author huqs
     * @date 2024/3/29
     */
    public static String generateDDL(Class<?> poClass) {
        TableName tableNameAnnotation = poClass.getAnnotation(TableName.class);
        String tableName = poClass.getSimpleName();
        if (tableNameAnnotation != null) {
             tableName = tableNameAnnotation.value();
        }


        StringBuilder ddlBuilder = new StringBuilder("CREATE TABLE \"");
        ddlBuilder.append(tableName).append("\" (\n");

        Field[] fields = poClass.getDeclaredFields();

        //处理po字段转换
        for (Field field : fields) {
            String fieldName = toSnakeCase(field.getName());
            String fieldTypeStr = getFieldType(field);
            //处理每行字段
            if (field.isAnnotationPresent(TableId.class)) {
                ddlBuilder.append("    \"").append(fieldName).append("\" ").append(" serial NOT NULL PRIMARY KEY");
            } else {
                ddlBuilder.append("    \"").append(fieldName).append("\" ").append(fieldTypeStr);
            }
            ddlBuilder.append(",\n");
        }
        //删除最后一个标点和换行  ,
        String substring = ddlBuilder.substring(0, ddlBuilder.length() - 2);
        ddlBuilder = new StringBuilder(substring);

        ddlBuilder.append("\n);\n");
        ddlBuilder.append("   -- Column comments\n");
        for (Field field : fields) {
            String fieldName = toSnakeCase(field.getName());
            // 添加字段注释
            String comment = getComment(field);
            if (comment != null && !comment.isEmpty()) {
                ddlBuilder.append(" COMMENT ON COLUMN \"").append(tableName).append("\".\"")
                        .append(fieldName).append("\" IS '").append(comment).append("';\n");
            }
        }

        // 添加表注释
        ddlBuilder.append(" -- Table comment\n COMMENT ON TABLE \"").append(tableName).append("\" IS '")
                .append(getTableComment(poClass)).append("';\n\n");

        return ddlBuilder.toString();
    }

    /**
     * 字段类型转换成ddl 类型
     *
     * @param field:
     * @return
     * @author huqs
     * @date 2024/3/29
     */
    private static String getFieldType(Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType == Integer.class) {
            return "int";
        } else if (fieldType == String.class) {
            return "varchar(255)";
        } else if (fieldType == java.util.Date.class) {
            if ("createTime".equals(field.getName())) {
                return "timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP";
            } else {
                return "timestamp(6)";
            }
        } else if (fieldType == java.math.BigDecimal.class) {
            return "DECIMAL(12,2)";
        } else {
            // Add support for more field types as needed
            return " ";
        }
    }

    /**
     * 字段驼峰命名
     *
     * @param input:
     * @return
     * @author huqs
     * @date 2024/3/29
     */
    private static String toSnakeCase(String input) {
        StringBuilder result = new StringBuilder();
        if (input != null && input.length() > 0) {
            result.append(input.substring(0, 1).toLowerCase());
            for (int i = 1; i < input.length(); i++) {
                char currentChar = input.charAt(i);
                if (Character.isUpperCase(currentChar)) {
                    result.append("_").append(Character.toLowerCase(currentChar));
                } else {
                    result.append(currentChar);
                }
            }
        }
        return result.toString();
    }

    /**
     * 获取字段注释
     *
     * @param field:
     * @return
     * @author huqs
     * @date 2024/3/29
     */
    private static String getComment(Field field) {
        ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
        return annotation != null ? annotation.value() : "";
    }

    /**
     * 获取表注释
     *
     * @param poClass:
     * @return
     * @author huqs
     * @date 2024/3/29
     */
    private static String getTableComment(Class<?> poClass) {
        ApiModel annotation = poClass.getAnnotation(ApiModel.class);
        return annotation != null ? annotation.description() : "";
    }
    @Test
    public  void main1() {
        List<Class<?>> entityClasses = scanEntityClasses("top.yueshushu.learn.mode.vo");//换成自己po所在包的目录
        for (Class<?> clazz : entityClasses) {
            String ddl = generateDDL(clazz);
            System.out.println(ddl);
        }
    }
}

