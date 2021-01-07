package com.mind.links.common.utils;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * description : TODO 地址码处理类
 *
 * @author : qiDing
 * date: 2020-11-19 11:22
 * @version v1.0.0
 */
@Component
@Data
@Slf4j
public class LinksFileManage {

    /**
     * @date ：Created in 2020/12/29 11:23
     * description： 检查路径是否完整，自动补全/  修复
     */
    public static String checkPathAndRepair(String path) {
        // 以 / 结尾
        boolean one = "/".equals(path.substring(path.length() - 1));
        // 以 \\ 结尾
        boolean two = "\\".equals(path.substring(path.length() - 1));
        // 长度为1
        boolean three = path.length() == 1;
        if (one && three) {
            return "";
        }
        if (!one || !two) {
            return path + "/";
        }
        return path;
    }

    /**
     * Description  删除指定目录下所有目录及文件
     * @date 9:16 2020/7/14 0014
     **/
    public static void deleteDirAndFileAll(String path) {
        File file = new File(path);
        AtomicBoolean end = new AtomicBoolean(false);
        // 判断待删除目录是否存在
        Optional.of(file).filter(File::exists).ifPresent(files -> {
            try {
                Arrays.stream(Objects.requireNonNull(files.list()))
                        .forEach(name -> {
                            File temp = new File(path, name);
                            // 判断是否是目录
                            if (temp.isDirectory()) {
                                // 递归调用，删除目录里的内容
                                deleteDirAndFileAll(temp.getAbsolutePath());
                                boolean delete = temp.delete();
                            } else {
                                // 删除文件
                                boolean delete = temp.delete();
                            }
                        });
            } catch (Exception e) {
                log.error("***文件删除出现异常***");
            }
            end.set(new File(path).delete());
            log.info("文件删除成功：" + path);
        });
        end.get();
    }

    /**
     * @date ：Created in 2019/10/07 上午10:46
     * description： 删除单个文件
     */
    public static boolean delFileOne(String path) {
        File file = new File(path);
        AtomicBoolean end = new AtomicBoolean(false);
        try {
            Optional.of(file)
                    .filter(files -> files.exists() && files.isFile())
                    .ifPresent(files -> {
                        end.set(files.delete());
                        if (end.get()) {
                            log.info("===文件删除成功===" + path);
                        } else {
                            log.error("***文件删除失败**" + path);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("***文件删除失败**" + path);
            return false;
        }
        return end.get();
    }

    /**
     * Description  创建目录
     * @date 9:17 2020/4/14 0014
     **/
    public static void fileMkdirs(File file) {
        try {
            boolean b = file.setWritable(true, false);
            if (!file.getParentFile().exists()) {
                //上级目录不存在，创建上级目录
                boolean mkdirs = file.getParentFile().mkdirs();
                log.info("==============执行创建文件夹" + file.getPath() + "赋予读写权限" + b);
            }
            boolean mkdirs = file.mkdirs();
            log.info("==============执行创建文件夹" + file.getPath() + "赋予读写权限" + b);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("*********************文件夹创建失败！！********************************");
        }
    }

    /**
     * InputStream转化为byte[]数组
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    /**
     * 获取文件名
     * 会抛异常
     */
    public static Mono<String> getFileName(Mono<String> mono) {
        return mono.map(LinksFileManage::getFileName);
    }

    public static String getFileName(String p) {
        return p.substring(p.lastIndexOf("/") + 1);
    }

    /**
     * 获取文件后缀
     * 会抛异常
     */
    public static Mono<String> getFileSuffix(Mono<String> mono) { return mono.map(LinksFileManage::getFileSuffix); }

    public static String getFileSuffix(String p) {
        return p.substring(p.lastIndexOf(".") + 1);
    }


}
