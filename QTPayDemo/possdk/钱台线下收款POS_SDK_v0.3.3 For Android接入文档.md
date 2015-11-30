钱台线下收款POS_SDK_v0.3 For Android接入文档
=========

标签： 钱台交易云-线下收款

1.开发前准备
---------
    1、获取AppCode，可在钱台交易云后台管理里面获取。
	2、获取token，详情参考Server端文档。
	3、获取qf_token，详情参考Server端文档。
        
2.接入准备
---------

##下载possdk工程##

###导入sdk类库###

####eclipse配置####

复制libs下dspread_android_sdk_2.3.9.jar、volley-14_10_10.jar、possdk_v0.3.jar到你项目libs目录下

复制jniLibs下文件到libs目录下

需要导入appcompat依赖库


####Android Studio配置####

复制libs下dspread_android_sdk_2.3.9.jar、volley-14_10_10.jar、possdk_v0.3.jar到你项目中的libs目录下

复制jniLibs下文件到jniLibs目录下

build.gradle文件添加

```
compile fileTree(dir: 'libs', include: ['*.jar'])
compile 'com.android.support:appcompat-v7:22.2.0'
```

###导入资源文件###

将 SDK 包中的 res 文件夹对应的资源文件拷贝到对应的 res 文件夹下

/anim 

/drawable 

/drawable-hdpi 

/drawable-xhdpi 

/layout 

/values

/values-v21

/xml


#### 注意 ####
资源文件可自行定义，但不可修改文件名称


###修改 AndroidManifest.xml 文件###
在清单文件 AndroidManifest.xml 里加入以下权限、添加 Activity 标签

	<uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_LOCATION_EXTRA_COMMANDS" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.BROADCAST_STICKY" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.DISABLE_KEYGUARD" />
    
    <application>
    
    	<activity
            android:name="com.qfpay.qpossdk.activity.QfTradeCloudActivity"
            android:allowTaskReparenting="true"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:theme="@style/QFBaseTheme">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />

                <data android:scheme="qpos" />
            </intent-filter>
        </activity>


        <activity
            android:name="com.qfpay.qpossdk.activity.ReaderActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:exported="true"
            android:theme="@style/QFBaseTheme" />

        <activity
            android:name="com.qfpay.qpossdk.activity.ChooseDeviceActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:theme="@style/Theme.UMDialog" />

        <activity
            android:name="com.qfpay.qpossdk.activity.ConfirmSignActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:exported="false"
            android:theme="@style/QFBaseTheme"
            android:screenOrientation="landscape" />

        <activity
            android:name="com.qfpay.qpossdk.activity.TradeProcessActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:exported="false"
            android:theme="@style/QFBaseTheme"
            android:screenOrientation="landscape" />

        <activity
            android:name="com.qfpay.qpossdk.activity.MacErrorUpdateActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:theme="@style/Theme.UMDialog" />

        <activity
            android:name="com.qfpay.qpossdk.activity.SuccessResultActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:exported="false"
            android:theme="@style/QFBaseTheme"
            android:screenOrientation="landscape" />

        <activity
            android:name="com.qfpay.qpossdk.activity.FailedResultActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:exported="false"
            android:theme="@style/QFBaseTheme"
            android:screenOrientation="landscape" />

        <activity
            android:name="com.qfpay.qpossdk.activity.UnkonwResultActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:exported="false"
            android:theme="@style/QFBaseTheme"
            android:screenOrientation="landscape" />
        <activity
            android:name="com.qfpay.qpossdk.activity.PaintActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:exported="false"
            android:theme="@style/QFBaseTheme"
            android:screenOrientation="landscape" />

        <activity
            android:name="com.qfpay.qpossdk.activity.CertificatePreviewActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:theme="@style/QFBaseTheme"
            android:screenOrientation="portrait" />
        <activity
            android:name="com.qfpay.qpossdk.activity.CertificateUploadActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:theme="@style/QFBaseTheme"
            android:screenOrientation="portrait"/>
        <activity
            android:name="com.qfpay.qpossdk.activity.ReceiptActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:screenOrientation="landscape"
            android:theme="@style/QFBaseTheme"
            android:windowSoftInputMode="adjustPan" />
        <!-- V0.3.3新增，刷卡撤销 -->
        <activity
            android:name="com.qfpay.qpossdk.activity.RepealOrderActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:theme="@style/QFBaseTheme"/>
     </application>

###App 打包混淆###

在 proguard 文件中加入

```
-keep class org.json.** {*;}
-keep class com.android.volley.** {*;}
-keep class com.dspread.xpos.** {*;}
-dontwarn com.dspread.xpos.**
-keep class android_serialport_api.** {*;}
-dontwarn android_serialport_api.**
-keep class com.qfpay.qpossdk.** { *; }
-keep class net.qfpay.king.android.** { *; }
-dontwarn com.qfpay.qpossdk.**,net.qfpay.king.android.util.**
```

        
3.支付功能调用步骤
---------
 1、参数说明     
        
|字段名          | 字段类型       | 字段含义|
|-------------|-------------|-----------|
|createUserId|String|支付订单创建者id，对应下单接口返回的create_userid字段（必填）|
|payOrderId|String|支付订单id，对应下单接口返回的order_id字段（必填）|
|platform|String|网页端调用时填“1”，App调用时填"2"（必填）|
|qfToken|String|用户订单token，对应下单接口返回的qf_token字段（必填）|
|token|String|钱台交易云用户登录返回的token字段（必填）|
|mobile|String|支付完成用于接收短信收据的手机号（选填）|


2、创建订单信息

```
// 组装PayOrderInfo对象
PayOrderInfo orderInfo = new PayOrderInfo();
orderInfo.createUserId = create_userid;
orderInfo.payOrderId = order_id;
orderInfo.qfToken = qf_token;
orderInfo.token = token;
orderInfo.platform = "2";
orderInfo.mobile = mobile;
```

3、调用SDK支付

```
// 调用sdk进行支付
QfPaySdk.getInstance().payOrder(GoodsListActivity.this, orderInfo, payCallBack);
```

4、支付结果获取和处理

```
// 支付结果获取和处理
// 实际交易结果以服务端返回结果为准
QfPayCallBack payCallBack = new QfPayCallBack() {
	@Override
	public void onPayResultFail() {
		Log.w(TAG, "============交易失败============");
		queryOrder();
	}

	@Override
	public void onPayResultSuccess() {
		Log.w(TAG, "=============交易成功===========");
		queryOrder();
	}

	@Override
	public void onPayResultCancel() {
		Log.w(TAG, "============交易取消============");
		queryOrder();
	}
	
	@Override
	public void onPayResultUnknow() {
		Log.w(TAG, "============交易结果未知============");
		queryOrder();
	}
};

```

##2015-11-30 更新 v0.3.3##
###更新日志:###
1、增加刷卡撤销功能。

2、修复bug。

###撤销功能调用步骤:###

 1、参数说明  

|字段名          | 字段类型       | 字段含义|
|-------------|-------------|-----------|
|createUserId|String|订单创建者id，对应创建撤销订单接口返回的create_userid字段（必填）|
|orderId|String|撤销订单id，对应创建撤销订单接口返回的order_id字段（必填）|
|orderNo|String|对应创建撤销订单接口返回的orderno字段（必填）|
|origOrderNo|String|对应创建撤销订单接口返回的orig_orderno字段（必填）|
|qfToken|String|用户订单token，对应创建撤销订单接口返回的qf_token字段（必填）|
|token|String|钱台交易云用户登录返回的token字段（必填）|

2、创建撤销订单信息

```
// 组装PayOrderInfo对象
RepealOrderInfo repealOrderInfo = new RepealOrderInfo();
repealOrderInfo.createUserId = create_userid;
repealOrderInfo.orderId = order_id;
repealOrderInfo.orderNo = orderno;
repealOrderInfo.origOrderNo = orig_orderno;
repealOrderInfo.qfToken = qf_token;
repealOrderInfo.token = token;
```
3、调用SDK进行撤销

```
// 调用sdk进行撤销
QfPaySdk.getInstance().repealOrder(GoodsListActivity.this, repealOrderInfo, repealCallBack);
```

4、撤销结果获取和处理

```
 /**
  * 实际交易结果以服务端返回结果为准
  * 撤销成功时，queryOrder返回refund 为2
  */
QfPayCallBack repealCallBack = new QfPayCallBack() {
	@Override
	public void onPayResultFail() {
		Log.w(TAG, "============撤销失败============");
		queryReapealOrder();
	}

	@Override
	public void onPayResultSuccess() {
		Log.w(TAG, "=============撤销成功===========");
		queryReapealOrder();
	}

	@Override
	public void onPayResultCancel() {
		Log.w(TAG, "============取消撤销============");
		queryReapealOrder();
	}
	
	@Override
	public void onPayResultUnknow() {
		Log.w(TAG, "============撤销结果未知============");
		queryReapealOrder();
	}
};

```