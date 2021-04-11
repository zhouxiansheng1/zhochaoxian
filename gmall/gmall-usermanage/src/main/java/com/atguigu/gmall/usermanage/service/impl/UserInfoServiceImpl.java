package com.atguigu.gmall.usermanage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.RedisUtil;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserInfoService;
import com.atguigu.gmall.usermanage.mapper.UserAddressMapper;
import com.atguigu.gmall.usermanage.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;

import java.util.List;


@Service
public class UserInfoServiceImpl implements UserInfoService{

    public String USERKEY_PREFIX="user:";
    public String USERINFOKEY_SUFFIX=":info";
    public int USERKEY_TIMEOUT=60*60*24;
    // 调用Maper。【UserInfoMapper】
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private RedisUtil redisUtil;
    @Override
    public List<UserInfo> getUserInfoListAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public void addUser(UserInfo userInfo) {

    }

    @Override
    public void updateUser(String id, UserInfo userInfo) {

    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        // select * from useraddress where userId = ? userId
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }

    @Override
    public UserInfo login(UserInfo userInfo) {
        String password = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        userInfo.setPasswd(password);
        UserInfo info = userInfoMapper.selectOne(userInfo);
        if (info!=null){
            // 获得到redis ,将用户存储到redis中
            Jedis jedis = redisUtil.getJedis();
            jedis.setex(USERKEY_PREFIX+info.getId()+USERINFOKEY_SUFFIX,USERKEY_TIMEOUT, JSON.toJSONString(info));
            jedis.close();
            return info;
        }
        return null;
    }

    //验证
    @Override
    public UserInfo verify(String userId) {
        // 去缓存中查询是否有redis
        Jedis jedis = redisUtil.getJedis();
        String key =USERKEY_PREFIX+userId+USERINFOKEY_SUFFIX;
        String userJson = jedis.get(key);
        //延长时效
        jedis.expire(key,USERKEY_TIMEOUT);
        if (userJson!=null){
            UserInfo userInfo = JSON.parseObject(userJson, UserInfo.class);
            return userInfo;
        }
        return null;
    }
}

















