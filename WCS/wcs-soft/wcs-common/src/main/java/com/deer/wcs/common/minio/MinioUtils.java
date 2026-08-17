package com.deer.wcs.common.minio;

import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.DateUtils;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MinioUtils {

    @Autowired
    private MinioClient client;
    @Autowired
    private MinioProp minioProp;

    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     */
    public void createBucket(String bucketName) {
        try {
            if (!client.bucketExists(bucketName)) {
                client.makeBucket(bucketName);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * 上传文件
     *
     * @param file       文件
     * @param bucketName 存储桶
     * @return
     */
    public JSONObject uploadFile(MultipartFile file, String bucketName, String fileName) throws Exception {
        JSONObject res = new JSONObject();
        res.put("code", 0);
        // 判断上传文件是否为空
        if (null == file || 0 == file.getSize()) {
            res.put("msg", "上传文件不能为空");
            return res;
        }
        try {
            // 判断存储桶是否存在
            createBucket(bucketName);
            // 文件名
            String originalFilename = file.getOriginalFilename();
            // 新的文件名 = 存储桶名称_时间戳.后缀名
            if(fileName==null || fileName.isEmpty()){
//                fileName = bucketName + "_" + DateUtil.getNowDateTimeString() + originalFilename.substring(originalFilename.lastIndexOf("."));
                fileName = bucketName + "_" + DateUtil.getNowDateTimeString() + originalFilename;
            }
            // 开始上传
            client.putObject(bucketName, fileName, file.getInputStream(), file.getContentType());
            res.put("code", 1);
            res.put("msg", minioProp.getEndpoint() + "/" + bucketName + "/" + fileName);
            return res;
        }  catch (Exception e) {
        }
        res.put("msg", "上传失败");
        return res;
    }

    //文件删除
    public String delete(String name) {
        try {
            MinioClient minioClient = new MinioClient(minioProp.getEndpoint(), minioProp.getAccesskey(), minioProp.getSecretKey());
            minioClient.removeObject("zhgy", name);
        } catch (Exception e) {
            return "删除失败"+e.getMessage();
        }
        return "删除成功";
    }
}

