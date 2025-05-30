package kpfu.itis.kasimov.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryUtil {

    private static Cloudinary cloudinary;

    public static Cloudinary getInstance() {
        if (cloudinary == null) {
            Map<String, String> configMap = new HashMap<>();
            configMap.put("cloud_name", "dgtm90ts8");
            configMap.put("api_key", "118997312764755");
            configMap.put("api_secret", "cwBU1E6svsFDToYTDvmcAQr99kA");
            cloudinary = new Cloudinary(configMap);
        }
        return cloudinary;
    }

    public static String upload(MultipartFile file) {
        try {
            Map uploadResult = getInstance().uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки файла в Cloudinary", e);
        }
    }
}
