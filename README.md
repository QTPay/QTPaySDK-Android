# 钱台线上收银台_Android接入文档 #

标签： 钱台交易云-线上收银台

## 1.集成准备   ##

### 1.1 复制资源文件
将resources文件夹中的所有文件夹复制到已有工程文件夹中。

![](http://i.imgur.com/wVMKQeM.png)


### 1.2 安装SDK依赖包 ###
根据需求，选择对应的依赖包复制到libs目录下

#### 1.2.1 支付宝钱包依赖包 ####

非必须，可根据业务需求添加。

路径：\libs\channel_jar\alipay\

#### 1.2.2 微信支付依赖包 ####

非必须，可根据业务需求添加。

路径：\libs\channel_jar\wechat\

#### 1.2.3 钱台SDK依赖包 ####

**必须添加，本依赖包是支付核心。**

路径：\libs\qtsdk_jar\

#### 1.2.4 volley网络库依赖包 ####

必须，若已经有相同文件，请使用压缩包中提供的依赖包。

路径：\libs\dependence_jar\

#### 1.2.5 集成效果 ####
![](http://i.imgur.com/Hm0quH3.png)

### 1.3 文件配置 ###

在AndroidManifest文件中加入如下配置：

(如果复制粘贴到Manifest后出现未知错误，可能是由文档文字格式引起，请按照文档手写以下代码)

```
 <activity
  android:name="com.alipay.sdk.app.H5PayActivity"
  android:configChanges="orientation|keyboardHidden|navigation"
  android:exported="false"
  android:screenOrientation="behind"
  android:windowSoftInputMode="adjustResize|stateHidden" >
 </activity>

  <activity
      android:name="com.qfpay.sdk.activity.CashierActivity"
      android:theme="@android:style/Theme.NoTitleBar" >
  </activity>
  <activity
      android:name="com.qfpay.sdk.activity.UserAccountActivity"
      android:theme="@android:style/Theme.NoTitleBar" >
  </activity>


    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

```


## 2. SDK的基础使用  ##

使用钱台非支付相关的接口，需要实例化QTPayCommon对象，示例如下：

    mqt = QTPayCommon.getInstance(getApplicationContext());


###2.1  配置运行环境
场景描述： sdk提供了 动态配置运行环境的接口，可切换到钱方的生产环境，sandbox，以及QA环境。（默认为生产环境）
```java
mqt.setEnviroment(QTEnviroment.WORK)
```
* ` 方法所属类`: QTPayCommon
* 参数:
  * enum QTEnviroment : 定义当前环境配置的枚举

### 2.2 获取钱台访问token ###

1. 商户app在登陆时，需要从商户自己的server端获取平台访问的token，详情请参考Server端文档。
2. 商户app获取到token后，需要调用`QTPayCommon`的`setUserToken`方法，将获取到的用户token设置到钱台sdk中，调用方法如下：

```
mqt.setUserToken( userToken );  //设置用户平台访问token
```

### 2.3 设置App配置参数 ###

在调用SDK中非`set`方法时需要用到appCode和apiKey，在做微信支付和微信分享时需要用到`wxAppId`,因此在调用这些方法前需要调用`setAppInfo`

| 参数id | 参数名称 |
| :---- | :---- |
|appCode|该参数在钱台交易云注册后可得到|
|apiKey|该参数在钱台交易云注册后可得到|
|wxAppId|由商户在微信平台注册得到|

```
mqt.setAppInfo(appCode,apiKey,wxAppId);
```

### 2.4 预下单并获取订单token (即ordertoken)###

1. 商户app创建订单时，商户server需要同时在钱台进行预下单，获取订单token并返回给商户app端，详情请参考Server端文档。
2. 商户app获取到订单token后，需要调用`QTPayCommon`的`setOutOrderToken`方法，将订单`token`写入钱台sdk，调用方法如下：

```
mqt.setOutOrderToken( orderToken ); //设置订单token
```

### 2.5 请求配置信息 ###

1. 本步骤需要**访问网络**，请在调用请求配置信息的方法前确保网络畅通；
2. 请求配置信息需要调用`QTPayCommon`的`getSettingConfiguration`方法，该方法需要传入1个参数：

| 参数名称 | 描述 |
| :---- | :---- |
|ordertoken|订单token|
| QTCallBack | 回调接口 |

- 参考示例：

```
    mqt.getSettingConfiguration(orderToken , new QTCallBack(){

          @Override
          public void onError(Map<String, String> arg0) {
            ......
          }

          @Override
          public void onSuccess(Map<String, Object> arg0) {
            ......
          }
        });


```


## 3. 订单支付 ##

### 3.1 调起支付界面 ###

1.钱台sdk的支付页面的`activity`的布局文件已经在准备阶段放入`layout`包中，请不要随意改变布局文件的控件id，以免引起错误；
2.在调起钱台页面时，需要在intent中传入一个由QTHolder对象。而QTHolder对象，在构造时需要传递3个参数


| 名称 | 描述 |
| :---- | :---- |
| ServiceType | 此参数为服务类型，暂时支持两种：QTConst.ServerType_PAY（支付） QTConst.ServerType_RECHARGE（充值） |
| goods | `List<Good>`类型 |
| extrainfo | `List<ExtraInfo>`类型 |
| mobile | 订单手机号码，若无请传空字符串|


- Good对象:

| 字段名 | 类型 | 描述 |
| :---- | :---- | :---- |
| good_name | String | 商品名称 |
| good_count | int | 商品数量 |
| good_amount | int | 商品单价 |
| good_des|String|商品描述|


- Extrainfo对象：

| 字段名 | 类型 | 描述 |
| :---- | :---- | :---- |
| title | String | 扩展字段标题 |
| info | String | 扩展字段类型 |

- 参考示例如下：

```java
List<Good> goods;
List<ExtraInfo> extraInfo;
… …
goods = new ArrayList<Good>();
goods.add(new Good("大象", 1, 1));//名称，数量，金额
goods.add(new Good("辛太急", 2, 1000));

extraInfo = new ArrayList<ExtraInfo>();
ExtraInfo mobile = new ExtraInfo("电话", "13260612083");
ExtraInfo address = new ExtraInfo("地址", "京信大厦1234");
extraInfo.add(address);
extraInfo.add(mobile);
… …


QTHolder holder = new QTHolder(QTConst.ServerType_PAY, totalAmt, goods, extraInfo,mobile);
Intent intent = new Intent(HomeActivity.this, CashierActivity.class);
intent.putExtra(QTConst.EXTRO,holder));
// startActivityForResult(intent, ConstValue.REQUEST_FOR_CASHIER);
overridePendingTransition(R.anim.qt_slide_in_from_bottom, R.anim.qt_slide_out_to_top);

```


其中，QTConst.ServerType_PAY（int型）表示支付，totalAmt（int型）表示总金额，good（对象）表示商品信息，如：商品名称、商品数量、商品金额，extraInfo（对象）表示附加信息，如：地址、电话。

- 唤起收银台示例图如下：

<img src="http://i.imgur.com/EWVCKuy.png" width= "480" height="800" alt="tupian"/>

- 余额充值支付页面：


```
List<ExtraInfo> extraInfo;
… …
extraInfo = new ArrayList<ExtraInfo>();
ExtraInfo extra = new ExtraInfo("充值奖励", "奖励金额：" + Utils.num2String(Integer.valueOf((String) dataInfo.get("award"))) + "元");
extraInfo.add(extra);
ExtraInfo recharge = new ExtraInfo("余额充值", "充值金额：" + Utils.num2String(rechargeAmt) + "元");
extraInfo.add(recharge);
… …

QTHolder holder = new QTHolder(QTConst.ServerType_RECHARGE, rechargeAmt, null, extraInfo ,mobile);
Intent intent = new Intent(HomeActivity.this, CashierActivity.class);
intent.putExtra(QTConst.EXTRO, holder));//
startActivityForResult(intent, ConstValue.REQUEST_FOR_CASHIER);
overridePendingTransition(R.anim.qt_slide_in_from_bottom, R.anim.qt_slide_out_to_top);

```
其中，QTConst.ServerType_Recharge（int型）表示余额充值，rechargeAmt（int型）表示充值总金额，extraInfo（对象）表示充值信息，余额充值金额：奖励金额。

### 3.2 支付结果返回 ###

在完成支付后，钱台sdk会finish()掉,同时通过setResult()给出支付结果，您可以对其进行进一步的处理，参考示例如下：

```
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == ConstValue.REQUEST_FOR_CASHIER) {
      if (resultCode == Activity.RESULT_OK) {
        int result = data.getExtras().getInt("pay_result");

        switch (result) {
        case QTConst.PAYMENT_RETURN_SUCCESS:
          startActivity(new Intent(HomeActivity.this, PaymentResultActivity.class));
          break;

        case QTConst.PAYMENT_RETURN_FAIL:
          break;
        }

      } else if (resultCode == Activity.RESULT_CANCELED) {
      } else if (resultCode == QTConst.ACTIVITY_RETURN_ERROR) {
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
```



## 4. API调用方法 ##

一些方法调用时需要传递`callBack`对象，`QTCallBack`是一个接口，在构造时需要实现两个方法：
```
     public void  onSuccess(Map<String, Object> dataMap)
```
```
     public void onError(Map<String, String> errorInfo)
```
`onError`回调会回传一个`errorInfo`的`Map`对象

`errorInfo` 格式：
```
    {
    respCode = "xxxx",
    resperr = "错误信息"
    }
```

###4.1 初始化配置接口

####4.1.1 获取用户token

平台方client在获取到平台访问token之后，需要将用户token传递给SDK。

```
    public void setUserToken(String token)
```

- 方法所属类：`QTPayCommon`
- 参数：

```
    token：平台方client获取的钱台访问token。
```

#### 4.1.2 设置APP配置参数 ####

```
  public void setAppInfo(String appCode, String apiKey, String wxAppId)
```

- 方法所属类：QTPayCommon

- 参数：
```
  appCode：该参数在钱台注册得到
  apiKey：该参数在钱台注册得到
  wxAppId：该参数在微信平台申请得到
```


###4.2 预下单

在使用钱台SDK下单前，需要把平台方client创建的订单号（钱台外部订单号）传给钱台SDK。

```
  public void setOutOrderToken(String token)
```

- 方法所属类：`QTPayCommon`
- 参数：

```
  token：平台方client预下单订单号。
```

###4.3 获取支付信息配置

场景：消费者在预下单成功之后，会根据之前获取的token，配置消费者账户信息和收银台设置信息。

#### 4.3.1 获取消费者账户信息

消费者下单开发者需要获取消费者可用的余额、积分、优惠券信息。

```
    public void getCustomerInfo(String amt , Int[] type, final QTCallBack callBack)
```
- 方法所属类：`QTPayCommon`
- 参数：

| 名称 | 描述 |
| :---- | :---- |
| amt|订单金额，单位分，若传订单金额，则查询的优惠券是订单可用的
|type|获取用户信息的内容，new int[] { QTConst.CustomerInfo_Point, QTConst.CustomerInfo_Coupon, QTConst.CustomerInfo_Balance };
| callback|获取配置参数结果的回调 |


* type描述：

 * 如果需要得到积分信息，则在数组里面加入 QTConst.CustomerInfo_Point
 * 如果需要得到优惠券信息，则在数组里面加入 QTConst.CustomerInfo_ Coupon
    * 如果需要得到余额信息，则在数组里面加入 QTConst.CustomerInfo_ Balance



- 回调：

```
  onSuccess( Map<String, Object> dataMap )
```

- dataMap格式：

```
    {
       Customer_info  =  CustomerInfo对象
    }
```

* CustomerInfo对象：

| 字段名 | 类型 | 备注 |
| :---- | :---- | :---- |
| point  | int | 积分 |
| conpons | List<Coupon> | 可用优惠券列表 |
| balance | int | 余额，单位分 |


* Coupon对像：


| 字段名 | 类型 | 备注 |
| :---- | :---- | :---- |
| amt | int | 金额，单位分 |
| title | String | 优惠券名称 |
| mchnt_name | String | 商户名称 |
| user_rule | String | 使用规则 |
| id | String | 优惠券id |
| coupon_code | String | 优惠券码 |
| status | int | 优惠券状态 1：未绑定 2：已绑定 |
| app_code | int | appCode |
| type | int | 优惠券类型 1：满减 2：代金券 |
| start_time | String | 发放生效时间 |
| content | String | 描述信息 |
| create_time | String | 创建时间 |
| expire_time | String | 发放过期时间 |



####4.3.2 获取订单支付配置信息

- 前提：调用`setUserToken`方法，将用户token传递给SDK

``` java
  public void getSettingConfiguration(final String orderToken ,final QTCallBack callBack)
```

- 方法所属类：`QTPayCommon`
- 参数：
 | 名称 | 描述 |
| :---- | :---- |
|orderToken| 预下单获取到的订单token|
|callback|获取配置参数结果的回调|


- 回调：

```
  onSuccess( Map<String, Object> dataMap )
```

- dataMap格式：

```
    {
      payment_list  =  [ 1 , 2 ] ( value类型：ArrayList<String> ) ，
      opration_list = ［ balance , coupon ］( value类型：ArrayList<String> )
  }

```

此处1代表：支付宝支付，2代表：微信支付



####4.3.3 获取充值返现金额

应用场景：消费者在充值时，如果充值金额达到一定数额，会有一定数额的返现。(需要提前在钱台宝管理后台添加配置）
```
    public void matchRule(String rechargeAmt, final QTCallBack callBack)
```

* 方法所属类：`QTPayCommon`
* 参数：
 * `rechargeAmt`：充值金额，单位分
 * `callback`：获取配置参数结果的回调
* 回调：

```
  onSuccess( Map<String, Object> dataMap )
```
- dataMap格式：

```
  {
    award  =  xxxx （充值返现金额）
    }
```

#### 4.3.4 获取所有可用充值返现规则 ####

应用场景：用户充值时，将提示用户可用的所有充值返现规则，以引导用户选择合适的充值金额，该接口可得到所有可用充值返现规则。
```
public void queryRule(String balanceId, final QTCallBack callBack)
```

- 方法所属类：`QTPayCommon`
- 参数：

```
  balanceId：余额充值规则id,不传则表示查询平台所有的余额充值规则，传，则查指定的充值规则
```
- 回调：

```
    onSuccess( Map<String, Object> dataMap )
```

- dataMap格式：

```
    {
      balances：List<Balance> 余额充值列表
  }
```
- balances 对象：

| 字段名 | 类型 | 备注 |
| :---- | :---- | :----|
| id | String | 余额充值id |
| title | String | 余额充值标题 |
| type | int | 余额充值类型 |
| status | int | 余额充值状态 |
| create_time | String | 余额充值规则创建时间 |
| update_time | String | 余额充值规则最后一次修改时间 |
| start_time | String | 余额充值规则生效的起始时间 |
| expire_time | String | 余额充值规则的结束时间 |
| amt | int | 余额充值规则充值金额 |
| award | int | 余额充值规则奖励金额 |
| mchnt_name | String | 创建余额充值规则的开发者名称 |
| content | String | 余额充值规则的说明 |


###4.4 下单及支付

####4.4.1 创建订单

1. 预下单之后，支付之前先就是创建订单阶段。
2. 使用支付宝或者微信支付之前都需要通过钱台SDK进行创建订单，订单创建成功之后才能完成支付。
3. 如果订单需要支付的金额为0（使用余额或优惠券抵消），则订单创建之后默认为支付成功。
4. 如果支付的时候同时使用了余额和优惠券，优先计算优惠券。

####4.4.1.1 交易类型订单

```
   public void createOrder(Order order, final QTCallBack callBack)
```

* 方法所属类：`QTPayCommon`
* 参数：
 * order：订单对象
 * callback：获取配置参数结果的回调

- Order对象：

| 字段名 | 类型 | 描述 | 是否必传 |
| :---- | :---- | :---- | :---- |
| order_token | String | 外部订单号 |   是   |
| total_amt | String | 总金额，单位分 |  是   |
| balance_amt | String | 使用余额的金额，单位分 |  否   |
| pay_amt | String | 实际支付金额，单位分 |  否   |
| point_amt | String | 积分支付金额，单位分 |  否   |
| coupon_amt | String | 优惠券支付金额，单位分，如果使用优惠券必传 |  否   |
| coupon_code | String | 优惠券代码，如果使用优惠券必传 |  否   |
| pay_type | String | 支付类型，1：支付宝，2：微信 |  是   |
| goods_name | String | 商品名称或标识 |  是   |
| goods_info | String | 商品描述 |  否   |
| mobile | String | 消费者手机 |  否   |

* 回调：

```
  onSuccess( Map<String, Object> dataMap )
```

- dataMap格式：

```
  {
    order_id  =  订单id
    }

```

#####4.4.1.2 余额充值类型订单

应用场景：余额充值时不能使用余额、积分、优惠券。

```
    public void recharge(String payType, String payAmt, final QTCallBack callBack)
```

 * 方法所属类：`QTPayCommon`
 * 参数：
  * payType：支付类型。1、支付宝支付；2、微信支付
  * payAmt：充值金额，单位分
  * callback：获取配置参数结果的回调
 * 回调：

```
  onSuccess( Map<String, Object> dataMap )
```
- dataMap格式：

```
    {
      order_id  =  订单id
  }

```


####4.4.2 支付

钱台交易云支付业务支持支付宝钱包和微信支付，调起支付前需要创建订单。

#####4.4.2.1 支付宝钱包支付

- 接入须知：使用支付宝钱包支付之前需加入支付宝依赖包。


```
    AlipayQT alipayQT = AlipayQT.init(mContext);
    alipayQT.pay();
```

##### 4.4.2.2 微信支付

- 接入须知：使用微信支付之前需加入微信依赖包。

```
WeChatQT wechatQt = WeChatQT.init(CashierActivity.this);
wechatQt.doWechatPayment(new QTPaymentCallBack() {
    @Override
    public void onPaymentSuccess() {
    }
    @Override
    public void onPaymentFailed(String errorMessage) {
  Toast.makeText(CashierActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }
});

```


####4.4.3 查询支付结果

- 应用场景：消费者使用支付宝或者微信支付完成后，SDK会调用页面的`onResume`方法，就可以查询支付结果。

```
    public void setResult(String result, final QTCallBack callBack)
```

 * `方法所属类`：QTPayCommon
 * `参数`：
  * result：支付宝或者微信SDK支付返回值，该参数只做参考。
  * callback：获取配置参数结果的回调
 * 回调：

```
    onSuccess( Map<String, Object> dataMap )
```

- dataMap格式：

```
    {
  trade_state  =  true ( value类型：Boolean )
  }
```
```
  onSuccess：支付成功，onError：支付失败
```

- 特例：

如果消费者在下单过程中实际支付金额为0，将直接返回支付结果。例如余额完全抵扣或优惠券完全抵扣应付款时，则无需链接支付宝或微信即返回支付成功的结果。

#### 4.4.4 关闭订单 ####


```java
public void closeOrder(String order_id, final QTCallBack callBack)
```

- 方法所属类：`QTPayCommon`
- 参数：
 - order_id: 需要关闭的订单id。
 - Callback: 获取配置参数结果的回调
- 回调：
```
    onSuccess( Map<String, Object> dataMap )
```
- dataMap对象为null

- 补充描述：消费者在创建订单之后会暂时占用已选择的余额和优惠券，下次下单前关闭之前未关闭订单，余额和优惠券就会返还。

###4.5、分享优惠券

场景描述：开发者可以创建可分享的优惠券诱导消费者进行分享。开发者需要提前通过钱台交易云获取分享信息。

####4.5.1 获取分享信息

```
    public void getShareInfo(final QTCallBack callBack)
```

 * `方法所属类`：QTPayCommon
 * `参数`：
  * callback：获取配置参数结果的回调
 * 回调：

```
  onSuccess( Map<String, Object> dataMap )
```
- dataMap为空对象。

####4.5.2 分享过程

**如果要使用微信分享，需提前加载微信依赖包。**
```
    WeChatQT weChatQt = WeChatQT.init(Context);
    boolean shareTemp = weChatQt.share(WeChatQT.WEIXIN_PENGYOUQUAN);
```
- share方法：

* `方法所属类`：WeChatQT
* `参数`：
 * flag：微信分享标识
   * WeChatQT.WEIXIN_PENGYOUQUAN ：分享到朋友圈
   * WeChatQT.WEIXIN  : 分享给微信好友
* 返回值：shareTemp ：boolean值
   * true ：分享成功
   * false ：分享失败