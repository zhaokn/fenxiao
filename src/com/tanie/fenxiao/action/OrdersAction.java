package com.tanie.fenxiao.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tanie.db.utils.JdbcHelper;
import com.tanie.fenxiao.entities.Commission;
import com.tanie.fenxiao.entities.Config;
import com.tanie.fenxiao.entities.Financial;
import com.tanie.fenxiao.entities.Kami;
import com.tanie.fenxiao.entities.Orders;
import com.tanie.fenxiao.entities.Product;
import com.tanie.fenxiao.entities.User;
import com.tanie.fenxiao.service.ICommissionService;
import com.tanie.fenxiao.service.IConfigService;
import com.tanie.fenxiao.service.IFinancialService;
import com.tanie.fenxiao.service.IKamiService;
import com.tanie.fenxiao.service.IOrdersService;
import com.tanie.fenxiao.service.IProductService;
import com.tanie.fenxiao.service.IUserService;
import com.tanie.fenxiao.utils.BjuiJson;
import com.tanie.fenxiao.utils.BjuiPage;
import com.tanie.fenxiao.utils.FreemarkerUtils;
import com.tanie.fenxiao.utils.PageModel;
import com.unstoppedable.common.CommonUtil;
import com.unstoppedable.common.Configure;
import com.unstoppedable.common.Sha1Util;
import com.unstoppedable.common.Signature;
import com.unstoppedable.notify.PayNotifyData;
import com.unstoppedable.notify.PayNotifyTemplate;
import com.unstoppedable.notify.PaySuccessCallBack;
import com.unstoppedable.protocol.UnifiedOrderReqData;
import com.unstoppedable.service.WxPayApi;
import freemarker.template.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

@Controller("ordersAction")
@Scope("prototype")
public class OrdersAction extends BaseAction {
    private static final long serialVersionUID = 1L;

    @Resource(name = "ordersService")
    private IOrdersService<Orders> ordersService;

    @Resource(name = "userService")
    private IUserService<User> userService;

    @Resource(name = "productService")
    private IProductService<Product> productService;

    @Resource(name = "kamiService")
    private IKamiService<Kami> kamiService;

    @Resource(name = "financialService")
    private IFinancialService<Financial> financialService;

    @Resource(name = "commissionService")
    private ICommissionService<Commission> commissionService;
    private Orders orders;
    private String ftlFileName;

    @Resource(name = "configService")
    private IConfigService<Config> configService;

    public void list() {
        String key = this.request.getParameter("key");
        String countHql = "select count(*) from Orders where deleted=0";
        String hql = "from Orders where deleted=0";
        if (StringUtils.isNotEmpty(key)) {
            countHql = countHql + " and (user.name='" + key + "' or no='" + key
                    + "' or productName='" + key + "')";
            hql = hql + " and (user.name='" + key + "' or no='" + key
                    + "' or productName='" + key + "')";
        }
        hql = hql + " order by id desc";

        int count = 0;
        count = this.ordersService.getTotalCount(countHql, new Object[0]);
        this.page = new BjuiPage(this.pageCurrent, this.pageSize);
        this.page.setTotalCount(count);
        this.cfg = new Configuration();

        this.cfg.setServletContextForTemplateLoading(this.request.getSession()
                .getServletContext(), "WEB-INF/templates/admin");
        List ordersList = this.ordersService.list(hql, this.page.getStart(),
                this.page.getPageSize(), new Object[0]);
        Map root = new HashMap();
        root.put("ordersList", ordersList);
        root.put("page", this.page);
        root.put("key", key);
        FreemarkerUtils.freemarker(this.request, this.response,
                this.ftlFileName, this.cfg, root);
    }

    public void add() {
        String pidStr = this.request.getParameter("pid");
        int pid = 0;
        try {
            pid = Integer.parseInt(pidStr);
        } catch (Exception e) {
            this.request.setAttribute("status", "0");
            this.request.setAttribute("message", "参数错误");
            try {
                this.request.getRequestDispatcher("cart.jsp").forward(
                        this.request, this.response);
            } catch (ServletException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
        Product findProduct = (Product) this.productService.findById(
                Product.class, pid);
        if (findProduct == null) {
            this.request.setAttribute("status", "0");
            this.request.setAttribute("message", "商品不存在");
        } else {
            this.request.setAttribute("status", "1");
            this.request.setAttribute("product", findProduct);
        }
        try {
            this.request.getRequestDispatcher("cart.jsp").forward(this.request,
                    this.response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        String pidStr = this.request.getParameter("pid");
        String numStr = this.request.getParameter("num");
        int pid = 0;
        int num = 1;
        try {
            pid = Integer.parseInt(pidStr);
            num = Integer.parseInt(numStr);
        } catch (Exception e) {
            this.request.setAttribute("status", "0");
            this.request.setAttribute("message", "参数错误");
            try {
                this.request.getRequestDispatcher("orderAdd.jsp").forward(
                        this.request, this.response);
            } catch (ServletException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
        Product findProduct = (Product) this.productService.findById(
                Product.class, pid);
        if (findProduct == null) {
            this.request.setAttribute("status", "0");
            this.request.setAttribute("message", "商品不存在");
        } else {
            HttpSession session = this.request.getSession();
            User loginUser = (User) session.getAttribute("loginUser");
            if ((loginUser == null) || (loginUser.getId() == null)) {
                this.request.setAttribute("status", "0");
                this.request.setAttribute("message", "您未登陆或者登陆失效，请重新登陆");
            } else {
                Orders newOrders = new Orders();
                newOrders.setProductId(String.valueOf(findProduct.getId()));
                newOrders.setProductName(findProduct.getTitle());
                newOrders.setProductNum(Integer.valueOf(num));
                newOrders.setProductMoney(findProduct.getMoney());
                newOrders.setUser(loginUser);
                newOrders.setStatus(Integer.valueOf(0));
                newOrders.setMoney(Double.valueOf(num
                        * findProduct.getMoney().doubleValue()));

                Random random = new Random();
                int n = random.nextInt(9999);
                n += 10000;

                String no = String.valueOf(System.currentTimeMillis() + n);
                newOrders.setNo(no);

                newOrders.setCreateDate(new Date());
                newOrders.setDeleted(false);
                this.ordersService.saveOrUpdate(newOrders);
                try {
                    //支付宝免签支付入口
//					this.response.sendRedirect("settle?no=" + no);
                    this.response.sendRedirect(request.getContextPath() + "/wxAauthorize?no=" + no);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 第三步【权限验证】
     * 微信支付权限验证
     */
    public void wxAauthorize() {
        System.out.println("########################   第三步【权限验证】   ########################");
        String no = this.request.getParameter("no");
        Configure config = new Configure();
        //WxPayBean wpb = new WxPayBean();
        //账号及商户相关参数
        String appId = config.getAppid();//wpb.getAppid();
        String redirectUri = config.getRedirectUri();
        //授权后要跳转的链接所需的参数一般有会员号，金额，订单号之类，
        //最好自己带上一个加密字符串将金额加上一个自定义的key用MD5签名或者自己写的签名,
        //比如 Sign = %3D%2F%CS%
        redirectUri = redirectUri + "?no=" + no;
        //URLEncoder.encode 后可以在backUri 的url里面获取传递的所有参数
        redirectUri = URLEncoder.encode(redirectUri);
        //scope 参数视各自需求而定，这里用scope=snsapi_base 不弹出授权页面直接授权目的只获取统一支付接口的openid
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=" + appId + "&redirect_uri=" + redirectUri +
                "&response_type=code&scope=snsapi_base&state=123#wechat_redirect";

        try {
            this.response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 第四步【获取用户OPENID】
     * 权限验证通过，页面跳转获取OPENID
     */
    public void wxRedirect() {
        System.out.println("########################   第四步【获取用户OPENID】   ########################");
        String no = request.getParameter("no");
        Configure config = new Configure();
        String appId = config.getAppid();
        String appSecret = config.getAppSecret();

        String code = request.getParameter("code");
        String openId = null;

        // 获取OPENID后调用统一支付接口https://api.mch.weixin.qq.com/pay/unifiedorder
        String URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + appId + "&secret=" + appSecret + "&code=" + code
                + "&grant_type=authorization_code";

        net.sf.json.JSONObject jsonObject = CommonUtil.httpsRequest(URL, "GET", null);
        if (null != jsonObject) {
            openId = jsonObject.getString("openid");
        }
        try {
            response.sendRedirect(request.getContextPath() + "/user/settle?no=" + no + "&openId=" + openId);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 第五步【获取用户OPENID】
     * 在进行确认支付之前，需要检查是否已报名过众筹商品，如果报名，那么显示众筹商品到确认支付页面，
     * 如果没有在确认页面可以再次选择众筹商品。增加选择众筹商品的灵活度。
     * 确认支付，通过已权限验证获取的openId传递到微信，进行调用微信支付组件
     * 正式运行,需要注释行:371
     */
    public void settle() {
        System.out.println("########################   第五步【获取用户OPENID】   ########################");
        String no = this.request.getParameter("no");
        String openId = this.request.getParameter("openId");
        Orders findOrders = this.ordersService.findByNo(no);
        if (findOrders == null) {
            this.request.setAttribute("status", "0");
            this.request.setAttribute("message", "订单不存在");
        } else {
            HttpSession session = this.request.getSession();
            User loginUser = (User) session.getAttribute("loginUser");
            if ((loginUser == null) || (loginUser.getId() == null)) {
                this.request.setAttribute("status", "0");
                this.request.setAttribute("message", "您未登陆或者登陆失效，请重新登陆");
            } else {

                //=========================  调用支付内嵌组件 开始    =========================
                Configure config = new Configure();
                String orderNo = no;// 订单号
                String body = findOrders.getProductName();//商品名称
                String total_fee = String.valueOf((int) (findOrders.getMoney() * 100));//request.getParameter("total_fee");// 订单金额，转换公式：金额  x 100，默认1为0.01元
                //total_fee = "1";
                //测试开启
                //openId = config.getOpenId();
                String appId = config.getAppid();

                String mchId = config.getMchid();
                String spbill_create_ip = request.getRemoteAddr();
                String notifyUri = config.getNotifyUri();
                String prepay_id = "";
                String nonce_str = "";
                //===============  之上获取openid可以单独处理，根据长时间测试openid是特定的
                UnifiedOrderReqData reqData =
                        new UnifiedOrderReqData.UnifiedOrderReqDataBuilder(appId, mchId, body, orderNo, Integer.valueOf(total_fee), spbill_create_ip, notifyUri, "JSAPI").setOpenid(openId).build();
                nonce_str = reqData.getNonce_str();
                try {
                    Map<String, Object> resultMap = WxPayApi.UnifiedOrder(reqData);
                    prepay_id = (String) resultMap.get("prepay_id");
                    if (prepay_id == null || prepay_id.equals("")) {
                        request.setAttribute("ErrorMsg", "统一支付接口获取预支付订单出错");
                        response.sendRedirect("/user/error.jsp");
                    }
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                SortedMap<String, String> finalpackage = new TreeMap<String, String>();
                String appid2 = appId;
                String timestamp = Sha1Util.getTimeStamp();
                String nonceStr2 = nonce_str;
                String prepay_id2 = "prepay_id=" + prepay_id;
                String packages = prepay_id2;
                finalpackage.put("appId", appid2);
                finalpackage.put("timeStamp", timestamp);
                finalpackage.put("nonceStr", nonceStr2);
                finalpackage.put("package", packages);
                finalpackage.put("signType", "MD5");
                String finalsign = "";
                finalsign = Signature.createSign(finalpackage);
                System.out.println("/wxpay/pay.jsp?appid=" + appid2 + "&timeStamp="
                        + timestamp + "&nonceStr=" + nonceStr2 + "&package=" + packages
                        + "&sign=" + finalsign);
                if (!response.isCommitted()) {
                    Map map = new HashMap();
                    map.put("appId", appid2);
                    map.put("timeStamp", timestamp);
                    map.put("nonceStr", nonceStr2);
                    map.put("package", packages);
                    map.put("signType", "MD5");
                    map.put("paySign", finalsign);
                    String json = JSONObject.toJSONString(map);
                    request.setAttribute("jsonData", json);
                    //=========================  调用支付内嵌组件 结束    =========================


                    this.request.setAttribute("orders", findOrders);
                    try {
                        //this.request.getRequestDispatcher("settle.jsp").forward(this.request, this.response);

                        //跳转确认页面
                        this.request.getRequestDispatcher("/user/settle.jsp").forward(
                                this.request, this.response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 【第六步】 完成微信支付后通知
     *
     * @throws IOException
     */
    public void wxPayNotify() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream) request.getInputStream()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        //sb为微信返回的xml

        PayNotifyTemplate payNotifyTemplate = new PayNotifyTemplate(sb.toString());
        String responseXml = payNotifyTemplate.execute(new PaySuccessCallBack() {
            @Override
            public void onSuccess(PayNotifyData data) {
                String sql = "INSERT INTO pay_record(totalFee,timeEnd,transactionId,outTradeNo,resultCode,returnCode,bankType,cashFee,feeType,isSubscribe,nonceStr,SIGN,tradeType,createDate,deleted)" +
                        " VALUES('" + data.getTotal_fee() + "','" + data.getTime_end() + "','" + data.getTransaction_id() + "','" + data.getOut_trade_no() + "','" + data.getResult_code() + "','" + data.getReturn_code() + "'," +
                        "'" + data.getBank_type() + "','" + data.getCash_fee() + "','" + data.getCash_fee_type() + "','" + data.getIs_subscribe() + "','" + data.getNonce_str() + "','" + data.getSign() + "','" + data.getTrade_type() + "','" + CommonUtil.createDate() + "',0)";
                try {
                    Long id = (Long) JdbcHelper.insertWithReturnPrimeKey(sql);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    /**
     * 【第七步】完成支付
     */
    public void wxFinish() {
        String no = this.request.getParameter("no");

        Orders order = this.ordersService.findByNo(no);
        if (order == null) {
            this.request.setAttribute("status", "0");
            this.request.setAttribute("message", "订单不存在");
        } else {
            HttpSession session = this.request.getSession();
            User loginUser = (User) session.getAttribute("loginUser");
            if ((loginUser == null) || (loginUser.getId() == null)) {
                this.request.setAttribute("status", "0");
                this.request.setAttribute("message", "您未登陆或者登陆失效，请重新登陆");
            } else {
                //设置订单支付成功状态
                order.setStatus(1);

                String productId = order.getProductId();
                Product product = this.productService.findById(Product.class, Integer.valueOf(productId));
                int inventory = product.getInventory();
                int productNum = order.getProductNum();
                product.setInventory(inventory - productNum);
                this.productService.saveOrUpdate(product);

                Date date = new Date();
                String contacts = "";
                String address = "";
                if (loginUser.getContacts() != null) {
                    contacts = loginUser.getContacts();
                }
                if (loginUser.getAddress() != null) {
                    address = loginUser.getAddress();
                }
                String summary = "收货信息：<br/>" + "联系人:" + contacts + "<br/>联系地址:" + address;
                order.setSummary(summary);
                order.setPayDate(date);

                this.ordersService.saveOrUpdate(order);
                //针对抽奖商品计数
                try {
                    this.request.getRequestDispatcher("/user/index.jsp").forward(
                            this.request, this.response);
                } catch (ServletException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 非微信支付付款方式
     */
    public void confirmPay() {
        String no = this.request.getParameter("no");
        Orders findOrders = this.ordersService.findByNo(no);
        HttpSession session = this.request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        String title = "订单【" + no + "】，购买商品：" + findOrders.getProductName();
        String money = String.valueOf(findOrders.getMoney());
        User findUser = (User) this.userService.findById(User.class,
                loginUser.getId().intValue());
        //修改订单信息
        String summary = "收货信息：<br/>" + "联系人:" + findUser.getContacts() != null ? findUser.getContacts() : "" + "<br/>联系地址:" + findUser.getAddress() != null ? findUser.getAddress() : "";
        findOrders.setSummary(summary);
        findOrders.setStatus(6);
        findUser.setStatusDate(new Date());
        findOrders.setPayDate(new Date());

        this.ordersService.saveOrUpdate(findOrders);
        String note = "请不要修改付款说明里的内容,否则将无法自动完成订购";
        this.request.setAttribute("status", 6);//等待确认
        this.request.setAttribute("title", title);
        this.request.setAttribute("money", money);
        this.request.setAttribute("note", note);
        try {
            this.request.getRequestDispatcher("alipay.jsp").forward(this.request,
                    this.response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 卡密支付付款方式
     */
    public void pay() {
        String no = this.request.getParameter("no");
        Orders findOrders = this.ordersService.findByNo(no);
        HttpSession session = this.request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");

        JSONObject json = new JSONObject();
        if ((loginUser == null) || (loginUser.getId() == null)) {
            json.put("status", "0");
            json.put("message", "您未登陆或者登陆失效，请重新登陆");
            json.put("href", "../login.jsp");
        } else {
            User findUser = (User) this.userService.findById(User.class,
                    loginUser.getId().intValue());
            if (findOrders == null) {
                json.put("status", "0");
                json.put("message", "订单不存在");
            } else if (findOrders.getUser().getId() != findUser.getId()) {
                json.put("status", "0");
                json.put("message", "没有权限");
            } else if (findUser.getBalance().doubleValue() < findOrders
                    .getMoney().doubleValue()) {
                json.put("status", "0");
                json.put("message", "余额不足，请先充值");
            } else if (findOrders.getStatus().intValue() == 1) {
                json.put("status", "0");
                json.put("message", "该订单已付款，请不要重复提交支付");
            } else {
                /*List<Kami> kamiList = this.kamiService.list(
				"from Kami where deleted=0 and status=0 and product.id="
						+ findOrders.getProductId(), 0, findOrders
						.getProductNum().intValue(), new Object[0]);
				if (kamiList.size() < findOrders.getProductNum().intValue()) {
					json.put("status", "0");
					json.put("message", "库存不足，请联系管理员");
				} */
                String productId = findOrders.getProductId();
                Product product = this.productService.findById(Product.class, Integer.valueOf(productId));
                int inventory = product.getInventory();
                int productNum = findOrders.getProductNum();
                if (inventory < productNum) {
                    json.put("status", "0");
                    json.put("message", "库存不足，请联系管理员");
                } else {
                    findUser.setBalance(Double.valueOf(findUser.getBalance()
                            .doubleValue()
                            - findOrders.getMoney().doubleValue()));
                    if (findUser.getStatus().intValue() == 0) {
                        findUser.setStatus(Integer.valueOf(1));
                        findUser.setStatusDate(new Date());
                    }
                    this.userService.saveOrUpdate(findUser);
                    product.setInventory(inventory - productNum);
                    this.productService.saveOrUpdate(product);
                    findOrders.setStatus(Integer.valueOf(1));
                    Date date = new Date();
                    String summary = "收货信息：<br/>" + "联系人:" + findUser.getContacts() + "<br/>联系地址:" + findUser.getAddress();
					/*String summary = "卡密信息:<br/>";
					for (Kami kami : kamiList) {
						summary = summary + "卡号:" + kami.getNo() + ",密码:"
								+ kami.getPassword() + "<br/>";
						kami.setSaleTime(date);
						kami.setOrdersNo(findOrders.getNo());
						kami.setStatus(Integer.valueOf(1));
						this.kamiService.saveOrUpdate(kami);
					}*/
                    findOrders.setSummary(summary);
                    findOrders.setPayDate(date);
                    this.ordersService.saveOrUpdate(findOrders);

                    Financial financial = new Financial();
                    financial.setType(Integer.valueOf(0));
                    financial.setMoney(Double.valueOf(-findOrders.getMoney()
                            .doubleValue()));
                    financial.setNo(String.valueOf(System.currentTimeMillis()));

                    financial.setOperator(loginUser.getName());

                    financial.setUser(findUser);

                    financial.setCreateDate(new Date());
                    financial.setDeleted(false);

                    financial.setBalance(findUser.getBalance());
                    financial.setPayment("余额付款");
                    financial.setRemark("购买" + findOrders.getProductName());
                    this.financialService.saveOrUpdate(financial);
                    Config findConfig = (Config) this.configService.findById(
                            Config.class, 1);

                    String levelNos = findUser.getSuperior();
                    if (!StringUtils.isEmpty(levelNos)) {
                        String[] leverNoArr = levelNos.split(";");
                        int i = leverNoArr.length - 1;
                        for (int j = 1; i > 0; j++) {
                            if (!StringUtils.isEmpty(leverNoArr[i])) {
                                User levelUser = this.userService
                                        .getUserByNo(leverNoArr[i]);
                                if (levelUser != null) {
                                    double commissionRate = 0.0D;
                                    if (j == 1)
                                        commissionRate = findConfig
                                                .getFirstLevel().doubleValue();
                                    else if (j == 2)
                                        commissionRate = findConfig
                                                .getSecondLevel().doubleValue();
                                    else if (j == 3) {
                                        commissionRate = findConfig
                                                .getThirdLevel().doubleValue();
                                    }

                                    double commissionNum = findOrders
                                            .getMoney().doubleValue()
                                            * commissionRate;
                                    levelUser.setCommission(Double
                                            .valueOf(levelUser.getCommission()
                                                    .doubleValue()
                                                    + commissionNum));
                                    this.userService.saveOrUpdate(levelUser);

                                    Commission commission = new Commission();
                                    commission.setType(Integer.valueOf(1));
                                    commission.setMoney(Double
                                            .valueOf(commissionNum));
                                    commission.setNo(String.valueOf(System
                                            .currentTimeMillis()));

                                    commission.setOperator(loginUser.getName());

                                    commission.setUser(levelUser);

                                    commission.setCreateDate(date);
                                    commission.setDeleted(false);
                                    commission.setLevel(Integer.valueOf(j));
                                    commission.setRemark("第" + j + "级用户:编号【"
                                            + loginUser.getNo() + "】购买商品奖励");
                                    this.commissionService
                                            .saveOrUpdate(commission);
                                }
                            }
                            i--;
                        }

                    }

                    json.put("status", "1");
                    json.put("message", "付款成功");
                    json.put("no", findOrders.getNo());
                }
            }
        }

        PrintWriter out = null;
        try {
            out = this.response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.print(json.toString());
        out.flush();
        out.close();
    }

    public void detail() {
        String no = this.request.getParameter("no");
        Orders findOrders = this.ordersService.findByNo(no);
        if (findOrders == null) {
            this.request.setAttribute("status", "0");
            this.request.setAttribute("message", "订单不存在");
        } else {
            HttpSession session = this.request.getSession();
            User loginUser = (User) session.getAttribute("loginUser");
            if (findOrders.getUser().getId() != loginUser.getId()) {
                this.request.setAttribute("status", "0");
                this.request.setAttribute("message", "没有权限");
            } else {
                this.request.setAttribute("orders", findOrders);
                try {
                    this.request.getRequestDispatcher("ordersDetail.jsp")
                            .forward(this.request, this.response);
                } catch (ServletException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void queryOrderInfo() {
        String no = this.request.getParameter("no");
        Orders orderInfo = this.ordersService.findByNo(no);
        String repDate = "";
        PrintWriter out = null;
        try {
            out = this.response.getWriter();
            if (orderInfo == null) {
                repDate = BjuiJson.json("9999", "订单不存在", "", "", "",
                        "", "", "");
            } else{
                Map repMap = new HashMap();
                repMap.put("orderNo", orderInfo.getNo());
                repMap.put("money", orderInfo.getMoney());
                repMap.put("createDate", orderInfo.getCreateDate());
                String status = "待付款";
                if (orderInfo.getStatus() == 0){
                    status = "待付款";
                }else if (orderInfo.getStatus() == 1){
                    status = "已付款";
                }else {
                    status = "待系统确认";
                }
                repMap.put("status", status);
                repDate = BjuiJson.jsonObj("0000","成功",repMap);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        out.print(repDate);
        out.flush();
        out.close();
    }

    public void  updateOrderStatus(){
        String no = this.request.getParameter("no");
        Orders orderInfo = this.ordersService.findByNo(no);
        String repDate = "";
        PrintWriter out = null;
        try {
            out = this.response.getWriter();
            if (orderInfo == null) {
                repDate = BjuiJson.json("9999", "订单不存在", "", "", "",
                        "", "", "");
            } else{
                HttpSession session = this.request.getSession();
                User loginUser = (User) session.getAttribute("loginUser");
                if ((loginUser == null) || (loginUser.getId() == null)) {
                    repDate = BjuiJson.json("9999", "您未登陆或者登陆失效，请重新登陆", "", "", "",
                            "", "", "");
                } else {

                    User findUser = (User) this.userService.findById(User.class,
                            orderInfo.getUser().getId());
                    findUser.setStatus(1);
                    boolean result1 = this.userService.saveOrUpdate(findUser);

                    //设置订单支付成功状态
                    orderInfo.setStatus(1);
                    String productId = orderInfo.getProductId();
                    Product product = this.productService.findById(Product.class, Integer.valueOf(productId));
                    int inventory = product.getInventory();
                    int productNum = orderInfo.getProductNum();
                    product.setInventory(inventory - productNum);
                    this.productService.saveOrUpdate(product);

                    Date date = new Date();
                    orderInfo.setPayDate(date);

                    boolean result = this.ordersService.saveOrUpdate(orderInfo);
                     if (result) {
                         repDate = BjuiJson.json("200", "修改成功", "", "", "",
                                "true", "", "");
                    } else
                        repDate = BjuiJson.json("300", "修改失败", "", "", "", "",
                                "", "");

            }
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        out.print(repDate);
        out.flush();
        out.close();
    }


    public void indexList() {
        String pStr = this.request.getParameter("p");
        int p = 1;
        if (!StringUtils.isEmpty(pStr)) {
            p = Integer.parseInt(pStr);
        }

        String type = this.request.getParameter("type");
        HttpSession session = this.request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        String countHql = "select count(*) from Orders where deleted=0 and user.id="
                + loginUser.getId();
        String hql = "from Orders where deleted=0 and user.id="
                + loginUser.getId();
        if (("0".equals(type)) || ("1".equals(type))) {
            countHql = countHql + " and status=" + type;
            hql = hql + " and status=" + type;
        }
        hql = hql + " order by id desc";

        int count = 0;
        count = this.ordersService.getTotalCount(countHql, new Object[0]);
        PageModel pageModel = new PageModel();
        pageModel.setAllCount(count);
        pageModel.setCurrentPage(p);
        List ordersList = this.ordersService.list(hql, pageModel.getStart(),
                pageModel.getPageSize(), new Object[0]);
        JSONObject json = new JSONObject();
        if (ordersList.size() == 0) {
            json.put("status", "0");

            json.put("isNextPage", "0");
        } else {
            json.put("status", "1");
            if (ordersList.size() == pageModel.getPageSize()) {
                json.put("isNextPage", "1");
            } else {
                json.put("isNextPage", "0");
            }
            JSONArray listJson = (JSONArray) JSONArray.toJSON(ordersList);
            json.put("list", listJson);
        }
        PrintWriter out = null;
        try {
            out = this.response.getWriter();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        out.print(json);
        out.flush();
        out.close();
    }

    public void info() {
        String idStr = this.request.getParameter("id");
        String callbackData = "";
        PrintWriter out = null;
        try {
            out = this.response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if ((idStr == null) || ("".equals(idStr))) {
                callbackData = BjuiJson.json("300", "参数不能为空", "", "", "", "",
                        "", "");
            } else {
                int id = 0;
                try {
                    id = Integer.parseInt(idStr);
                } catch (Exception e) {
                    callbackData = BjuiJson.json("300", "参数错误", "", "", "", "",
                            "", "");
                }
                Orders findorders = (Orders) this.ordersService.findById(
                        Orders.class, id);
                if (findorders == null) {
                    callbackData = BjuiJson.json("300", "订单不存在", "", "", "",
                            "", "", "");
                } else {
                    this.cfg = new Configuration();

                    this.cfg.setServletContextForTemplateLoading(this.request
                                    .getSession().getServletContext(),
                            "WEB-INF/templates/admin");
                    Map root = new HashMap();
                    root.put("orders", findorders);
                    FreemarkerUtils.freemarker(this.request, this.response,
                            this.ftlFileName, this.cfg, root);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        out.print(callbackData);
        out.flush();
        out.close();
    }

    public void update() {
        PrintWriter out = null;
        try {
            out = this.response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String callbackData = "";
        try {
            if (this.orders == null) {
                callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "",
                        "");
            } else {
                Orders findorders = (Orders) this.ordersService.findById(
                        Orders.class, this.orders.getId().intValue());
                this.orders.setCreateDate(findorders.getCreateDate());
                this.orders.setDeleted(findorders.isDeleted());
                this.orders.setVersion(findorders.getVersion());
                boolean result = this.ordersService.saveOrUpdate(this.orders);

                if (result) {
                    callbackData = BjuiJson.json("200", "修改成功", "", "", "",
                            "true", "", "");
                } else
                    callbackData = BjuiJson.json("300", "修改失败", "", "", "", "",
                            "", "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        out.print(callbackData);
        out.flush();
        out.close();
    }

    /**
     * 系统收到订单金额，即进行分销佣金核算
     */
    public void sysConfirm() {
        PrintWriter out = null;
        try {
            out = this.response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String callbackData = "";
        try {
            String idStr = this.request.getParameter("id");

            if ((idStr == null) || ("".equals(idStr))) {
                callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "",
                        "");
            } else {
                int id = 0;
                try {
                    id = Integer.parseInt(idStr);
                } catch (Exception e) {
                    callbackData = BjuiJson.json("300", "参数错误", "", "", "", "",
                            "", "");
                }
                Orders findOrders = (Orders) this.ordersService.findById(
                        Orders.class, id);
                if (findOrders == null) {
                    callbackData = BjuiJson.json("300", "订单不存在", "", "", "",
                            "true", "", "");
                } else {
                    findOrders.setStatus(1);//系统待确认转变为已付款
                    boolean result = this.ordersService.saveOrUpdate(findOrders);
                    if (result) {
                        callbackData = BjuiJson.json("200", "确认成功", "", "", "",
                                "", "", "");
                    } else {
                        callbackData = BjuiJson.json("300", "确认失败", "", "", "",
                                "", "", "");
                    }
                    //不是购买分销级会员，即可享分级佣金
                    if (findOrders.getMoney() > 1) {
                        HttpSession session = this.request.getSession();
                        User loginUser = (User) session.getAttribute("loginUser");
                        User findUser = (User) this.userService.findById(User.class,
                                loginUser.getId().intValue());
                        Financial financial = new Financial();
                        financial.setType(Integer.valueOf(0));
                        financial.setMoney(Double.valueOf(-findOrders.getMoney()
                                .doubleValue()));
                        financial.setNo(String.valueOf(System.currentTimeMillis()));

                        financial.setOperator(loginUser.getName());

                        financial.setUser(findUser);

                        financial.setCreateDate(new Date());
                        financial.setDeleted(false);

                        financial.setBalance(findUser.getBalance());
                        financial.setPayment("余额付款");
                        financial.setRemark("购买" + findOrders.getProductName());
                        this.financialService.saveOrUpdate(financial);
                        Config findConfig = (Config) this.configService.findById(
                                Config.class, 1);

                        String levelNos = findUser.getSuperior();
                        if (!StringUtils.isEmpty(levelNos)) {
                            String[] leverNoArr = levelNos.split(";");
                            int i = leverNoArr.length - 1;
                            for (int j = 1; i > 0; j++) {
                                if (!StringUtils.isEmpty(leverNoArr[i])) {
                                    User levelUser = this.userService
                                            .getUserByNo(leverNoArr[i]);
                                    if (levelUser != null) {
                                        double commissionRate = 0.0D;
                                        if (j == 1) {
                                            commissionRate = findConfig
                                                    .getFirstLevel().doubleValue();
                                        } else if (j == 2) {
                                            commissionRate = findConfig
                                                    .getSecondLevel().doubleValue();
                                        } else if (j == 3) {
                                            commissionRate = findConfig
                                                    .getThirdLevel().doubleValue();
                                        }

                                        double commissionNum = findOrders
                                                .getMoney().doubleValue()
                                                * commissionRate;
                                        levelUser.setCommission(Double
                                                .valueOf(levelUser.getCommission()
                                                        .doubleValue()
                                                        + commissionNum));
                                        this.userService.saveOrUpdate(levelUser);

                                        Commission commission = new Commission();
                                        commission.setType(Integer.valueOf(1));
                                        commission.setMoney(Double
                                                .valueOf(commissionNum));
                                        commission.setNo(String.valueOf(System
                                                .currentTimeMillis()));

                                        commission.setOperator(loginUser.getName());

                                        commission.setUser(levelUser);

                                        commission.setCreateDate(new Date());
                                        commission.setDeleted(false);
                                        commission.setLevel(Integer.valueOf(j));
                                        commission.setRemark("第" + j + "级用户:编号【"
                                                + loginUser.getNo() + "】购买商品奖励");
                                        this.commissionService
                                                .saveOrUpdate(commission);
                                    }
                                }
                                i--;
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        out.print(callbackData);
        out.flush();
        out.close();

    }

    public void delete() {
        PrintWriter out = null;
        try {
            out = this.response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String callbackData = "";
        try {
            String idStr = this.request.getParameter("id");

            if ((idStr == null) || ("".equals(idStr))) {
                callbackData = BjuiJson.json("300", "参数错误", "", "", "", "", "",
                        "");
            } else {
                int id = 0;
                try {
                    id = Integer.parseInt(idStr);
                } catch (Exception e) {
                    callbackData = BjuiJson.json("300", "参数错误", "", "", "", "",
                            "", "");
                }
                Orders findorders = (Orders) this.ordersService.findById(
                        Orders.class, id);
                if (findorders == null) {
                    callbackData = BjuiJson.json("300", "订单不存在", "", "", "",
                            "true", "", "");
                } else {
                    boolean result = this.ordersService.delete(findorders);
                    if (result)
                        callbackData = BjuiJson.json("200", "删除成功", "", "", "",
                                "", "", "");
                    else
                        callbackData = BjuiJson.json("300", "删除失败", "", "", "",
                                "", "", "");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        out.print(callbackData);
        out.flush();
        out.close();
    }

    public Orders getOrders() {
        return this.orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public String getFtlFileName() {
        return this.ftlFileName;
    }

    public void setFtlFileName(String ftlFileName) {
        this.ftlFileName = ftlFileName;
    }
}
