package top.retnull.myscanner.service;

import java.awt.image.BufferedImage;

/**
 * <p>
 * 验证码
 * </p>
 *
 * @author retnull
 * @version 1.0
 * @createTime 2020/3/5 0005 11:28
 */

public interface VerifyCodeService {

    /**
     * 随机获取图形验证码
     *
     * @param codeKey
     * @return ImageVerifyCode
     * @author retnull
     * @createTime 2020/3/5 0005 11:31
     */
    BufferedImage randomImageVerifyCode(String codeKey);

    /**
     * 删除在 redis 中缓存的。图形验证码值
     *
     * @param codeKey
     * @return ImageVerifyCode
     * @author retnull
     * @createTime 2020/3/5 0005 11:31
     */
    void deleteImageVerifyCode(String codeKey);

    /**
     * 删除在 redis 中缓存的。图形验证码值
     *
     * @param codeKey
     * @param userCodeText
     * @return ImageVerifyCode
     * @author retnull
     * @createTime 2020/3/5 0005 11:31
     */
    boolean checkVerifyCode(String codeKey, String userCodeText);

}
