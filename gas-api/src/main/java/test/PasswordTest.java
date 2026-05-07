package test;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;

public class PasswordTest {
    public static void main(String[] args) {
        // 测试用户名和密码
        String username = "test888";
        String password = "abc123456";
        
        // 按照MisUserServiceImpl中的加密逻辑计算密码
        MD5 md5 = MD5.create();
        String temp = md5.digestHex(username);
        // first 6 characters
        String tempStart = StrUtil.subWithLength(temp, 0, 6);
        // last 3 characters
        String tempEnd = StrUtil.subSuf(temp, temp.length() - 3);
        // mix original password and hash encrypt
        String encryptedPassword = md5.digestHex(tempStart + password + tempEnd);
        
        System.out.println("Username: " + username);
        System.out.println("Original password: " + password);
        System.out.println("MD5 of username: " + temp);
        System.out.println("tempStart (first 6 chars): " + tempStart);
        System.out.println("tempEnd (last 3 chars): " + tempEnd);
        System.out.println("Encrypted password: " + encryptedPassword);
        
        // 验证其他用户的加密过程
        System.out.println("\n--- Testing other users ---");
        testUser("admin", "abc123456");
        testUser("test123", "abc123456");
        testUser("test888", "abc123456");
        testUser("try6677", "abc123456");
        testUser("test222222", "abc123456");
        testUser("zzzpc", "abc123456");
    }
    
    private static void testUser(String username, String password) {
        MD5 md5 = MD5.create();
        String temp = md5.digestHex(username);
        String tempStart = StrUtil.subWithLength(temp, 0, 6);
        String tempEnd = StrUtil.subSuf(temp, temp.length() - 3);
        String encryptedPassword = md5.digestHex(tempStart + password + tempEnd);
        
        System.out.println("\nUsername: " + username);
        System.out.println("Original password: " + password);
        System.out.println("Encrypted password: " + encryptedPassword);
    }
}