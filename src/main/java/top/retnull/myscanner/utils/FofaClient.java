package top.retnull.myscanner.utils;

/**
 * @program: myScanner
 * @description: maven包中filds校验不全，所以重构
 * @author: luoyu
 * @create: 2022-01-10 15:30
 **/
import cn.hutool.core.codec.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.r4v3zn.fofa.core.DO.FofaData;
import com.r4v3zn.fofa.core.DO.User;
import com.r4v3zn.fofa.core.DO.UserLogin;
import com.r4v3zn.fofa.core.enmus.UserVipLevelEnum;
import com.r4v3zn.fofa.core.exception.FofaException;
import com.r4v3zn.fofa.core.util.HttpUtils;
import java.util.*;

import static cn.hutool.core.codec.Base64.encode;
import static com.r4v3zn.fofa.core.constants.FofaClientConsts.*;

/**
 * Title: FofaClient
 * Descrption: this is FOFA Pro client
 * Date:2019-06-07 14:21
 * Email:woo0nise@gmail.com
 * Company:www.j2ee.app
 * @author R4v3zn
 * @version 1.0.0
 */
public class FofaClient {

    private String email;

    private String key;

    /**
     * Constructor
     * @param email email
     * @param key key
     */
    public FofaClient(String email, String key){
        this.email = email;
        this.key = key;
    }

    /**
     * Jackson mapper
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * getUserLogin
     * @return
     */
    public UserLogin getUserLogin(){
        return new UserLogin(email, key);
    }

    /**
     * get user info
     * @return
     */
    public User getUser() throws Exception{
        String loginEmail = this.email;
        String loginKey = this.key;
        // get user
        String url = "https://fofa.info" + GET_USER_INFO_URI;
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("email", loginEmail);
        map.put("key", loginKey);
        String rsp = HttpUtils.doGet(url, map);
        JsonNode node = mapper.readTree(rsp);
        JsonNode errorNode = node.get("error");
        if(errorNode != null && errorNode.asBoolean()){
            throw new FofaException(node.get("errmsg").asText());
        }
        String email = node.get("email").asText();
        String userName = node.get("username").asText();
        Integer fCoin = node.get("fcoin").asInt();
        Boolean isVip = node.get("isvip").asBoolean();
        Integer vipLevel = node.get("vip_level").asInt();
        Boolean isVerified = node.get("is_verified").asBoolean();
        String avatar = node.get("avatar").asText();
        Integer message = node.get("message").asInt();
        String fofacliVersion = node.get("fofacli_ver").asText();
        Boolean fofaServer = node.get("fofacli_ver").asBoolean();
        UserVipLevelEnum vipLevelEnum = vipLevel == 1?UserVipLevelEnum.VIP:UserVipLevelEnum.SVIP;
        User user = new User(email,userName,fCoin,isVip,vipLevelEnum,isVerified,avatar,message,fofacliVersion, fofaServer);
        return user;
    }

    /**
     * get data
     * <p>
     *     page default 1
     *     size default 100
     *     fields default host
     *     full default false
     * </p>
     * @param q search query(not encode)
     * @return fofa search result data
     * @throws Exception search Expection
     */
    public FofaData getData(String q) throws Exception {
        return getData(q, 1,100,"host",false);
    }

    /**
     * get data
     * <p>
     *     size default 100
     *     fields default host
     *     full default false
     * </p>
     * @param q search query(not encode)
     * @param page page
     * @return fofa search result data
     * @exception Exception search Expection
     */
    public FofaData getData(String q,Integer page) throws Exception {
        return getData(q, page,100,"host",false);
    }

    /**
     * get data
     * <p>
     *     fields default host
     *     full default false
     * </p>
     * @param q search query(not encode)
     * @param page page
     * @param size page size
     * @return fofa search result data
     * @exception Exception search Expection
     */
    public FofaData getData(String q,Integer page,Integer size) throws  Exception{
        return getData(q, page,size,"host",false);
    }

    /**
     * get data
     * <p>
     *     full default false
     * </p>
     * @param q search query(not encode)
     * @param page page
     * @param size page size
     * @param fields fields
     * @return  fofa search result data
     * @throws Exception search Expection
     */
    public FofaData getData(String q,Integer page,Integer size,String fields)throws Exception{
        return getData(q,page,size,fields,false);
    }

    /**
     * get data
     * @param q search query(not encode)
     * @param page page no
     * @param size page size
     * @param fields fields
     * @param full is full
     * @return fofa search result data
     */
    public FofaData getData(String q, Integer page, Integer size, String fields, Boolean full) throws Exception{
        checkParam(q,size,fields);
        // check page
        page = page < 0 ? 1:page;
        // check full is not null
        full = full == null ? false:full;
        String url = "https://fofa.info"+SEARCH_URI;
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("qbase64", encode(q));
        map.put("page", page);
        map.put("size", size);
        map.put("fields", fields);
        map.put("full",full);
        map.put("key", key);
        map.put("email", email);
        String rsp = HttpUtils.doGet(url, map);
        JsonNode node = mapper.readTree(rsp);
        JsonNode errorNode = node.get("error");
        if(errorNode != null && errorNode.asBoolean()){
            throw new FofaException(node.get("errmsg").asText());
        }
        String mode = node.get("mode").asText();
        String query = node.get("query").asText();
        Integer rspPage = node.get("page").asInt();
        Integer totalSize = node.get("size").asInt();
        String results = node.get("results").toString();
        Integer totalPage = totalSize%size == 0 ? totalSize/size:totalSize/size + 1;
        FofaData fofaData = new FofaData();
        fofaData.setMode(mode);
        fofaData.setPage(rspPage);
        fofaData.setSize(totalSize);
        fofaData.setQuery(query);
        fofaData.setTotalPage(totalPage);
        List<List<String>> list = mapper.readValue(results, List.class);
        fofaData.setResults(list);
        return fofaData;
    }

    /**
     * check param
     * @param q search query
     * @param size size
     * @param fields fields
     * @throws FofaException
     */
    public void checkParam(String q,Integer size, String fields)throws FofaException{
        // check search query
        final List<String> FIELDS_LIST = Arrays.asList(new String[]{"host" ,"title" ,"ip" ,"domain" ,"port" ,"country" ,"province" ,"city" ,"country_name" ,"header" ,"server" ,"protocol" ,"banner" ,"cert" ,"isp" ,"as_number" ,"as_organization" ,"latitude" ,"longitude" ,"structinfo" ,"icp" ,"fid" ,"cname" ,"type" ,"jarm"});
        if(q == null || "".equals(q)){
            throw  new FofaException("search query cannot be empty");
        }
        // check max size
        if(size > MAX_SIZE){
            throw new FofaException("max size "+MAX_SIZE);
        }
        // check fields
        List<String> splitList = Arrays.asList(fields.split(","));
        splitList = new ArrayList<String>(splitList);
        splitList.removeAll(FIELDS_LIST);
        if(splitList.size() > 0){
            throw new FofaException(splitList+" not's fields,please delte that");
        }
    }
}