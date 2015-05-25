## 1.3 修改内容：

### 4.2 支付结果返回 ###

在完成支付后，钱台sdk会finish()掉,同时通过setResult()给出支付结果，您可以对其进行进一步的处理。
在支付结果中，会返回2个变量，分别是交易结果变量 "pay_result" 与 订单信息"pay_order"。

| 字段名 | 类型 | 描述 |
| :---- | :---- | :---- |
| QTConst.PAY_RESULT | int | 交易结果变量 |
| QTConst.PAY_ORDER | Order | 订单信息bean |

参考示例如下：

```java
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
			...
			} else if (resultCode == QTConst.ACTIVITY_RETURN_ERROR) {
			...
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
```
